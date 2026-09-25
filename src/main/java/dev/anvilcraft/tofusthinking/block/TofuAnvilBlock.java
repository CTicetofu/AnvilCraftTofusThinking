package dev.anvilcraft.tofusthinking.block;

import dev.anvilcraft.tofusthinking.entity.FallingImitativeBlockEntity;
import dev.anvilcraft.tofusthinking.inventory.TofuAnvilMenu;
import dev.dubhe.anvilcraft.api.hammer.IHammerRemovable;
import dev.dubhe.anvilcraft.init.block.ModBlockTags;
import dev.dubhe.anvilcraft.util.MagnetUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TofuAnvilBlock extends Block implements IHammerRemovable {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final Component CONTAINER_TITLE = Component.translatable("container.repair");
    private static final VoxelShape BASE = Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
    private static final VoxelShape X_LEG1 = Block.box(4.0, 4.0, 5.0, 12.0, 10.0, 11.0);
    private static final VoxelShape X_TOP = Block.box(0.0, 10.0, 3.0, 16.0, 16.0, 13.0);
    private static final VoxelShape Z_LEG1 = Block.box(5.0, 4.0, 4.0, 11.0, 10.0, 12.0);
    private static final VoxelShape Z_TOP = Block.box(3.0, 10.0, 0.0, 13.0, 16.0, 16.0);
    private static final VoxelShape X_AXIS_AABB = Shapes.or(BASE, X_LEG1, X_TOP);
    private static final VoxelShape Z_AXIS_AABB = Shapes.or(BASE, Z_LEG1, Z_TOP);

    public static final MutableComponent TOFU_ANVIL_IGNORE_CONFLICT = Component.translatable("tooltip.anvilcraft_tofus_thinking.tofu_anvil_ignore_conflict").withStyle(ChatFormatting.GRAY);
    public static final Component TOFU_ANVIL_USE = Component.translatable("tooltip.anvilcraft_tofus_thinking.tofu_anvil_use").withStyle(ChatFormatting.GRAY);
    public static final Component TOFU_ANVIL_FALL = Component.translatable("tooltip.anvilcraft_tofus_thinking.tofu_anvil_fall").withStyle(ChatFormatting.GRAY);

    public static Component getConfigConflictComponent(){
        return Component.translatable("tooltip.anvilcraft_tofus_thinking.tofu_anvil_anvil",TOFU_ANVIL_IGNORE_CONFLICT.withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.GRAY);
    }


    public TofuAnvilBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false)
        );
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(TOFU_ANVIL_USE.copy());
        tooltipComponents.add(getConfigConflictComponent());
        tooltipComponents.add(TOFU_ANVIL_FALL.copy());
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return direction.getAxis() == Direction.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
    }

    @Override
    protected @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected boolean isPathfindable(@NotNull BlockState state, @NotNull PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getClockWise())
                .setValue(POWERED, context.getLevel().getBlockState(context.getClickedPos().above()).is(ModBlockTags.MAGNET));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(
            @NotNull BlockState s,
            Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull BlockHitResult hitResult
    ) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            Direction direction = randomDirectList[level.getRandom().nextInt(4)];
            BlockPos blockPos = pos.relative(direction);
            BlockState state = level.getBlockState(blockPos);
            if((state.is(BlockTags.ANVIL) || state.getBlock() instanceof AnvilBlock) && !state.is(this)){
                return state.useWithoutItem(level, player, new BlockHitResult(blockPos.getCenter(),direction.getOpposite(),blockPos,true));
            }
            player.openMenu(s.getMenuProvider(level, pos));
            player.awardStat(Stats.INTERACT_WITH_ANVIL);
            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return new SimpleMenuProvider(
                (i, inventory, player) -> new TofuAnvilMenu(i, inventory, ContainerLevelAccess.create(level, pos)),
                CONTAINER_TITLE);
    }

    public static final Direction[] randomDirectList = {Direction.EAST,Direction.NORTH,Direction.WEST,Direction.SOUTH};

    public BlockState getRandomNearAnvil(Level level,BlockPos pos){
        BlockState state = level.getBlockState(pos.relative(randomDirectList[level.getRandom().nextInt(4)]));
        return state.is(BlockTags.ANVIL) && !state.is(this) ? state : Blocks.AIR.defaultBlockState();
    }

    @Override
    protected void tick(@NotNull BlockState state, ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        level.getBlockState(pos).setValue(POWERED, false);
        BlockState target = getRandomNearAnvil(level,pos);
        if(!state.isAir()){
            FallingImitativeBlockEntity.fall(level,pos,target);
        }
    }

    @Override
    public void neighborChanged(
            BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull Block neighborBlock,
            @NotNull BlockPos neighborPos,
            boolean movedByPiston
    ) {
        boolean hasNeighborSignal = MagnetUtil.hasMagnetism(level, pos);
        boolean currentPowered = state.getValue(POWERED);
        if (hasNeighborSignal && !currentPowered) {
            level.setBlockAndUpdate(pos, state.setValue(POWERED, true));
        } else if (!hasNeighborSignal && currentPowered) {
            level.scheduleTick(pos, this, 10);
            level.setBlockAndUpdate(pos, state.setValue(POWERED, false));
        }
    }


}
