package dev.anvilcraft.tofusthinking.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.anvilcraft.tofusthinking.block.entity.FoodGeneratorBlockEntity;
import dev.dubhe.anvilcraft.client.renderer.blockentity.BaseShowItemRenderer;
import dev.dubhe.anvilcraft.client.support.FluidRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FoodGeneratorRenderer extends BaseShowItemRenderer<FoodGeneratorBlockEntity> {
    public static final float TANK_BORDER = 1 / 16.0F + 0.001F;
    public FoodGeneratorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected @Nullable ItemStack getDisplayItemStack(@NotNull FoodGeneratorBlockEntity blockEntity) {
        return blockEntity.getDisplayItemStack();
    }

    @Override
    protected int getSeed(@NotNull FoodGeneratorBlockEntity blockEntity) {
        return 0;
    }
    @Override
    public void render(
            @NotNull FoodGeneratorBlockEntity be,
            float partialTick,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource buffer,
            int packedLight,
            int packedOverlay
    ) {
        ItemStack stack = getDisplayItemStack(be);
        if (stack != null && !stack.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.586, 0.5);
            poseStack.scale(0.8f, 0.8f, 0.8f);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0f));
            Minecraft.getInstance()
                    .getItemRenderer()
                    .renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, buffer, be.getLevel(), 0);
            poseStack.popPose();
        }
        IFluidHandler handler = be.getFluidHandler();
        FluidStack fluid = ((FluidTank)handler).getFluid();
        if (!fluid.isEmpty()) {
            float fill = (float) fluid.getAmount() / be.MAX_FOOD_VALUE;
            float minY = 0.5F - TANK_BORDER;
            float height = 1 - minY - 2 * TANK_BORDER;
            float maxY = minY + fill * height;
            FluidRenderHelper.INSTANCE.renderFluidBox(fluid, TANK_BORDER, minY, TANK_BORDER, 1 - TANK_BORDER, maxY, 1 - TANK_BORDER, buffer, poseStack, packedLight, true, false);
        }
    }
}
