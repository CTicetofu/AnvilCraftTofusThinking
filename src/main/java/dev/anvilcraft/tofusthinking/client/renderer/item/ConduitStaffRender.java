package dev.anvilcraft.tofusthinking.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.block.entity.OriginalConduitBlockEntity;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.dubhe.anvilcraft.client.support.FluidRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class ConduitStaffRender extends BlockEntityWithoutLevelRenderer {
    public ConduitStaffRender() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }
    public static IClientItemExtensions CONDUIT_STAFF_EXTENSION = new IClientItemExtensions() {
        private final BlockEntityWithoutLevelRenderer renderer = new ConduitStaffRender();
        @Override
        public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return renderer;
        }
    };
    FluidStack water = new FluidStack(Fluids.WATER,1000);
    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int light, int overlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        poseStack.pushPose();

        BakedModel models = itemRenderer.getItemModelShaper().getModelManager().getModel(ModelResourceLocation.standalone(AnvilCraftTofusThinking.of("item/conduit_staff_model")));
        for (var model : models.getRenderPasses(stack, true)) {
            for (var rendertype : model.getRenderTypes(stack, true)) {
                VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(buffer, rendertype, true, stack.hasFoil());
                itemRenderer.renderModelLists(model, stack, light, overlay, poseStack, vertexconsumer);
            }
        }
        poseStack.scale(0.5F,0.5F,0.5F);
        poseStack.translate(0.5,1.85F,0.5);
        boolean auto = stack.getOrDefault(AddonComponents.AUTO_HUNT,false);
        OriginalConduitBlockEntity blockEntity = auto ? OriginalConduitItemRenderer.huntBlockEntity : OriginalConduitItemRenderer.blockEntity;
        OriginalConduitItemRenderer.renderConduit(blockEntity,poseStack,buffer,light,overlay);
        poseStack.translate(0,-0.1,0);
        FluidRenderHelper.INSTANCE.renderFluidBox(water, 0.01F, 0.01F, 0.01F, 0.99F, 0.99F, 0.99F, buffer, poseStack, light, true, false);
        poseStack.popPose();
    }

}
