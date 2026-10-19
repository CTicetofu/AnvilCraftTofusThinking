package dev.anvilcraft.tofusthinking.compat.jei.category.reforge;

import dev.anvilcraft.lib.v2.util.predicate.ItemIngredientPredicate;
import dev.anvilcraft.tofusthinking.block.OriginalConduitBlock;
import dev.anvilcraft.tofusthinking.compat.jei.TofusThinkingJeiPlugin;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.recipe.reforge.ReforgePredicates;
import dev.anvilcraft.tofusthinking.recipe.reforge.ReforgeRecipe;
import dev.dubhe.anvilcraft.client.support.RenderSupport;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.integration.jei.AnvilCraftJeiPlugin;
import dev.dubhe.anvilcraft.integration.jei.drawable.DrawableBlockStateIcon;
import dev.dubhe.anvilcraft.integration.jei.util.JeiFluidUtil;
import dev.dubhe.anvilcraft.integration.jei.util.JeiItemUtil;
import dev.dubhe.anvilcraft.integration.jei.util.JeiRecipeUtil;
import dev.dubhe.anvilcraft.integration.jei.util.JeiRenderHelper;
import dev.dubhe.anvilcraft.integration.jei.util.JeiSlotUtil;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

//是的，主要由大肥鱼完成，当然它听不听的懂和我修正了多少另说
public class ReforgeCategory implements IRecipeCategory<RecipeHolder<ReforgeRecipe>> {
    private static final int WIDTH = 158;
    private static final int HEIGHT = 82;
    private static final int BLOCK_SCALE = 11;

    private static final int INFO_HEIGHT = 10;
    private static final String INFO_KEY_PREFIX = "gui.anvilcraft_tofus_thinking.category.";

    private static final int CONDUIT_X = 22;
    private static final int CONDUIT_Y = 44;
    private static final int FISH_TANK_X = 22;
    private static final int FISH_TANK_Y = 32;

    private static final int BRICK_OFFSET = 8;
    private static final int BRICK_Y = 32;
    private static final int ANVIL_Y = 20;

    private static final int ACTIVATOR_X = 46;
    private static final int ACTIVATOR_Y = 6;
    private static final int INGREDIENT_CX = 46;
    private static final int INGREDIENT_CY = 38;
    private static final int FLUID_X = 86;
    private static final int FLUID_Y = 50;
    private static final int ARROW_X = 92;
    private static final int ARROW_Y = 34;
    private static final int RESULT_X = 118;
    private static final int RESULT_Y = 29;

    private final IDrawable icon;
    private final IDrawable slot;
    private final IDrawable arrow;

    public ReforgeCategory(IGuiHelper helper) {
        this.icon = new DrawableBlockStateIcon(
                ModBlocks.FISH_TANK.getDefaultState(),
                getConduitState());
        this.slot = JeiRenderHelper.getSlotDefault(helper);
        this.arrow = JeiRenderHelper.getArrowOutput(helper);
    }

