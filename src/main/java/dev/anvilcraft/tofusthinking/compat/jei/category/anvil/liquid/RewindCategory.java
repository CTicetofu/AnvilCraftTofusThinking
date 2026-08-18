package dev.anvilcraft.tofusthinking.compat.jei.category.anvil.liquid;

import dev.anvilcraft.tofusthinking.block.OriginalConduitBlock;
import dev.anvilcraft.tofusthinking.compat.jei.TofusThinkingJeiPlugin;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.init.recipe.AddonRecipeTypes;
import dev.anvilcraft.tofusthinking.recipe.anvil.RewindRecipe;
import dev.dubhe.anvilcraft.integration.jei.AnvilCraftJeiPlugin;
import dev.dubhe.anvilcraft.integration.jei.category.anvil.liquid.AbstractLiquidCategory;
import dev.dubhe.anvilcraft.integration.jei.drawable.DrawableBlockStateIcon;
import dev.dubhe.anvilcraft.integration.jei.util.JeiRecipeUtil;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;


//很草率，能用就行
public class RewindCategory extends AbstractLiquidCategory<RewindRecipe> {
    public RewindCategory(mezz.jei.api.helpers.IGuiHelper helper) {
        super(
                helper,
                new DrawableBlockStateIcon(
                        Blocks.CAULDRON.defaultBlockState(),
                        AddonBlocks.ORIGINAL_CONDUIT
                                .get()
                                .defaultBlockState()
                                .setValue(BlockStateProperties.WATERLOGGED, true)
                                .setValue(OriginalConduitBlock.OPEN,true)
                ),
                Component.translatable("gui.anvilcraft_tofus_thinking.category.rewind"));
    }

    @Override
    public @NotNull RecipeType<RecipeHolder<RewindRecipe>> getRecipeType() {
        return TofusThinkingJeiPlugin.REWIND;
    }

    @Override
    protected @NotNull BlockState getProcessBlock() {
        return AddonBlocks.ORIGINAL_CONDUIT.getDefaultState().setValue(OriginalConduitBlock.WATERLOGGED,true).setValue(OriginalConduitBlock.OPEN,true);
    }

    @Override
    public void getTooltip(
            @NotNull ITooltipBuilder tooltip,
            @NotNull RecipeHolder<RewindRecipe> recipeHolder,
            @NotNull IRecipeSlotsView recipeSlotsView,
            double mouseX,
            double mouseY
    ) {
        if (mouseX >= 72 && mouseX <= 90 && mouseY >= 34 && mouseY <= 53) {
            tooltip.add(AddonBlocks.ORIGINAL_CONDUIT.get().getName());
            tooltip.add(Component.translatable("gui.anvilcraft_tofus_thinking.category.rewind.need_activated")
                    .withStyle(ChatFormatting.AQUA));
        }
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        AnvilCraftJeiPlugin.addAnvilCauldronCatalysts(registration, TofusThinkingJeiPlugin.REWIND);
        registration.addRecipeCatalyst(new ItemStack(AddonBlocks.ORIGINAL_CONDUIT), TofusThinkingJeiPlugin.REWIND);
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(
                TofusThinkingJeiPlugin.REWIND,
                JeiRecipeUtil.getRecipeHoldersFromType(AddonRecipeTypes.REWIND_TYPE.get())
        );
    }
}
