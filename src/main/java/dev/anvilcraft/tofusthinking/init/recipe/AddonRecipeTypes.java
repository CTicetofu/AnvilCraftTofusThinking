package dev.anvilcraft.tofusthinking.init.recipe;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.recipe.anvil.RewindRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AddonRecipeTypes {
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, AnvilCraftTofusThinking.MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, AnvilCraftTofusThinking.MOD_ID);

    private static <T extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<T>> registerType(String name) {
        return RECIPE_TYPES.register(
                name, () -> new RecipeType<>() {
                    @Override
                    public String toString() {
                        return AnvilCraftTofusThinking.of(name).toString();
                    }
                }
        );
    }

    public static final DeferredHolder<RecipeType<?>, RecipeType<RewindRecipe>> REWIND_TYPE =
            registerType("time_warp");
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RewindRecipe>> REWIND_SERIALIZER =
            RECIPE_SERIALIZERS.register("time_warp", RewindRecipe.Serializer::new);

    public static void register(IEventBus bus) {
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
    }
}
