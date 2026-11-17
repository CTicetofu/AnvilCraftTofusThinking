package dev.anvilcraft.tofusthinking.block;

import com.mojang.serialization.MapCodec;
import dev.anvilcraft.tofusthinking.block.entity.AutoGrindstoneBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.IItemHandler;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class AutoGrindstone extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public AutoGrindstone(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH).setValue(TRIGGERED, false));
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(AutoGrindstone::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(TRIGGERED);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        return new AutoGrindstoneBlockEntity(blockPos, blockState);
    }

    @Override
    protected @NonNull VoxelShape getVisualShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected float getShadeBrightness(@NonNull BlockState state, @NonNull BlockGetter getter, @NonNull BlockPos pos) {
        return 1.0F;
    }

    protected boolean propagatesSkylightDown(@NonNull BlockState state, @NonNull BlockGetter getter, @NonNull BlockPos pos) {
        return true;
    }

    @Override
    protected @NonNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    protected @NonNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, facing);
    }

    @Override
    protected @NonNull RenderShape getRenderShape(@NonNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void neighborChanged(
            @NonNull BlockState state,
            Level level,
            @NonNull BlockPos pos,
            @NonNull Block neighborBlock,
            @NonNull BlockPos neighborPos,
            boolean movedByPiston
    ) {
        if (level.isClientSide) {return;}
        boolean flag = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
        boolean flag1 = state.getValue(TRIGGERED);
        if (flag && !flag1) {
            level.scheduleTick(pos, this, 4);
            level.setBlock(pos, state.setValue(TRIGGERED, true), 2);
        } else if (!flag && flag1) {
            level.setBlock(pos, state.setValue(TRIGGERED, false), 2);
        }
    }

    @Override
    protected void tick(@NonNull BlockState state, @NonNull ServerLevel level, @NonNull BlockPos pos, @NonNull RandomSource random) {
        if(level.getBlockEntity(pos) instanceof AutoGrindstoneBlockEntity blockEntity){
            blockEntity.takeGrind();
        }
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        if(player.level().isClientSide){return InteractionResult.SUCCESS;}
        if(level.getBlockEntity(pos) instanceof AutoGrindstoneBlockEntity blockEntity){
            if(!player.isShiftKeyDown()){
                blockEntity.setItemToHand(player);
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected void onRemove(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull BlockState newState, boolean movedByPiston) {
        if(level instanceof ServerLevel serverLevel && !state.is(newState.getBlock()) && serverLevel.getBlockEntity(pos) instanceof AutoGrindstoneBlockEntity blockEntity){
            Vec3 vec3 = blockEntity.getBlockPos().getCenter();
            IItemHandler depository = blockEntity.getItemHandler();
            for (int slot = 0; slot < depository.getSlots(); slot++) {
                Containers.dropItemStack(level, vec3.x, vec3.y, vec3.z, depository.getStackInSlot(slot));
            }
            int dropXp = blockEntity.getTotalXp();
            if(dropXp > 0){
                Vec3 spawn = pos.getCenter();
                ExperienceOrb orb = new ExperienceOrb(serverLevel, spawn.x, spawn.y, spawn.z, dropXp);
                serverLevel.addFreshEntity(orb);
            }
            level.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, Item.@NonNull TooltipContext context, @NonNull List<Component> tooltips, @NonNull TooltipFlag tooltipFlag) {
        tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thing.auto_grindstone1").withStyle(ChatFormatting.GRAY));
        tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thing.auto_grindstone2").withStyle(ChatFormatting.GRAY));
    }
}
