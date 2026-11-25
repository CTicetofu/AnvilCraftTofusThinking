package dev.anvilcraft.tofusthinking.block;

import com.mojang.serialization.MapCodec;
import dev.anvilcraft.lib.v2.util.DistExecutor;
import dev.anvilcraft.tofusthinking.block.entity.SteelBarrelBlockEntity;
import dev.anvilcraft.tofusthinking.util.TooltipUtil;
import dev.dubhe.anvilcraft.block.VoidMatterBlock;
import dev.dubhe.anvilcraft.client.gui.screen.StorageScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

public class SteelBarrelBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    public SteelBarrelBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH).setValue(OPEN, false));
    }

    @Override
    protected void onExplosionHit(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Explosion explosion, @NonNull BiConsumer<ItemStack, BlockPos> dropConsumer) {}

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(SteelBarrelBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        return new SteelBarrelBlockEntity(blockPos, blockState);
    }

    public static boolean hasAdjacentVoidMatter(LevelReader level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (level.getBlockState(pos.relative(direction)).getBlock() instanceof VoidMatterBlock) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean hasAnalogOutputSignal(@NonNull BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(@NonNull BlockState state, Level level, @NonNull BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof SteelBarrelBlockEntity entity) {
            return entity.getComparatorSignal();
        }
        return 0;
    }

    @Override
    protected void neighborChanged(
            @NonNull BlockState state,
            @NonNull Level level,
            @NonNull BlockPos pos,
            @NonNull Block neighborBlock,
            @NonNull BlockPos neighborPos,
            boolean movedByPiston
    ) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        SteelBarrelBlock.updateDisposeState(level, pos);
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        if(level.getBlockEntity(pos) instanceof SteelBarrelBlockEntity blockEntity && !player.isShiftKeyDown()){
            if (player instanceof ServerPlayer) {
                return InteractionResult.sidedSuccess(false);
            } else if (level.isClientSide()) {
                level.playSound(player, pos, SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                Component title = SteelBarrelBlock.displayName(state);
                DistExecutor.run(Dist.CLIENT, () -> () -> StorageScreen.openScreen(blockEntity.getBlockPos(), title));
                return InteractionResult.sidedSuccess(true);
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    public static Component displayName(BlockState state) {
        return state.getValue(SteelBarrelBlock.OPEN)
                ? Component.translatable("block.anvilcraft_tofus_thinking.overflow_disposal_steel_barrel")
                : state.getBlock().getName();
    }

    public static void updateDisposeState(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof SteelBarrelBlock) || level.isClientSide()) {
            return;
        }
        boolean dispose = SteelBarrelBlock.hasAdjacentVoidMatter(level, pos);
        if (state.getValue(SteelBarrelBlock.OPEN) != dispose) {
            level.setBlock(pos, state.setValue(SteelBarrelBlock.OPEN, dispose), Block.UPDATE_CLIENTS);
            if (level.getBlockEntity(pos) instanceof SteelBarrelBlockEntity steel) {
                steel.refreshDispose();
            }
        }
    }

    @Override
    protected void onRemove(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!(newState.getBlock() instanceof SteelBarrelBlock)) {
            if (level.getBlockEntity(pos) instanceof SteelBarrelBlockEntity be) {
                be.dropContents(level, pos);
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
        if (!level.isClientSide() && !(newState.getBlock() instanceof SteelBarrelBlock)) {
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.relative(direction);
                if (level.getBlockState(neighborPos).getBlock() instanceof SteelBarrelBlock) {
                    SteelBarrelBlock.updateDisposeState(level, neighborPos);
                }
            }
        }
    }

    protected @NonNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    protected @NonNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected @NonNull RenderShape getRenderShape(@NonNull BlockState state) {
        return RenderShape.MODEL;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NonNull ItemStack stack, Item.@NonNull TooltipContext context, @NonNull List<Component> tooltips, @NonNull TooltipFlag tooltipFlag) {
        boolean shift = Screen.hasShiftDown();
        if(shift){
            tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thing.steel_barrel_shift").withStyle(ChatFormatting.GRAY));
            tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thing.steel_barrel1").withStyle(ChatFormatting.GRAY));
            tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thing.steel_barrel2").withStyle(ChatFormatting.GRAY));
            tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thing.steel_barrel3").withStyle(ChatFormatting.GRAY));
        } else {
            tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thing.steel_barrel").withStyle(ChatFormatting.GRAY));
            tooltips.add(TooltipUtil.HOLD_SHIFT_FOR_MORE.copy());
        }
    }
}
