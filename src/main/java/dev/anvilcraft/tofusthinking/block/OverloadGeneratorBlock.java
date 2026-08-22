package dev.anvilcraft.tofusthinking.block;

import com.mojang.serialization.MapCodec;
import dev.anvilcraft.tofusthinking.block.entity.OverloadGeneratorBlockEntity;
import dev.anvilcraft.tofusthinking.init.block.AddonBlockEntities;
import dev.dubhe.anvilcraft.api.hammer.IHammerRemovable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OverloadGeneratorBlock extends BaseEntityBlock implements IHammerRemovable {
    public static VoxelShape SHAPE = Shapes.or(
            Block.box(0, 0, 0, 16, 2, 16)
    );
    public OverloadGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(OverloadGeneratorBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new OverloadGeneratorBlockEntity(pos,state);
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState blockState, Level level, @NotNull BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof OverloadGeneratorBlockEntity be) {
            return be.getRedstoneSignal();
        }
        return 0;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? createTickerHelper(blockEntityType, AddonBlockEntities.OVERLOAD_GENERATOR.get(),
                (level1, blockPos, blockState, blockEntity) -> blockEntity.clientTick()
        ) : createTickerHelper(blockEntityType, AddonBlockEntities.OVERLOAD_GENERATOR.get(),
                (level1, blockPos, blockState, blockEntity) -> blockEntity.serverTick(level,blockPos));
    }

    @Override
    protected void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
        if(!level.isClientSide && level.getBlockEntity(pos) instanceof OverloadGeneratorBlockEntity blockEntity){
            blockEntity.checkBeaconExist(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    public @NotNull VoxelShape getShape(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        return SHAPE;
    }
}
