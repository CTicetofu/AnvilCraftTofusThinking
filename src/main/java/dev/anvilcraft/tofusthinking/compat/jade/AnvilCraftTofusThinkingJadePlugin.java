package dev.anvilcraft.tofusthinking.compat.jade;

import dev.anvilcraft.tofusthinking.block.AutoGrindstone;
import dev.anvilcraft.tofusthinking.block.entity.SmartPowerConverterBlockEntity;
import dev.anvilcraft.tofusthinking.compat.jade.provider.AutoGrindstoneProvider;
import dev.anvilcraft.tofusthinking.compat.jade.provider.ExtraEnergyStorageProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class AnvilCraftTofusThinkingJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEnergyStorage(ExtraEnergyStorageProvider.INSTANCE, SmartPowerConverterBlockEntity.class);
        registration.registerBlockDataProvider(AutoGrindstoneProvider.INSTANCE, AutoGrindstone.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEnergyStorageClient(ExtraEnergyStorageProvider.INSTANCE);
        registration.registerBlockComponent(AutoGrindstoneProvider.INSTANCE, AutoGrindstone.class);
    }
}
