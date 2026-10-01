package dev.anvilcraft.tofusthinking.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.client.event.ClientManageHandler;
import dev.anvilcraft.tofusthinking.item.weapon.GemStaff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class GemStaffRenderer extends BlockEntityWithoutLevelRenderer {
    public GemStaffRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }
    public static IClientItemExtensions GEM_STAFF_EXTENSION = new IClientItemExtensions() {
        private final BlockEntityWithoutLevelRenderer renderer = new GemStaffRenderer();
        @Override
        public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return renderer;
        }
    };

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int light, int overlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        poseStack.pushPose();

        BakedModel models = itemRenderer.getItemModelShaper().getModelManager().getModel(ModelResourceLocation.standalone(AnvilCraftTofusThinking.of("item/gem_staff_model")));
        for (var model : models.getRenderPasses(stack, true)) {
            for (var rendertype : model.getRenderTypes(stack, true)) {
                VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(buffer, rendertype, true, stack.hasFoil());
                itemRenderer.renderModelLists(model, stack, light, overlay, poseStack, vertexconsumer);
            }
        }
        int angle = ClientManageHandler.TICK_COUNT % 90 * 4;
        poseStack.scale(0.5F,0.5F,0.5F);

        poseStack.pushPose();
        poseStack.translate(1,2.4F,1);
        poseStack.scale(0.6F,0.6F,0.6F);
        ItemStack head = GemStaff.getHeadItem(stack);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        itemRenderer.renderStatic(head, ItemDisplayContext.FIXED, light, overlay, poseStack, buffer, null, 0);
        poseStack.popPose();

        poseStack.pushPose();
        ItemStack grip = GemStaff.getGripItem(stack);
        poseStack.translate(1,1.9F,1);
        poseStack.scale(0.5F,0.5F,0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-angle));
        float rangeRate = Math.abs(angle % 90 - 45) / 45F;
        float offset = 0.4F + Mth.lerp(rangeRate,0,0.15F);
        poseStack.translate(offset,0,offset);
        itemRenderer.renderStatic(grip, ItemDisplayContext.FIXED, light, overlay, poseStack, buffer, null, 0);
        poseStack.translate(-2 * offset,0,0);
        itemRenderer.renderStatic(grip, ItemDisplayContext.FIXED, light, overlay, poseStack, buffer, null, 0);
        poseStack.translate(0,0,-2 * offset);
        itemRenderer.renderStatic(grip, ItemDisplayContext.FIXED, light, overlay, poseStack, buffer, null, 0);
        poseStack.translate(2 * offset,0,0);
        itemRenderer.renderStatic(grip, ItemDisplayContext.FIXED, light, overlay, poseStack, buffer, null, 0);
        poseStack.popPose();

        poseStack.popPose();
    }
}
