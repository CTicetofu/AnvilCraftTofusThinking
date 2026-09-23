package dev.anvilcraft.tofusthinking.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.anvilcraft.tofusthinking.item.weapon.OriginalConduitStaff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

public class OriginalConduitStaffRenderer extends ConduitStaffRenderer {
    public OriginalConduitStaffRenderer(){
        super();
    }
    public static IClientItemExtensions ORIGINAL_CONDUIT_STAFF_EXTENSION = new IClientItemExtensions() {
        private final BlockEntityWithoutLevelRenderer renderer = new OriginalConduitStaffRenderer();
        @Override
        public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return renderer;
        }
    };
    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int light, int overlay) {
        super.renderByItem(stack, context, poseStack, buffer, light, overlay);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();

        poseStack.pushPose();
        Item headItem = OriginalConduitStaff.getHeadItemFromItem(stack);
        if(headItem instanceof BlockItem blockItem){
            Block block = blockItem.getBlock();
            poseStack.scale(0.5F,0.5F,0.5F);
            poseStack.translate(0.495,1.745F,0.495);
            poseStack.scale(1.01F,1.01F,1.01F);
            dispatcher.renderSingleBlock(block.defaultBlockState(),poseStack,buffer,light,overlay, ModelData.EMPTY, RenderType.TRANSLUCENT);
        } else {
            ItemStack head = headItem.getDefaultInstance();
            poseStack.translate(0.5,1.125F,0.5);
            poseStack.scale(0.99F,0.99F,0.99F);
            itemRenderer.renderStatic(head, ItemDisplayContext.FIXED, light, overlay, poseStack, buffer, null, 0);
        }
        poseStack.popPose();

        poseStack.pushPose();
        Item gripItem = OriginalConduitStaff.getGripItemFromItem(stack);
        if(gripItem instanceof BlockItem blockItem){
            Block block = blockItem.getBlock();
            poseStack.scale(0.25F,0.25F,0.25F);
            poseStack.translate(1.5,2.65,1.5);
            poseStack.pushPose();
            poseStack.translate(0.2F,0.2F,0.2F);
            poseStack.scale(0.6F,0.6F,0.6F);
            dispatcher.renderSingleBlock(block.defaultBlockState(),poseStack,buffer,light,overlay, ModelData.EMPTY, RenderType.SOLID);
            poseStack.popPose();
            poseStack.translate(0,-3.9,0);
            poseStack.pushPose();
            poseStack.translate(-0.1F,-0.1F,-0.1F);
            poseStack.scale(1.2F,1.2F,1.2F);
            dispatcher.renderSingleBlock(block.defaultBlockState(),poseStack,buffer,light,overlay, ModelData.EMPTY, RenderType.SOLID);
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}
