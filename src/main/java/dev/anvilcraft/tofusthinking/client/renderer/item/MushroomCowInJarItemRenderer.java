package dev.anvilcraft.tofusthinking.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;

public class MushroomCowInJarItemRenderer extends BlockEntityWithoutLevelRenderer {
    public MushroomCowInJarItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public static final IClientItemExtensions MUSHROOM_COW_IN_JAR_EXTENSION = new IClientItemExtensions() {
        private final BlockEntityWithoutLevelRenderer renderer = new MushroomCowInJarItemRenderer();
        @Override
        public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return renderer;
        }
    };

    @Override
    public void renderByItem(@NonNull ItemStack stack, @NonNull ItemDisplayContext displayContext, @NonNull PoseStack poseStack, @NonNull MultiBufferSource buffer, int light, int overlay) {
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

        MushroomCow cow = getXeKrFromStack(stack);
        if(cow == null){return;}
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.0f, 0.5f);

        float size = 0.6f;
        float max = Math.max(cow.getBbWidth(), cow.getBbHeight());
        if ((double) max > 1.0) {
            size /= max;
        }
        poseStack.translate(0.0f, 0.14f, 0.0f);
        poseStack.scale(size, size, size);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90));
        EntityRenderer<? super Entity> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(cow);
        renderer.render(cow, 0, 0, poseStack, buffer, LightTexture.FULL_BRIGHT);
        poseStack.popPose();
    }

    public static @Nullable MushroomCow getXeKrFromStack(ItemStack stack){
        if (Minecraft.getInstance().level != null) {
            MushroomCow cow = new MushroomCow(EntityType.MOOSHROOM,Minecraft.getInstance().level);
            if(stack.getOrDefault(AddonComponents.TYPE_NUMBER,(byte)0) != 0){
                cow.setVariant(MushroomCow.MushroomType.BROWN);
            }
            return cow;
        }
        return null;
    }
}
