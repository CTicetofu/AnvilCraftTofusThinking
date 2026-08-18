package dev.anvilcraft.tofusthinking.recipe.anvil;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.anvilcraft.lib.v2.util.predicate.BlockStatePredicate;
import dev.anvilcraft.lib.v2.util.predicate.ChanceItemStack;
import dev.anvilcraft.lib.v2.util.predicate.ItemIngredientPredicate;
import dev.anvilcraft.tofusthinking.block.OriginalConduitBlock;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.init.recipe.AddonRecipeTypes;
import dev.dubhe.anvilcraft.recipe.anvil.util.WrapUtils;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.AbstractProcessRecipe;
import dev.dubhe.anvilcraft.recipe.component.HasCauldronSimple;
import dev.dubhe.anvilcraft.util.FluidStackPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ConduitBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;


//copy from 本体的部分配方
public class RewindRecipe extends AbstractProcessRecipe<RewindRecipe> {
    public RewindRecipe(List<ItemIngredientPredicate> itemIngredients, List<ChanceItemStack> results, HasCauldronSimple hasCauldron) {
        super(new Property().setItemInputOffset(new Vec3(0.0, -0.375, 0.0))
                .setItemInputRange(new Vec3(0.75, 0.75, 0.75))
                .setInputItems(itemIngredients)
                .setItemOutputOffset(new Vec3(0.0, -0.75, 0.0))
                .setResultItems(results).setCauldronOffset(new Vec3i(0, -1, 0))
                .setHasCauldron(hasCauldron).setBlockInputOffset(new Vec3i(0, -2, 0))
                .setInputBlocks(BlockStatePredicate.builder().of(AddonBlocks.ORIGINAL_CONDUIT.get())
                        .with(OriginalConduitBlock.OPEN, true).with(ConduitBlock.WATERLOGGED,true).build()));
    }

    @Override
    public @NotNull RecipeSerializer<RewindRecipe> getSerializer() {
        return AddonRecipeTypes.REWIND_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<RewindRecipe> getType() {
        return AddonRecipeTypes.REWIND_TYPE.get();
    }

    /**
     * 创建一个构建器实例
     *
     * @return 构建器实例
     */
    public static RewindRecipe.Builder builder() {
        return new RewindRecipe.Builder();
    }

    /**
     * 是否消耗流体
     *
     * @return 如果消耗流体返回true，否则返回false
     */
    public boolean isConsumeFluid() {
        HasCauldronSimple hasCauldron = this.getHasCauldron();
        return hasCauldron.hasFluid() && hasCauldron.consume() > 0;
    }

    /**
     * 是否产生流体
     *
     * @return 如果产生流体返回true，否则返回false
     */
    public boolean isProduceFluid() {
        HasCauldronSimple hasCauldron = this.getHasCauldron();
        return !hasCauldron.transforms().isEmpty();
    }

    /**
     * 回溯配方序列化器
     */
    public static class Serializer implements RecipeSerializer<RewindRecipe> {
        /**
         * 编解码器
         */
        private static final MapCodec<RewindRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(ItemIngredientPredicate.CODEC.listOf().optionalFieldOf("ingredients", List.of()).forGetter(RewindRecipe::getInputItems), ChanceItemStack.CODEC.listOf().optionalFieldOf("results", List.of()).forGetter(RewindRecipe::getResultItems), HasCauldronSimple.CODEC.forGetter(RewindRecipe::getHasCauldron)).apply(instance, RewindRecipe::new));
        /**
         * 流编解码器
         */
        private static final StreamCodec<RegistryFriendlyByteBuf, RewindRecipe> STREAM_CODEC = StreamCodec.composite(ItemIngredientPredicate.STREAM_CODEC.apply(ByteBufCodecs.list()), RewindRecipe::getInputItems, ChanceItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), RewindRecipe::getResultItems, HasCauldronSimple.STREAM_CODEC, RewindRecipe::getHasCauldron, RewindRecipe::new);

        @Override
        public @NotNull MapCodec<RewindRecipe> codec() {
            return RewindRecipe.Serializer.CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, RewindRecipe> streamCodec() {
            return RewindRecipe.Serializer.STREAM_CODEC;
        }
    }

    /**
     * 回溯配方构建器
     */
    public static class Builder extends SimpleAbstractBuilder<RewindRecipe, RewindRecipe.Builder> {
        /**
         * 炼药锅条件构建器
         */
        HasCauldronSimple.Builder hasCauldron = HasCauldronSimple.empty();

        /**
         * 设置流体
         *
         * @param fluid 流体
         * @return 构建器实例
         */
        public RewindRecipe.Builder fluid(Fluid fluid) {
            this.hasCauldron.fluid(fluid);
            return this;
        }

        public RewindRecipe.Builder fluid(Holder<Fluid> fluid) {
            this.hasCauldron.fluid(fluid);
            return this;
        }

        public RewindRecipe.Builder fluid(FluidStackPredicate fluid) {
            this.hasCauldron.fluid(fluid);
            return this;
        }

        public RewindRecipe.Builder fluid(TagKey<Fluid> fluid) {
            this.hasCauldron.fluid(fluid);
            return this;
        }

        /**
         * 设置炼药锅方块
         *
         * @param cauldron 炼药锅方块
         * @return 构建器实例
         */
        public RewindRecipe.Builder fluid(Block cauldron) {
            return this.fluid(BuiltInRegistries.FLUID.get(WrapUtils.cauldron2Fluid(cauldron)));
        }

        /**
         * 设置转换后的流体
         *
         * @param transform 转换后的流体ID
         * @return 构建器实例
         */
        public RewindRecipe.Builder transform(Fluid transform, int produce) {
            this.hasCauldron.transform(transform, produce);
            return this;
        }

        /**
         * 设置转换后的流体
         *
         * @param transform 转换后的流体
         * @return 构建器实例
         */
        public RewindRecipe.Builder transform(Holder<Fluid> transform, int produce) {
            this.hasCauldron.transform(transform, produce);
            return this;
        }

        public RewindRecipe.Builder transform(Block cauldron, int produce) {
            return this.transform(BuiltInRegistries.FLUID.get(WrapUtils.cauldron2Fluid(cauldron)), produce);
        }

        public RewindRecipe.Builder transform(FluidStack transform) {
            this.hasCauldron.transform(transform);
            return this;
        }

        /**
         * 设置消耗量
         *
         * @param consume 消耗量
         * @return 构建器实例
         */
        public RewindRecipe.Builder consume(int consume) {
            this.hasCauldron.consume(consume);
            return this;
        }

        @Override
        protected @NotNull RewindRecipe of(@NotNull List<ItemIngredientPredicate> itemIngredients, @NotNull List<ChanceItemStack> results) {
            return new RewindRecipe(itemIngredients, results, this.hasCauldron.build());
        }

        @Override
        public void validate(@NotNull ResourceLocation id) {
            if (itemIngredients.isEmpty()) {
                throw new IllegalArgumentException("Recipe ingredients must not be empty, RecipeId: " + id);
            }
        }

        @Override
        public @NotNull String getType() {
            return "rewind";
        }

        @Override
        protected RewindRecipe.@NotNull Builder getThis() {
            return this;
        }
    }
}
