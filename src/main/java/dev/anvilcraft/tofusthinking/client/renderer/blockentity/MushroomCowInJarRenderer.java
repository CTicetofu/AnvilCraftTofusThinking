package dev.anvilcraft.tofusthinking.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.anvilcraft.tofusthinking.block.MushroomCowInJarBlock;
import dev.anvilcraft.tofusthinking.block.entity.MushroomCowInJarBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public class MushroomCowInJarRenderer implements BlockEntityRenderer<MushroomCowInJarBlockEntity> {
    @SuppressWarnings("unused")
    public MushroomCowInJarRenderer(BlockEntityRendererProvider.Context context){}
    @Override
    public void render(@NonNull MushroomCowInJarBlockEntity blockEntity, float partialTick, @NonNull PoseStack poseStack, @NonNull MultiBufferSource source, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        if (level == null) return;
        Entity entity = blockEntity.getOrCreateXeKr(level);
        if (entity == null) return;
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.0f, 0.5f);

        float size = 0.6f;
        float max = Math.max(entity.getBbWidth(), entity.getBbHeight());
        if ((double) max > 1.0) {
            size /= max;
        }
        poseStack.translate(0.0f, 0.14f, 0.0f);
        poseStack.scale(size, size, size);
        BlockState state = blockEntity.getBlockState();
        if(state.hasProperty(MushroomCowInJarBlock.FACING)){
            float yRot = blockEntity.getBlockState().getValue(MushroomCowInJarBlock.FACING).toYRot();
            poseStack.mulPose(Axis.YP.rotationDegrees(-yRot));
        }
        EntityRenderer<? super Entity> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
        renderer.render(entity, 0, 0, poseStack, source, packedLight);
        poseStack.popPose();
    }
}
