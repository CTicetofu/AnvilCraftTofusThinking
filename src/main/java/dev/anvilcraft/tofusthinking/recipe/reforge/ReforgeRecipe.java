package dev.anvilcraft.tofusthinking.recipe.reforge;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.anvilcraft.lib.v2.util.predicate.ItemIngredientPredicate;
import dev.anvilcraft.tofusthinking.init.recipe.AddonRecipeTypes;
import dev.dubhe.anvilcraft.recipe.anvil.builder.AbstractRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 重铸配方：以槽位 0 中的物品作为激活物，消耗附近（上方 FishTank 或上方掉落物）的原料，
 * 对激活物添加或移除数据组件。
 * <p>
 * 激活物可为具体物品/标签（{@link ItemIngredientPredicate}），也可写字符串 {@code "all"} 表示任意物品。
 * 当配方要求流体时，只能由上方 FishTank 提供，且流体可选是否消耗。
 */
public class ReforgeRecipe implements Recipe<ReforgeRecipe.Input> {
    /**
     * 「不限物品」激活物：不限定物品类型与组件。
     */
    public static final ItemIngredientPredicate ALL =
            new ItemIngredientPredicate(Optional.empty(), 1, DataComponentPredicate.EMPTY, Map.of());

    private static final Codec<ItemIngredientPredicate> ACTIVATOR_CODEC =
            Codec.either(Codec.STRING, ItemIngredientPredicate.CODEC).flatXmap(
                    either -> either.map(
                            name -> "all".equalsIgnoreCase(name) || "any".equalsIgnoreCase(name)
                                    ? DataResult.success(ALL)
                                    : DataResult.error(() -> "Unknown activator alias: '" + name + "' (expected \"all\")"),
                            DataResult::success
                    ),
                    predicate -> DataResult.success(ALL.equals(predicate) ? Either.left("all") : Either.right(predicate))
            );

    private final ItemIngredientPredicate activator;
    private final List<ItemIngredientPredicate> ingredients;
    private final List<String> requirements;
    private final Optional<FluidRequirement> fluid;
    private final DataComponentPatch result;
    private final int maxQuantity;

    public ReforgeRecipe(
            ItemIngredientPredicate activator,
            List<ItemIngredientPredicate> ingredients,
            List<String> requirements,
            Optional<FluidRequirement> fluid,
            DataComponentPatch result,
            int maxQuantity
    ) {
        this.activator = activator == null ? ALL : activator;
        this.ingredients = List.copyOf(ingredients);
        this.requirements = List.copyOf(requirements);
        this.fluid = fluid;
        this.result = result;
        this.maxQuantity = maxQuantity;
    }

    public ItemIngredientPredicate getActivator() {
        return this.activator;
    }

    public List<ItemIngredientPredicate> getReforgeIngredients() {
        return this.ingredients;
    }

    public List<String> getRequirements() {
        return this.requirements;
    }

    public Optional<FluidRequirement> getFluid() {
        return this.fluid;
    }

    public DataComponentPatch getResult() {
        return this.result;
    }

    /**
     * 单次最大转化数量：&lt;= 0 表示不限制（整堆转化）。
     */
    public int getMaxQuantity() {
        return this.maxQuantity;
    }

    /**
     * 计算单次实际转化数量：数量不足上限时转化全部。
     */
    public int getConvertedCount(ItemStack activatorStack) {
        int count = activatorStack.getCount();
        if (this.maxQuantity <= 0) return count;
        return Math.min(count, this.maxQuantity);
    }

