package dev.anvilcraft.tofusthinking.data.tags;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumTagsProvider;
import dev.dubhe.anvilcraft.data.tags.DamageTypeTagLoader;
import net.minecraft.world.damagesource.DamageType;

public class AddonTagsHandler {
    public static void initDamageType(RegistrumTagsProvider<DamageType> provider) {
        AddonDamageTypeTagLoader.init(provider);
    }

}
