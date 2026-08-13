package dev.anvilcraft.tofusthinking.block;

import com.mojang.serialization.MapCodec;
import dev.anvilcraft.tofusthinking.block.entity.FoodGeneratorBlockEntity;
import dev.anvilcraft.tofusthinking.init.block.AddonBlockEntities;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.network.toClient.SimpleNumberInitPacket;
import dev.dubhe.anvilcraft.api.hammer.IHammerRemovable;
import dev.dubhe.anvilcraft.api.itemhandler.FilteredItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


/*
    大部分copy from 充/放电器,虽然我觉得有的写法很奇怪，但这么做一定有它的道理
 */
public class FoodGeneratorBlock extends BaseEntityBlock implements IHammerRemovable {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public FoodGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return super.getMenuProvider(state, level, pos);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(FoodGeneratorBlock::new);
    }
    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return defaultBlockState().setValue(POWERED, false);
    }

    @Override
    public void neighborChanged(
            @NotNull BlockState state,
            Level level,
            @NotNull BlockPos pos,
            @NotNull Block neighborBlock,
            @NotNull BlockPos neighborPos,
            boolean movedByPiston
    ) {
        if (level.isClientSide) {
            return;
        }
        level.setBlock(pos, state.setValue(POWERED, level.hasNeighborSignal(pos)), 2);
    }
    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new FoodGeneratorBlockEntity(AddonBlockEntities.FOOD_GENERATOR.get(),pos,state);
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }
    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if(level.isClientSide){return null;}
        return createTickerHelper(
                    type,
                    AddonBlockEntities.FOOD_GENERATOR.get(),
                    ((level1, pos, state1, blockEntity) -> blockEntity.serverTick(level1,pos,state1))
                );
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston){
        if (state.is(newState.getBlock())) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FoodGeneratorBlockEntity entity) {
            Vec3 vec3 = entity.getBlockPos().getCenter();
            FilteredItemStackHandler depository = entity.getFilteredItemStackHandler();
            for (int slot = 0; slot < depository.getSlots(); slot++) {
                Containers.dropItemStack(level, vec3.x, vec3.y, vec3.z, depository.getStackInSlot(slot));
            }
            level.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
    //先偷个懒
    @Override
    protected @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder params) {
        List<ItemStack> stacks = super.getDrops(state, params);
        if(stacks.size() == 1 && params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof FoodGeneratorBlockEntity blockEntity){
            ItemStack stack = stacks.getFirst();
            if(stack.is(AddonBlocks.NUTRIENT_EXTRACTOR.asItem()) && stack.getCount() == 1 && blockEntity.getLevel() != null){
                CompoundTag tankTag = blockEntity.onlySaveTank(blockEntity.getLevel());
                stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tankTag));
            }
        }
        return stacks;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(
            @NotNull ItemStack stack,
            @NotNull BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand hand,
            @NotNull BlockHitResult hit
    ) {
        if (level.isClientSide()) {
            if (!stack.isEmpty()) return ItemInteractionResult.SUCCESS;
        } else {
            if (level.getBlockEntity(pos) instanceof FoodGeneratorBlockEntity blockEntity && player instanceof ServerPlayer serverPlayer) {
                if(stack.isEmpty()){
                    if(player.isShiftKeyDown()){
                        ItemStack stack1 = blockEntity.getFilteredItemStackHandler().getStackInSlot(1);
                        if(!stack1.isEmpty()){
                            ItemStack extracted = blockEntity.getFilteredItemStackHandler().extractItem(1, stack1.getCount(), false);
                            if(!extracted.isEmpty()){
                                player.getInventory().placeItemBackInInventory(extracted);
                            }
                        } else {
                            ItemStack stack0 = blockEntity.tryExtractItemFromSlot0();
                            if(!stack0.isEmpty()){
                                player.getInventory().placeItemBackInInventory(stack0);
                            }
                        }
                        return ItemInteractionResult.SUCCESS;
                    }
                } else {
                    if (blockEntity.containsValidItem(stack)) {
                        ItemStack result = blockEntity.getFilteredItemStackHandler().insertItem(0, stack, true);
                        if (result.isEmpty() || result.getCount() < stack.getCount()) {
                            int countDiff = stack.getCount() - (result.isEmpty() ? 0 : result.getCount());
                            ItemStack toInsert = stack.split(countDiff);
                            blockEntity.getFilteredItemStackHandler().insertItem(0, toInsert, false);
                        }
                        return ItemInteractionResult.SUCCESS;
                    }
                }
                serverPlayer.openMenu(blockEntity, pos);
                PacketDistributor.sendToPlayer(serverPlayer, new SimpleNumberInitPacket(blockEntity.getPlanNextPower(),0, FoodGeneratorBlockEntity.MAX_POWER));
                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }
}
