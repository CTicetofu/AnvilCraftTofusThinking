package dev.anvilcraft.tofusthinking.api.energy;

import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;


//我觉得普通工具的电量不该被提取 Maybe之后可以做一个可变容量
public class FEEnergyTool implements IEnergyStorage {
    private final ItemStack stack;

    public FEEnergyTool(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        int energy = stack.getOrDefault(AddonComponents.STORED_ENERGY, 0);
        int accepted = Math.min(toReceive, getMaxEnergyStored() - energy);
        if (!simulate && accepted > 0) {
            stack.set(AddonComponents.STORED_ENERGY, energy + accepted);
        }
        return accepted;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return stack.getOrDefault(AddonComponents.STORED_ENERGY, 0);
    }

    @Override
    public int getMaxEnergyStored() {
        return stack.getOrDefault(AddonComponents.MAX_ENERGY, 1000);
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