    /**
     * 判断当前激活物是否满足所有额外要求（激活物自身的组件要求由 activator 谓词负责）。
     */
    public boolean testRequirements(ItemStack activatorStack) {
        for (String requirement : this.requirements) {
            if (!ReforgePredicates.test(requirement, activatorStack)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算需要从可用物品堆栈中消耗的数量。
     *
     * @param available 可用物品堆栈（与外部来源一一对应）
     * @return 每个堆栈需要消耗的数量，无法满足时返回 null
     */
    public int[] findConsumption(List<ItemStack> available) {
        int[] remaining = new int[available.size()];
        int[] consume = new int[available.size()];
        for (int i = 0; i < available.size(); i++) {
            remaining[i] = available.get(i).getCount();
        }
        for (ItemIngredientPredicate ingredient : this.ingredients) {
            int need = ingredient.count();
            for (int i = 0; i < available.size() && need > 0; i++) {
                if (remaining[i] <= 0) continue;
                if (!ingredient.testIgnoreCount(available.get(i))) continue;
                int take = Math.min(remaining[i], need);
                remaining[i] -= take;
                consume[i] += take;
                need -= take;
            }
            if (need > 0) {
                return null;
            }
        }
        return consume;
    }

    private ItemStack applyResult(ItemStack activatorStack) {
        ItemStack resultStack = activatorStack.copy();
        resultStack.applyComponents(this.result);
        return resultStack;
    }

    @Override
    public boolean matches(Input input, @NotNull Level level) {
        ItemStack activatorStack = input.activator();
        if (activatorStack.isEmpty()) return false;
        if (!this.activator.test(activatorStack)) return false;
        if (!this.testRequirements(activatorStack)) return false;
        if (!this.testFluid(input.fluid())) return false;
        if (this.findConsumption(input.available()) == null) return false;
        // 若结果与激活物完全一致（例如已有该属性），视为无变化，不消耗任何材料。
        return !ItemStack.isSameItemSameComponents(this.applyResult(activatorStack), activatorStack);
    }

    private boolean testFluid(FluidStack available) {
        if (this.fluid.isEmpty()) return true;
        FluidRequirement requirement = this.fluid.get();
        if (available.isEmpty()) return false;
        if (!FluidStack.isSameFluidSameComponents(available, requirement.fluid())) return false;
        return available.getAmount() >= requirement.fluid().getAmount();
    }

    @Override
    public @NotNull ItemStack assemble(Input input, @NotNull HolderLookup.Provider registries) {
        return this.applyResult(input.activator());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return AddonRecipeTypes.REFORGE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return AddonRecipeTypes.REFORGE_TYPE.get();
    }

    /**
     * 配方输入：激活物、附近可用原料，以及上方 FishTank 提供的流体（无则为 {@link FluidStack#EMPTY}）。
     */
    public record Input(ItemStack activator, List<ItemStack> available, FluidStack fluid) implements RecipeInput {
        public Input(ItemStack activator, List<ItemStack> available) {
            this(activator, available, FluidStack.EMPTY);
        }

        @Override
        public @NotNull ItemStack getItem(int index) {
            return index == 0 ? this.activator : ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return 1;
        }
    }

    /**
     * 流体要求：要求 FishTank 中含有指定流体，且可选是否消耗。
     */
    public record FluidRequirement(FluidStack fluid, boolean consume) {
        public static final Codec<FluidRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                FluidStack.CODEC.fieldOf("fluid").forGetter(FluidRequirement::fluid),
                Codec.BOOL.optionalFieldOf("consume", true).forGetter(FluidRequirement::consume)
        ).apply(instance, FluidRequirement::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FluidRequirement> STREAM_CODEC = StreamCodec.composite(
                FluidStack.STREAM_CODEC, FluidRequirement::fluid,
                ByteBufCodecs.BOOL, FluidRequirement::consume,
                FluidRequirement::new
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Serializer implements RecipeSerializer<ReforgeRecipe> {
        private static final MapCodec<ReforgeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ACTIVATOR_CODEC.optionalFieldOf("activator", ALL).forGetter(ReforgeRecipe::getActivator),
                ItemIngredientPredicate.CODEC.listOf().optionalFieldOf("ingredients", List.of())
                        .forGetter(ReforgeRecipe::getReforgeIngredients),
                Codec.STRING.listOf().optionalFieldOf("requirements", List.of())
                        .forGetter(ReforgeRecipe::getRequirements),
                FluidRequirement.CODEC.optionalFieldOf("fluid").forGetter(ReforgeRecipe::getFluid),
                DataComponentPatch.CODEC.fieldOf("result").forGetter(ReforgeRecipe::getResult),
                Codec.INT.optionalFieldOf("max_quantity", 0).forGetter(ReforgeRecipe::getMaxQuantity)
        ).apply(instance, ReforgeRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, ReforgeRecipe> STREAM_CODEC = StreamCodec.composite(
                ItemIngredientPredicate.STREAM_CODEC, ReforgeRecipe::getActivator,
                ItemIngredientPredicate.STREAM_CODEC.apply(ByteBufCodecs.list()), ReforgeRecipe::getReforgeIngredients,
                ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), ReforgeRecipe::getRequirements,
                ByteBufCodecs.optional(FluidRequirement.STREAM_CODEC), ReforgeRecipe::getFluid,
                DataComponentPatch.STREAM_CODEC, ReforgeRecipe::getResult,
                ByteBufCodecs.VAR_INT, ReforgeRecipe::getMaxQuantity,
                ReforgeRecipe::new
        );

        @Override
        public @NotNull MapCodec<ReforgeRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ReforgeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public static class Builder extends AbstractRecipeBuilder<ReforgeRecipe> {
        private ItemIngredientPredicate activator;
        private final List<ItemIngredientPredicate> ingredients = new ArrayList<>();
        private final List<String> requirements = new ArrayList<>();
        private FluidRequirement fluid;
        private final DataComponentPatch.Builder result = DataComponentPatch.builder();
        private int maxQuantity;

        /**
         * 不限物品：任意物品都可作为激活物。
         */
        public Builder activatorAll() {
            this.activator = ALL;
            return this;
        }

        /**
         * 不限物品类型，但要求激活物必须持有指定组件（例如有 {@code fire_resistant} 才可重铸）。
         */
        public <T> Builder activatorAllWithComponent(DataComponentType<T> type, T value) {
            this.activator = new ItemIngredientPredicate(
                    Optional.empty(), 1,
                    DataComponentPredicate.builder().expect(type, value).build(),
                    Map.of()
            );
            return this;
        }

        /**
         * 单次最大转化数量：&lt;= 0 或不调用表示不限制（整堆转化）。
         */
        public Builder maxQuantity(int maxQuantity) {
            this.maxQuantity = maxQuantity;
            return this;
        }

        public Builder activator(ItemLike item) {
            this.activator = ItemIngredientPredicate.of(item).build();
            return this;
        }

        public Builder activator(TagKey<Item> tag) {
            this.activator = ItemIngredientPredicate.of(tag).build();
            return this;
        }

        public Builder activator(ItemIngredientPredicate predicate) {
            this.activator = predicate;
            return this;
        }

        public <T> Builder activatorWithComponent(ItemLike item, DataComponentType<T> type, T value) {
            this.activator = ItemIngredientPredicate.of(item).hasComponents(
                    DataComponentPredicate.builder().expect(type, value).build()
            ).build();
            return this;
        }

        public Builder requires(ItemLike item) {
            this.ingredients.add(ItemIngredientPredicate.of(item).build());
            return this;
        }

        public Builder requires(ItemLike item, int count) {
            this.ingredients.add(ItemIngredientPredicate.of(item).withCount(count).build());
            return this;
        }

        public Builder requires(TagKey<Item> tag) {
            this.ingredients.add(ItemIngredientPredicate.of(tag).build());
            return this;
        }

        public Builder requires(TagKey<Item> tag, int count) {
            this.ingredients.add(ItemIngredientPredicate.of(tag).withCount(count).build());
            return this;
        }

        public Builder requires(ItemIngredientPredicate predicate) {
            this.ingredients.add(predicate);
            return this;
        }

        public Builder require(String predicate) {
            this.requirements.add(predicate);
            return this;
        }

        /**
         * 要求上方 FishTank 含有指定流体并消耗之。
         */
        public Builder fluid(FluidStack fluid) {
            return this.fluid(fluid, true);
        }

        public Builder fluid(FluidStack fluid, boolean consume) {
            this.fluid = new FluidRequirement(fluid, consume);
            return this;
        }

        public Builder fluid(Fluid fluid, int amount) {
            return this.fluid(new FluidStack(fluid, amount), true);
        }

        public Builder fluid(Fluid fluid, int amount, boolean consume) {
            return this.fluid(new FluidStack(fluid, amount), consume);
        }

        public <T> Builder addComponent(DataComponentType<T> type, T value) {
            this.result.set(type, value);
            return this;
        }

        public Builder removeComponent(DataComponentType<?> type) {
            this.result.remove(type);
            return this;
        }

        public Builder result(DataComponentPatch patch) {
            patch.entrySet().forEach(entry -> {
                DataComponentType<?> type = entry.getKey();
                entry.getValue().ifPresentOrElse(value -> this.setUnchecked(type, value), () -> this.result.remove(type));
            });
            return this;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private void setUnchecked(DataComponentType type, Object value) {
            this.result.set(type, value);
        }

        @Override
        public @NonNull ReforgeRecipe buildRecipe() {
            return new ReforgeRecipe(
                    this.activator,
                    this.ingredients,
                    this.requirements,
                    Optional.ofNullable(this.fluid),
                    this.result.build(),
                    this.maxQuantity
            );
        }

        @Override
        public void validate(@NonNull ResourceLocation id) {
            // 未指定激活物时按「不限物品（all）」处理，无需校验。
        }

        @Override
        public @NonNull String getType() {
            return "reforge";
        }

        @Override
        public @NonNull Item getResult() {
            return Items.ANVIL;
        }
    }
}
