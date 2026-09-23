package dev.anvilcraft.tofusthinking.block;

import dev.anvilcraft.tofusthinking.block.entity.OriginalConduitBlockEntity;
import dev.anvilcraft.tofusthinking.init.block.AddonBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ConduitBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class OriginalConduitBlock extends ConduitBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public OriginalConduitBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false).setValue(OPEN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN);
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new OriginalConduitBlockEntity(pos, state);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return state.getValue(OPEN) ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.MODEL;
    }

    @Override
    protected @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder params) {
        List<ItemStack> stacks = super.getDrops(state, params);
        if(params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof OriginalConduitBlockEntity blockEntity){
            if(blockEntity.isIrreversible()){return new ArrayList<>();}
        }
        return stacks;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, AddonBlockEntities.ORIGINAL_CONDUIT.get(), level.isClientSide ? OriginalConduitBlockEntity::clientTick : OriginalConduitBlockEntity::serverTick);
    }

    @Override
    public boolean canEntityDestroy(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Entity entity) {
        return (entity instanceof Player || entity.getType().is(Tags.EntityTypes.BOSSES)) && super.canEntityDestroy(state, level, pos, entity);
    }

}