    private static BlockState getConduitState() {
        return AddonBlocks.ORIGINAL_CONDUIT.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.WATERLOGGED, true)
                .setValue(OriginalConduitBlock.OPEN, true);
    }

    @Override
    public @NotNull RecipeType<RecipeHolder<ReforgeRecipe>> getRecipeType() {
        return TofusThinkingJeiPlugin.REFORGE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("gui.anvilcraft_tofus_thinking.category.reforge");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(
            @NotNull IRecipeLayoutBuilder builder,
            @NotNull RecipeHolder<ReforgeRecipe> holder,
            @NotNull IFocusGroup focuses
    ) {
        ReforgeRecipe recipe = holder.value();
        List<String> requirements = recipe.getRequirements();
        int yOff = yOff(holder);

        IRecipeSlotBuilder activator = builder.addSlot(RecipeIngredientRole.INPUT, ACTIVATOR_X, ACTIVATOR_Y + yOff);
        if (isAny(recipe)) {
            activator.addItemStack(anyItemMarker());
            activator.addRichTooltipCallback((view, tooltip) -> tooltip.add(
                    Component.translatable("jei.anvilcraft_tofus_thinking.reforge.any_item")
                            .withStyle(ChatFormatting.GRAY)));
        } else {
            for (ItemStack stack : recipe.getActivator().getItems()) {
                activator.addItemStack(stack);
            }
        }
        if (!requirements.isEmpty()) {
            activator.addRichTooltipCallback((view, tooltip) -> {
                for (String requirement : requirements) {
                    tooltip.add(ReforgePredicates.describe(requirement).withStyle(ChatFormatting.AQUA));
                }
            });
        }

        List<ItemIngredientPredicate> ingredients = recipe.getReforgeIngredients();
        if (!ingredients.isEmpty()) {
            JeiItemUtil.addInputSlots(builder, ingredients, INGREDIENT_CX, INGREDIENT_CY + yOff);
        }

        recipe.getFluid().ifPresent(requirement -> {
            FluidStack fluid = requirement.fluid();
            IRecipeSlotBuilder fluidSlot = JeiFluidUtil.addFluidSlot(
                    builder, RecipeIngredientRole.INPUT, FLUID_X, FLUID_Y + yOff,
                    16, 16, fluid.getAmount(), false, List.of(fluid));
            if (!requirement.consume()) {
                fluidSlot.addRichTooltipCallback((view, tooltip) -> tooltip.add(
                        Component.translatable("jei.anvilcraft_tofus_thinking.reforge.fluid.not_consumed")
                                .withStyle(ChatFormatting.GRAY)));
            }
        });

        ItemStack result = buildResult(recipe);
        if (!result.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, RESULT_X, RESULT_Y + yOff).addItemStack(result);
        }
    }
    @Override
    public void draw(
            @NotNull RecipeHolder<ReforgeRecipe> holder,
            @NotNull IRecipeSlotsView recipeSlotsView,
            @NotNull GuiGraphics guiGraphics,
            double mouseX,
            double mouseY
    ) {
        ReforgeRecipe recipe = holder.value();
        int yOff = yOff(holder);

        if (yOff > 0) {
            guiGraphics.drawString(
                    Minecraft.getInstance().font,
                    Component.translatable(infoKey(holder)),
                    3, 1, 0x404040, false);
        }

        BlockState brickState = AddonBlocks.STABLE_PRISMARINE_BRICKS.get().defaultBlockState();
        RenderSupport.renderBlock(
                guiGraphics, brickState,
                FISH_TANK_X - BRICK_OFFSET, CONDUIT_Y + yOff + 4, 17, BLOCK_SCALE, RenderSupport.SINGLE_BLOCK);
        RenderSupport.renderBlock(
                guiGraphics, brickState,
                FISH_TANK_X + BRICK_OFFSET, CONDUIT_Y + yOff - 4, 3, BLOCK_SCALE, RenderSupport.SINGLE_BLOCK);
        RenderSupport.renderBlock(
                guiGraphics, Blocks.ANVIL.defaultBlockState(),
                FISH_TANK_X - BRICK_OFFSET, ANVIL_Y + yOff + 4, 19, BLOCK_SCALE, RenderSupport.SINGLE_BLOCK);
        RenderSupport.renderBlock(
                guiGraphics, Blocks.ANVIL.defaultBlockState(),
                FISH_TANK_X + BRICK_OFFSET, ANVIL_Y + yOff - 4, 5, BLOCK_SCALE, RenderSupport.SINGLE_BLOCK);
        RenderSupport.renderBlock(
                guiGraphics, ModBlocks.FISH_TANK.getDefaultState(),
                FISH_TANK_X, FISH_TANK_Y + yOff, 11, BLOCK_SCALE, RenderSupport.SINGLE_BLOCK);
        RenderSupport.renderBlock(
                guiGraphics, getConduitState(),
                CONDUIT_X, CONDUIT_Y + yOff, 0, BLOCK_SCALE, RenderSupport.SINGLE_BLOCK);

        this.slot.draw(guiGraphics, ACTIVATOR_X - 1, ACTIVATOR_Y + yOff - 1);

        List<ItemIngredientPredicate> ingredients = recipe.getReforgeIngredients();
        if (!ingredients.isEmpty()) {
            JeiSlotUtil.drawSlots(guiGraphics, this.slot, ingredients.size(), INGREDIENT_CX - 1, INGREDIENT_CY + yOff - 1);
        }

        if (recipe.getFluid().isPresent()) {
            this.slot.draw(guiGraphics, FLUID_X - 1, FLUID_Y + yOff - 1);
        }

        this.arrow.draw(guiGraphics, ARROW_X, ARROW_Y + yOff);

        if (!buildResult(recipe).isEmpty()) {
            this.slot.draw(guiGraphics, RESULT_X - 1, RESULT_Y + yOff - 1);
        }

        if (recipe.getMaxQuantity() > 0) {
            guiGraphics.drawString(
                    Minecraft.getInstance().font,
                    Component.translatable(
                            "jei.anvilcraft_tofus_thinking.reforge.max_quantity", recipe.getMaxQuantity()),
                    68, RESULT_Y - 18 + yOff, 0x404040, false);
        }
    }

    @Override
    public void getTooltip(
            @NotNull ITooltipBuilder tooltip,
            @NotNull RecipeHolder<ReforgeRecipe> holder,
            @NotNull IRecipeSlotsView recipeSlotsView,
            double mouseX,
            double mouseY
    ) {
        int yOff = yOff(holder);
        if (isBlockHovered(mouseX, mouseY, CONDUIT_X, CONDUIT_Y + yOff)) {
            tooltip.add(AddonBlocks.ORIGINAL_CONDUIT.get().getName());
            tooltip.add(Component.translatable("gui.anvilcraft_tofus_thinking.category.rewind.need_activated")
                    .withStyle(ChatFormatting.AQUA));
        } else if (isBlockHovered(mouseX, mouseY, FISH_TANK_X - BRICK_OFFSET, BRICK_Y + yOff)
                || isBlockHovered(mouseX, mouseY, FISH_TANK_X + BRICK_OFFSET, BRICK_Y + yOff)) {
            tooltip.add(AddonBlocks.STABLE_PRISMARINE_BRICKS.get().getName());
        } else if (isBlockHovered(mouseX, mouseY, FISH_TANK_X, FISH_TANK_Y + yOff)) {
            tooltip.add(ModBlocks.FISH_TANK.get().getName());
        }
    }

    private static String infoKey(RecipeHolder<ReforgeRecipe> holder) {
        return INFO_KEY_PREFIX + holder.id().getPath().replace('/', '.');
    }

    private static int yOff(RecipeHolder<ReforgeRecipe> holder) {
        return I18n.exists(infoKey(holder)) ? INFO_HEIGHT : 0;
    }

    private static boolean isBlockHovered(double mouseX, double mouseY, int centerX, int centerY) {
        return mouseX >= centerX - 10 && mouseX <= centerX + 10
                && mouseY >= centerY - 10 && mouseY <= centerY + 10;
    }

    private static boolean isAny(ReforgeRecipe recipe) {
        return recipe.getActivator().items().isEmpty();
    }

    private static ItemStack anyItemMarker() {
        return new ItemStack(ModBlocks.CHECK_MARK);
    }

    private static ItemStack buildResult(ReforgeRecipe recipe) {
        ItemStack base;
        if (isAny(recipe)) {
            base = anyItemMarker();
        } else {
            ItemStack[] items = recipe.getActivator().getItems();
            if (items.length == 0) return ItemStack.EMPTY;
            base = items[0].copy();
        }
        base.applyComponents(recipe.getResult());
        return base;
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(AddonBlocks.ORIGINAL_CONDUIT), TofusThinkingJeiPlugin.REFORGE);
        registration.addRecipeCatalyst(new ItemStack(AddonBlocks.STABLE_PRISMARINE_BRICKS), TofusThinkingJeiPlugin.REFORGE);
        AnvilCraftJeiPlugin.addAnvilProcessingCatalysts(registration, TofusThinkingJeiPlugin.REFORGE);
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(
                TofusThinkingJeiPlugin.REFORGE,
                JeiRecipeUtil.getRecipeHoldersFromType(dev.anvilcraft.tofusthinking.init.recipe.AddonRecipeTypes.REFORGE_TYPE.get())
        );
    }
}
