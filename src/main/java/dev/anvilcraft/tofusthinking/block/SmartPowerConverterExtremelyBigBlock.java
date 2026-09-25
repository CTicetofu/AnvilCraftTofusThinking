package dev.anvilcraft.tofusthinking.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class SmartPowerConverterExtremelyBigBlock extends SmartPowerConverterBlock{
    public static final VoxelShape SHAPE_DOWN = Block.box(3, 0, 3, 13, 16, 13);
    public static final VoxelShape SHAPE_UP = Block.box(3, 0, 3, 13, 16, 13);
    public static final VoxelShape SHAPE_NORTH = Block.box(3, 3, 0, 13, 13, 16);
    public static final VoxelShape SHAPE_EAST = Block.box(0, 3, 3, 16, 13, 13);
    public static final VoxelShape SHAPE_SOUTH = Block.box(3, 3, 0, 13, 13, 16);
    public static final VoxelShape SHAPE_WEST = Block.box(0, 3, 3, 16, 13, 13);

    public SmartPowerConverterExtremelyBigBlock(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxInputPower() {
        return 1048567;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case UP -> SHAPE_UP;
            case DOWN -> SHAPE_DOWN;
            case NORTH -> SHAPE_NORTH;
            case EAST -> SHAPE_EAST;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
        };
    }
}
