package dev.anvilcraft.tofusthinking.block;

import com.mojang.serialization.MapCodec;
import dev.anvilcraft.tofusthinking.block.entity.SmartPowerConverterBlockEntity;
import dev.anvilcraft.tofusthinking.init.block.AddonBlockEntities;
import dev.anvilcraft.tofusthinking.network.toClient.SimpleNumberInitPacket;
import dev.dubhe.anvilcraft.api.hammer.IHammerRemovable;
import dev.dubhe.anvilcraft.block.BasePowerConverterBlock;
import dev.dubhe.anvilcraft.init.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmartPowerConverterBlock extends BasePowerConverterBlock implements IHammerRemovable {
    public static final VoxelShape SHAPE_DOWN = Block.box(4, 0, 4, 12, 9, 12);
    public static final VoxelShape SHAPE_UP = Block.box(4, 7, 4, 12, 16, 12);
    public static final VoxelShape SHAPE_NORTH = Block.box(4, 4, 0, 12, 12, 9);
    public static final VoxelShape SHAPE_EASE = Block.box(7, 4, 4, 16, 12, 12);
    public static final VoxelShape SHAPE_SOUTH = Block.box(4, 4, 7, 12, 12, 16);
    public static final VoxelShape SHAPE_WEST = Block.box(0, 4, 4, 9, 12, 12);
    public SmartPowerConverterBlock(Properties properties) {
        super(properties,0);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(SmartPowerConverterBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new SmartPowerConverterBlockEntity(pos,state);
    }
    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if(level.isClientSide){return null;}
        return createTickerHelper(
                type,
                AddonBlockEntities.SMART_POWER_CONVERTER.get(),
                ((level1, pos, state1, blockEntity) -> blockEntity.serverTick(level1,pos,state1))
        );
    }
    public @NotNull InteractionResult use(
            @NotNull BlockState state,
            Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand hand,
            @NotNull BlockHitResult hit
    ){
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof SmartPowerConverterBlockEntity entity
                && player instanceof ServerPlayer serverPlayer) {
            ModMenuTypes.open(serverPlayer, entity, pos);
            PacketDistributor.sendToPlayer(serverPlayer, new SimpleNumberInitPacket(entity.getWantInputPower(),0,entity.getMaxInputPower()));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case UP -> SHAPE_UP;
            case DOWN -> SHAPE_DOWN;
            case NORTH -> SHAPE_NORTH;
            case EAST -> SHAPE_EASE;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
        };
    }
}
