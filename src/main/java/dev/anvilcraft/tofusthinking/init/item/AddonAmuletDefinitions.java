package dev.anvilcraft.tofusthinking.init.item;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.init.entity.AddonEntityTypeTags;
import dev.dubhe.anvilcraft.api.amulet.def.AmuletDefinition;
import dev.dubhe.anvilcraft.api.amulet.def.IAmuletDefinition;
import dev.dubhe.anvilcraft.init.registry.ModRegistryKeys;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public class AddonAmuletDefinitions {
    public static final ResourceKey<IAmuletDefinition> WATER = AddonAmuletDefinitions.key("water");

    public static void bootstrap(BootstrapContext<IAmuletDefinition> ctx) {
        ctx.register(
                WATER,
                AmuletDefinition.builder(AddonItems.WATER_AMULET)
                        .obtainEntity(AddonEntityTypeTags.WATER_AMULET_VALID)
                        .build()
        );
    }

    private static ResourceKey<IAmuletDefinition> key(String name) {
        return ResourceKey.create(ModRegistryKeys.AMULET_DEF, AnvilCraftTofusThinking.of(name));
    }
}
