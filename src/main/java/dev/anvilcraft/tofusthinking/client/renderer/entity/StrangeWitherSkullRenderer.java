package dev.anvilcraft.tofusthinking.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.anvilcraft.tofusthinking.entity.projectile.StrangeWitherSkull;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class StrangeWitherSkullRenderer extends EntityRenderer<StrangeWitherSkull> {
    private static final ResourceLocation WITHER_INVULNERABLE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither_invulnerable.png");
    private static final ResourceLocation WITHER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither.png");
    private final SkullModel model;
    private final SkullModel armorModel;
    private final static ResourceLocation armorTexture = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither_armor.png");

    public StrangeWitherSkullRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SkullModel(context.bakeLayer(ModelLayers.WITHER_SKULL));
        this.armorModel = new SkullModel(context.bakeLayer(ModelLayers.WITHER_SKULL));
    }

    public static LayerDefinition createSkullLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 35).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    protected int getBlockLightLevel(@NotNull StrangeWitherSkull entity, @NotNull BlockPos pos) {
        return 15;
    }

    public void render(StrangeWitherSkull entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        float yRot = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
        float xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        VertexConsumer vertexconsumer = buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.setupAnim(0.0F, yRot, xRot);
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        if (entity.isDangerous()) {
            poseStack.pushPose();
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            float armorScale = 1.15F;
            poseStack.scale(armorScale, armorScale, armorScale);

            float f = (float) entity.tickCount + partialTicks;

            float xOff = Mth.cos(f * 0.02F) * 3.0F % 1.0F;
            float yOff = f * 0.01F % 1.0F;
            VertexConsumer armorVertex = buffer.getBuffer(RenderType.energySwirl(armorTexture, xOff, yOff));
            this.armorModel.setupAnim(0.0F, yRot, xRot);
            this.armorModel.renderToBuffer(poseStack, armorVertex, packedLight, OverlayTexture.NO_OVERLAY, 0x80528B8B);
            poseStack.popPose();
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    public @NotNull ResourceLocation getTextureLocation(StrangeWitherSkull entity) {
        return entity.isDangerous() ? WITHER_INVULNERABLE_LOCATION : WITHER_LOCATION;
    }
}
