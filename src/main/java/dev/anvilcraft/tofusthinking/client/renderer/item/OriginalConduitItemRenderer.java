package dev.anvilcraft.tofusthinking.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.anvilcraft.tofusthinking.block.entity.OriginalConduitBlockEntity;
import dev.anvilcraft.tofusthinking.client.event.ClientManageHandler;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OriginalConduitItemRenderer extends BlockEntityWithoutLevelRenderer {
    public OriginalConduitItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }
    public static IClientItemExtensions ORIGINAL_CONDUIT_EXTENSION = new IClientItemExtensions() {
        private final BlockEntityWithoutLevelRenderer renderer = new OriginalConduitItemRenderer();
        @Override
        public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return renderer;
        }
    };

    public static final OriginalConduitBlockEntity blockEntity = new OriginalConduitBlockEntity(BlockPos.ZERO.offset(0,1,0), AddonBlocks.ORIGINAL_CONDUIT.get().defaultBlockState()).withActive();
    public static final OriginalConduitBlockEntity huntBlockEntity = new OriginalConduitBlockEntity(BlockPos.ZERO.offset(0,2,0), AddonBlocks.ORIGINAL_CONDUIT.get().defaultBlockState()).withActive().withHunt();
    public static final OriginalConduitBlockEntity sleepBlockEntity = new OriginalConduitBlockEntity(BlockPos.ZERO.offset(0,3,0), AddonBlocks.ORIGINAL_CONDUIT.get().defaultBlockState());
    private static final BlockEntityRenderDispatcher blockEntityRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderConduit(blockEntity,poseStack,buffer,packedLight,packedOverlay);
    }

    public static void renderConduit(OriginalConduitBlockEntity conduit, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay){
        conduit.tickCount = ClientManageHandler.TICK_COUNT;
        Objects.requireNonNull(blockEntityRenderDispatcher.getRenderer(conduit)).render(conduit,DeltaTracker.ONE.getGameTimeDeltaPartialTick(false),poseStack,buffer,packedLight,packedOverlay);
    }
}
