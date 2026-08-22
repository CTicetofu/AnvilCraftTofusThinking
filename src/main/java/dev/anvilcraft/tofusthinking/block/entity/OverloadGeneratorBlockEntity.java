package dev.anvilcraft.tofusthinking.block.entity;

import dev.anvilcraft.tofusthinking.init.block.AddonBlockEntities;
import dev.anvilcraft.tofusthinking.init.entity.AddonDamageTypes;
import dev.dubhe.anvilcraft.api.power.IPowerProducer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import dev.dubhe.anvilcraft.block.CorruptedBeaconBlock;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dev.anvilcraft.tofusthinking.block.entity.OriginalConduitBlockEntity.ORIGINAL_EXPLOSION_CALCULATOR;

public class OverloadGeneratorBlockEntity extends BlockEntity implements IPowerProducer {
    public OverloadGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public OverloadGeneratorBlockEntity(BlockPos pos, BlockState blockState){
        this(AddonBlockEntities.OVERLOAD_GENERATOR.get(),pos,blockState);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private int overloadTimes;
    private PowerGrid grid;
    private long lastTick = -1;
    private float rotation = 0;

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("overload_times",this.overloadTimes);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        this.overloadTimes = tag.getInt("overload_times");
    }

    public void serverTick(Level level, BlockPos pos){
        long time = level.getGameTime();
        if(time % 20 == 0){
            checkOverload(level);
            BlockState state = level.getBlockState(pos.below());
            if(state.is(ModBlocks.CORRUPTED_BEACON.get()) && state.getValue(CorruptedBeaconBlock.LIT)){
                checkOverload(level);
                if(this.overloadTimes > 14){doOverloadDestruct(level, pos);}
            } else {
                if(this.overloadTimes < 1){
                    this.changeAndUpdate(this.overloadTimes - 1);
                } else {
                    doOverloadDestruct(level, pos);
                }
            }
        }
    }

    public void clientTick() {
        rotation += this.overloadTimes * 2.5F + 1;
    }


    private void checkOverload(Level level){
        long currentTime = level.getGameTime();
        if(currentTime == this.lastTick){
            this.changeAndUpdate(this.overloadTimes + 1);
        }
        this.lastTick = currentTime;
    }

    public void checkBeaconExist(Level level, BlockPos pos){
        if(this.overloadTimes <= 0){return;}
        BlockState state = level.getBlockState(pos.below());
        if(!state.is(ModBlocks.CORRUPTED_BEACON.get()) || !state.getValue(CorruptedBeaconBlock.LIT)){
            doOverloadDestruct(level, pos);
        }
    }

    private void doOverloadDestruct(Level level, BlockPos pos){
        if(this.level == null || this.level.isClientSide){return;}
        for (BlockPos blockPos:BlockPos.betweenClosed(pos.offset(1,1,1),pos.offset(-1,-1,-1))){
            BlockState state = level.getBlockState(blockPos);
            if(state.getDestroySpeed(level,pos) < 0){continue;}
            level.destroyBlock(blockPos,false);
        }
        level.explode(null, AddonDamageTypes.rewind(level).justDie().withNotBlock(true),ORIGINAL_EXPLOSION_CALCULATOR,pos.getCenter(),4, false,Level.ExplosionInteraction.BLOCK);
    }

    private void changeAndUpdate(int times){
        if(times < -1){return;}
        this.overloadTimes = times;
        sendUpdate();
        setChanged();
    }

    private void sendUpdate() {
        if (this.level == null) return;
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return this.saveCustomOnly(registries);
    }

    @Override
    public @Nullable Level getCurrentLevel() {
        return this.level;
    }

    @Override
    public @NotNull BlockPos getPos() {
        return this.getBlockPos();
    }

    @Override
    public void setGrid(@Nullable PowerGrid grid) {
        this.grid = grid;
    }

    @Override
    public @Nullable PowerGrid getGrid() {
        return this.grid;
    }

    @Override
    public int getOutputPower() {
        return this.overloadTimes >= 0 ? 2 << 3 * Math.min(this.overloadTimes,4) : 0;
    }

    @Override
    public int getRange() {
        return 2;
    }

    public int getOverloadTimes() {
        return overloadTimes;
    }

    public int getRedstoneSignal(){
        return Mth.clamp(overloadTimes, Redstone.SIGNAL_MIN, Redstone.SIGNAL_MAX);
    }

    public float getRotation() {
        return rotation;
    }
}
