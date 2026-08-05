package dev.anvilcraft.tofusthinking.block.entity;

import dev.anvilcraft.tofusthinking.init.block.AddonBlockEntities;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.inventory.SimpleNumberConfigMenu;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import dev.dubhe.anvilcraft.block.BasePowerConverterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmartPowerConverterBlockEntity extends BlockEntity implements IPowerConsumer, MenuProvider {
    private PowerGrid grid = null;
    private int inputPower;
    private int cooldown = 0;
    int energy = 0;
    public final int MAX_ENERGY = 1000000000;
    public SmartPowerConverterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public SmartPowerConverterBlockEntity(BlockPos pos, BlockState blockState){
        this(AddonBlockEntities.SMART_POWER_CONVERTER.get(),pos,blockState);
    }

    public @Nullable IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        if (side == null) return new SmartPowerConverterEnergyStore();
        if (side == getBlockState().getValue(BasePowerConverterBlock.FACING)) return new SmartPowerConverterEnergyStore();
        return null;
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("InputPower", inputPower);
        tag.putInt("Cooldown", cooldown);
        tag.putInt("Energy", energy);
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        inputPower = tag.getInt("InputPower");
        cooldown = tag.getInt("Cooldown");
        energy = tag.getInt("Energy");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("Energy", energy);
        tag.putInt("InputPower", inputPower);
        return tag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.handleUpdateTag(tag, registries);
        this.energy = tag.getInt("Energy");
        this.inputPower = tag.getInt("InputPower");
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(@NotNull Connection connection, @NotNull ClientboundBlockEntityDataPacket packet, HolderLookup.@NotNull Provider registries) {
        super.onDataPacket(connection, packet, registries);
        CompoundTag tag = packet.getTag();
        handleUpdateTag(tag, registries);
    }

    //原本的转换器是不是多算了1tick?

    @Override
    public void gridTick() {
        if (this.level != null) {
            flushState(this.level, getBlockPos());
            if (this.getBlockState().getValue(BasePowerConverterBlock.POWERED)) return;
        }
        if (getBlockState().getValue(BasePowerConverterBlock.OVERLOAD)) return;
        int amountTick = (int) (inputPower
                * AnvilCraft.CONFIG.powerConverter.powerConverterEfficiency
                * (1 - AnvilCraft.CONFIG.powerConverter.powerConverterLoss)
        );
        int amount = amountTick * PowerGrid.GRID_TICK;
        this.energy = Math.clamp(this.energy + amount,0, MAX_ENERGY);
        setChanged();
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level != null) {
            flushState(level, pos);
            if (state.getValue(BasePowerConverterBlock.POWERED)) return;
        }
        pushEnergy();
        if (level != null && level.getGameTime() % 20 == 0) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private void pushEnergy() {
        if (this.level == null || this.energy <= 0) return;
        Direction face = getBlockState().getValue(BasePowerConverterBlock.FACING);
        IEnergyStorage target = level.getCapability(
                Capabilities.EnergyStorage.BLOCK,
                getBlockPos().relative(face),
                face.getOpposite()
        );
        if (target != null && target.canReceive()) {
            int accepted = target.receiveEnergy(this.energy, false);
            if (accepted > 0) {
                this.energy -= accepted;
                setChanged();
            }
        }
    }
    @Override
    public int getInputPower() {
        return this.getBlockState().getValue(BasePowerConverterBlock.POWERED) ? 0 : inputPower;
    }
    @Override
    public @Nullable Level getCurrentLevel() {
        return getLevel();
    }

    @Override
    public @NotNull BlockPos getPos() {
        return getBlockPos();
    }

    @Override
    public void setGrid(@Nullable PowerGrid grid) {
        this.grid = grid;
    }

    @Override
    public @Nullable PowerGrid getGrid() {
        return grid;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return AddonBlocks.SMART_POWER_CONVERTER.get().getName();
    }

    private void setInputPower(int energy) {
        this.inputPower = Mth.clamp(energy,0,getMaxInputPower());
        setChanged();
    }

    public int getWantInputPower() {
        return inputPower;
    }

    public int getMaxInputPower(){return 1048576;}

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new SimpleNumberConfigMenu(containerId, this::setInputPower);
    }

    class SmartPowerConverterEnergyStore implements IEnergyStorage {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int r = Math.min(energy, maxExtract);
            if (!simulate) {
                energy -= r;
                setChanged();
            }
            return r;
        }

        @Override
        public int getEnergyStored() {
            return energy;
        }

        @Override
        public int getMaxEnergyStored() {
            return MAX_ENERGY;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    }
}
