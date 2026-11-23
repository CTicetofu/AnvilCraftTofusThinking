package dev.anvilcraft.tofusthinking.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.item.tool.PotionJarItem;
import dev.dubhe.anvilcraft.client.support.FluidRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class PotionJarItemRenderer extends BlockEntityWithoutLevelRenderer {
    public PotionJarItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }
    public static final float DISTANCE = 3F/16;
    public static final IClientItemExtensions POTION_JAR_EXTENSION = new IClientItemExtensions() {
        private final BlockEntityWithoutLevelRenderer renderer = new PotionJarItemRenderer();
        @Override
        public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return renderer;
        }
    };
    FluidStack water = new FluidStack(Fluids.WATER,1000);
    @Override
    public void renderByItem(@NonNull ItemStack stack, @NonNull ItemDisplayContext displayContext, PoseStack poseStack, @NonNull MultiBufferSource buffer, int light, int overlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        poseStack.pushPose();
        BakedModel models = itemRenderer.getItemModelShaper().getItemModel(stack);
        for (var model : models.getRenderPasses(stack, true)) {
            for (var rendertype : model.getRenderTypes(stack, true)) {
                VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(buffer, rendertype, true, stack.hasFoil());
                itemRenderer.renderModelLists(model, stack, light, overlay, poseStack, vertexconsumer);
            }
        }
        poseStack.popPose();
        int progress = stack.getOrDefault(AddonComponents.PROGRESS, 0);
        if(progress == 0){return;}
        poseStack.pushPose();
        RenderType renderType = RenderType.translucent();
        VertexConsumer builder = buffer.getBuffer(renderType);
        float minX = DISTANCE + 0.01F;
        float minY = 0.01F;
        float minZ = DISTANCE + 0.01F;
        float maxX = 0.99F - DISTANCE;
        float maxZ = 0.99F - DISTANCE;
        float maxY = progress < 0 ? 0.74F : 0.01F + 0.73F * progress / PotionJarItem.MAX_PROGRESS;
        maxY = Math.min(maxY, 0.74F);

        var renderProps = IClientFluidTypeExtensions.of(water.getFluid());
        final TextureAtlasSprite stillTexture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(renderProps.getStillTexture(water));
        int blockLightIn = (light >> 4) & 0xF;
        int color;
        if(stack.has(DataComponents.POTION_CONTENTS)){
            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
            color = contents == null ? -1 : contents.getColor();
        } else {
            color = renderProps.getTintColor(water);
        }
        int luminosity = Math.max(blockLightIn, water.getFluidType().getLightLevel());
        light = (light & 0xF00000) | luminosity << 4;
        for (Direction side : Direction.values()) {
            boolean positive = side.getAxisDirection() == Direction.AxisDirection.POSITIVE;
            if (side.getAxis().isHorizontal()) {
                if (side.getAxis() == Direction.Axis.X) {
                    FluidRenderHelper.renderStillTiledFace(side, minZ, minY, maxZ, maxY, positive ? maxX : minX,
                            builder, poseStack, light, color, stillTexture);
                } else {
                    FluidRenderHelper.renderStillTiledFace(side, minX, minY, maxX, maxY, positive ? maxZ : minZ,
                            builder, poseStack, light, color, stillTexture);
                }
            } else {
                FluidRenderHelper.renderStillTiledFace(side, minX, minZ, maxX, maxZ, positive ? maxY : minY,
                        builder, poseStack, light, color, stillTexture);
            }
        }
        poseStack.popPose();
    }
}
