package dev.anvilcraft.tofusthinking.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.anvilcraft.tofusthinking.block.OriginalConduitBlock;
import dev.anvilcraft.tofusthinking.block.entity.OriginalConduitBlockEntity;
import dev.anvilcraft.tofusthinking.client.init.AddonModelLayers;
import net.minecraft.client.Camera;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class OriginalConduitRenderer implements BlockEntityRenderer<OriginalConduitBlockEntity> {
    public static final ResourceLocation LOCATION_BLOCKS = InventoryMenu.BLOCK_ATLAS;
    public static final Material SHELL_TEXTURE = new Material(LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("entity/conduit/base"));
    public static final Material ACTIVE_SHELL_TEXTURE = new Material(LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("entity/conduit/cage"));
    public static final Material WIND_TEXTURE = new Material(LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("entity/conduit/wind"));
    public static final Material VERTICAL_WIND_TEXTURE = new Material(
            LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("entity/conduit/wind_vertical")
    );
    public static final Material OPEN_EYE_TEXTURE = new Material(LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("entity/conduit/open_eye"));
    public static final Material CLOSED_EYE_TEXTURE = new Material(
            LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("entity/conduit/closed_eye")
    );
    private final ModelPart eye;
    private final ModelPart wind;
    private final ModelPart shell;
    private final ModelPart cage;
    private final BlockEntityRenderDispatcher renderer;

    public OriginalConduitRenderer(BlockEntityRendererProvider.Context context) {
        this.renderer = context.getBlockEntityRenderDispatcher();
        this.eye = context.bakeLayer(AddonModelLayers.CONDUIT_EYE);
        this.wind = context.bakeLayer(AddonModelLayers.CONDUIT_WIND);
        this.shell = context.bakeLayer(AddonModelLayers.CONDUIT_SHELL);
        this.cage = context.bakeLayer(AddonModelLayers.CONDUIT_CAGE);
    }

    public static LayerDefinition createEyeLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild(
                "eye", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.01F)), PartPose.ZERO
        );
        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    public static LayerDefinition createWindLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("wind", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public static LayerDefinition createShellLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 32, 16);
    }

    public static LayerDefinition createCageLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 32, 16);
    }

    public void render(OriginalConduitBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float f = (float)blockEntity.tickCount + partialTick;
        BlockState state = blockEntity.getBlockState();
        boolean active = blockEntity.isActive() || (state.hasProperty(OriginalConduitBlock.OPEN) && state.getValue(OriginalConduitBlock.OPEN));
        if (!active) {
            float f5 = blockEntity.getActiveRotation(0.0F);
            VertexConsumer vertex1 = SHELL_TEXTURE.buffer(bufferSource, RenderType::entitySolid);
            poseStack.pushPose();
            poseStack.translate(0.495F, 0.495F, 0.495F);
            poseStack.mulPose(new Quaternionf().rotationY(f5 * (float) (Math.PI / 180.0)));
            this.shell.render(poseStack, vertex1, packedLight, packedOverlay);
            poseStack.popPose();
        } else {
            float f1 = blockEntity.getActiveRotation(partialTick) * (180.0F / (float)Math.PI);
            float f2 = Mth.sin(f * 0.1F) / 2.0F + 0.5F;
            f2 = f2 * f2 + f2;
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.3F + f2 * 0.1F, 0.5F);
            Vector3f vector3f = new Vector3f(0.5F, 1.0F, 0.5F).normalize();
            poseStack.mulPose(new Quaternionf().rotationAxis(f1 * (float) (Math.PI / 180.0), vector3f));
            this.cage.render(poseStack, ACTIVE_SHELL_TEXTURE.buffer(bufferSource, RenderType::entityCutoutNoCull), packedLight, packedOverlay);
            poseStack.popPose();
            int i = blockEntity.tickCount / 66 % 3;
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            if (i == 1) {
                poseStack.mulPose(new Quaternionf().rotationX((float) (Math.PI / 2)));
            } else if (i == 2) {
                poseStack.mulPose(new Quaternionf().rotationZ((float) (Math.PI / 2)));
            }

            VertexConsumer vertexconsumer = (i == 1 ? VERTICAL_WIND_TEXTURE : WIND_TEXTURE).buffer(bufferSource, RenderType::entityCutoutNoCull);
            this.wind.render(poseStack, vertexconsumer, packedLight, packedOverlay);
            poseStack.popPose();
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.scale(0.875F, 0.875F, 0.875F);
            poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.PI, 0.0F, (float) Math.PI));
            this.wind.render(poseStack, vertexconsumer, packedLight, packedOverlay);
            poseStack.popPose();
            Camera camera = this.renderer.camera;
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.3F + f2 * 0.1F, 0.5F);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            float f3 = -camera.getYRot();
            poseStack.mulPose(new Quaternionf().rotationYXZ(f3 * (float) (Math.PI / 180.0), camera.getXRot() * (float) (Math.PI / 180.0), (float) Math.PI));
            float f4 = 1.3333334F;
            poseStack.scale(f4, f4, f4);
            this.eye
                    .render(
                            poseStack,
                            (blockEntity.isHunting() ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE).buffer(bufferSource, RenderType::entityCutoutNoCull),
                            packedLight,
                            packedOverlay
                    );
            poseStack.popPose();
        }
    }

    @Override
    public net.minecraft.world.phys.@NotNull AABB getRenderBoundingBox(OriginalConduitBlockEntity blockEntity) {
        net.minecraft.core.BlockPos pos = blockEntity.getBlockPos();
        return new net.minecraft.world.phys.AABB(pos.getX(), pos.getY() - .25, pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.25, pos.getZ() + 1.0);
    }
}
