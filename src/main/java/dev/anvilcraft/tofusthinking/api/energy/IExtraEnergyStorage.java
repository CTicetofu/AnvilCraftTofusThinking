package dev.anvilcraft.tofusthinking.api.energy;

import net.neoforged.neoforge.energy.IEnergyStorage;

public interface IExtraEnergyStorage extends IEnergyStorage {
    int MAX_DISPLAYER = 2000000000;
    int HALF_DISPLAYER = 1000000000;
    long getRealEnergyStored();
    long getRealMaxEnergyStored();

    @Override
    default int getEnergyStored(){
        long max = getRealMaxEnergyStored();
        long current = getRealEnergyStored();
        if(current <= HALF_DISPLAYER || max <= MAX_DISPLAYER){return (int)current;}
        if(max - current < HALF_DISPLAYER){
            return Math.toIntExact(MAX_DISPLAYER - (max - current));
        } else {
            return HALF_DISPLAYER;
        }
    }

    @Override
    default int getMaxEnergyStored(){
        return Math.toIntExact(Math.min(getRealMaxEnergyStored(), MAX_DISPLAYER));
    }
}
