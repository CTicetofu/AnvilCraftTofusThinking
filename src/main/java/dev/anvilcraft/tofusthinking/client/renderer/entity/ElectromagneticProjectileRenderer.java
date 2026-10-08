package dev.anvilcraft.tofusthinking.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.anvilcraft.tofusthinking.entity.projectile.ElectromagneticProjectile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ElectromagneticProjectileRenderer extends EntityRenderer<ElectromagneticProjectile> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ResourceLocation.DEFAULT_NAMESPACE,"textures/item/iron_nugget.png");

    public ElectromagneticProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    @Override
    public void render(@NotNull ElectromagneticProjectile entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        ItemStack stack = entity.getItem();
        if (!stack.isEmpty()) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) + 90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
            poseStack.translate(0,0.3f,0);
            poseStack.scale(0.6f, 0.6f, 0.6f);
            Minecraft.getInstance()
                    .getItemRenderer()
                    .renderStatic(stack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, entity.level(), entity.getId());
            poseStack.popPose();
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ElectromagneticProjectile electromagneticProjectile) {
        return TEXTURE;
    }
}
