package dev.anvilcraft.tofusthinking.compat.jei;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.compat.jei.category.anvil.liquid.RewindCategory;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.recipe.anvil.RewindRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class TofusThinkingJeiPlugin implements IModPlugin {

    public static final RecipeType<RecipeHolder<RewindRecipe>> REWIND =
            createRecipeHolderType("rewind");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return AnvilCraftTofusThinking.of("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registration.addRecipeCategories(new RewindCategory(guiHelper));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {

        registration.addItemStackInfo(
                AddonBlocks.ORIGINAL_CONDUIT.asStack(),
                Component.translatable("jei.anvilcraft_tofus_thinking.info.original_conuit")
        );

        RewindCategory.registerRecipes(registration);
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        RewindCategory.registerRecipeCatalysts(registration);
    }

    private static <R extends net.minecraft.world.item.crafting.Recipe<?>> RecipeType<RecipeHolder<R>> createRecipeHolderType(String name) {
        return RecipeType.createRecipeHolderType(AnvilCraftTofusThinking.of(name));
    }
}
