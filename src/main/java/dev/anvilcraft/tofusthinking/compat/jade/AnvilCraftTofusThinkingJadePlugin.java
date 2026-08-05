package dev.anvilcraft.tofusthinking.compat.jade;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class AnvilCraftTofusThinkingJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {}

    @Override
    public void registerClient(IWailaClientRegistration registration) {}
}
