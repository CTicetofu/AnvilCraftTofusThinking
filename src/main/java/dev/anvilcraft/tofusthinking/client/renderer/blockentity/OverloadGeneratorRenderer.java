package dev.anvilcraft.tofusthinking.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.block.entity.OverloadGeneratorBlockEntity;
import dev.anvilcraft.tofusthinking.client.init.AddonModelLayers;
import dev.dubhe.anvilcraft.client.renderer.blockentity.PowerProducerRenderer;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;

public class OverloadGeneratorRenderer extends PowerProducerRenderer<OverloadGeneratorBlockEntity> {
    public static final ModelResourceLocation MODEL = ModelResourceLocation.standalone(
            AnvilCraftTofusThinking.of("block/overload_generator_head")
    );
    public static final ResourceLocation LOCATION_BLOCKS = InventoryMenu.BLOCK_ATLAS;
    public static final Material WIND_TEXTURE = new Material(LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("entity/conduit/wind"));
    private final static ResourceLocation armorTexture = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither_armor.png");
    private final ModelPart wind;
    private final SkullModel armorModel;

    public OverloadGeneratorRenderer(BlockEntityRendererProvider.Context context) {
        this.wind = context.bakeLayer(AddonModelLayers.CONDUIT_WIND);
        this.armorModel = new SkullModel(context.bakeLayer(ModelLayers.WITHER_SKULL));
    }

    @Override
    public void render(@NotNull OverloadGeneratorBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        super.render(blockEntity, partialTick, poseStack, buffer, packedLight, packedOverlay);
        poseStack.pushPose();
        float rotation = rotation(blockEntity, partialTick);
        poseStack.translate(0.5F, elevation(), 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotation));
        VertexConsumer vertexconsumer = WIND_TEXTURE.buffer(buffer, RenderType::entityCutoutNoCull);
        this.wind.render(poseStack, vertexconsumer, packedLight, packedOverlay);
        poseStack.scale(-1,-1,-1);
        this.wind.render(poseStack, vertexconsumer, packedLight, packedOverlay);
        if(blockEntity.getOverloadTimes() >= 10){
            poseStack.pushPose();
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            float armorScale = 1.5F;
            poseStack.scale(armorScale, armorScale, armorScale);
            poseStack.translate(0,0.25F,0);
            float f = rotation * 0.2F;
            float xOff = Mth.cos(f * 0.02F) * 3.0F % 1.0F;
            float yOff = f * 0.01F % 1.0F;
            VertexConsumer armorVertex = buffer.getBuffer(RenderType.energySwirl(armorTexture, xOff, yOff));
            this.armorModel.renderToBuffer(poseStack, armorVertex, packedLight, OverlayTexture.NO_OVERLAY, 0x80528B8B);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    @Override
    protected float elevation() {
        return 0.75f;
    }

    @Override
    protected float rotation(OverloadGeneratorBlockEntity blockEntity, float partialTick) {
        return blockEntity.getRotation() + (blockEntity.getOverloadTimes() * 2.5f * partialTick);
    }

    @Override
    protected @NotNull ModelResourceLocation getModel() {
        return MODEL;
    }
}
