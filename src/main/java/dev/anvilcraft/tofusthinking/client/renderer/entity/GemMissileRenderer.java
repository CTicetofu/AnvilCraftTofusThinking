package dev.anvilcraft.tofusthinking.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.anvilcraft.tofusthinking.client.renderer.model.GemMissileModel;
import dev.anvilcraft.tofusthinking.client.init.AddonModelLayers;
import dev.anvilcraft.tofusthinking.entity.projectile.GemMissile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class GemMissileRenderer extends EntityRenderer<GemMissile> {
    private final static ResourceLocation armorTexture = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither_armor.png");
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/wind_charge.png");
    private final GemMissileModel model;
    public GemMissileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new GemMissileModel(context.bakeLayer(AddonModelLayers.GEM_MISSILE));
    }

    @Override
    public void render(@NotNull GemMissile entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int light) {
        float f = (float) entity.tickCount + partialTick;
        float xOff = Mth.cos(f * 0.02F) * 3.0F % 1.0F;
        float yOff = f * 0.01F % 1.0F;
        int color = entity.getColor();
        float scale = entity.getScale() / 10F;

        poseStack.pushPose();
        poseStack.scale(scale,scale,scale);
        poseStack.translate(0,0.125F,0);
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.breezeWind(TEXTURE, xOff % 1.0F, 0.0F));
        this.model.setupAnim(entity, 0.0F, 0.0F, f, 0.0F, 0.0F);
        this.model.renderToBuffer(poseStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY,color);
        VertexConsumer armorVertex = buffer.getBuffer(RenderType.energySwirl(armorTexture, xOff, yOff));
        float armorScale = 1.1F;

        poseStack.scale(armorScale, armorScale, armorScale);
        this.model.renderToBuffer(poseStack, armorVertex, light, OverlayTexture.NO_OVERLAY, color);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        this.model.renderToBuffer(poseStack, armorVertex, light, OverlayTexture.NO_OVERLAY, color);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, light);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull GemMissile gemMissile) {
        return TEXTURE;
    }
}
