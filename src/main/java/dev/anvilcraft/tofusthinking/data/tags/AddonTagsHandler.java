package dev.anvilcraft.tofusthinking.data.tags;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumTagsProvider;
import dev.dubhe.anvilcraft.data.tags.DamageTypeTagLoader;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;

public class AddonTagsHandler {
    public static void initDamageType(RegistrumTagsProvider<DamageType> provider) {
        AddonDamageTypeTagLoader.init(provider);
    }

    public static void initEntityType(RegistrumTagsProvider<EntityType<?>> provider) {
        AddonEntityTypeTagLoader.init(provider);
    }

}
