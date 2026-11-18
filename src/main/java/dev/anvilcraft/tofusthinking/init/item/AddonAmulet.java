package dev.anvilcraft.tofusthinking.init.item;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.api.amulet.effect.WaterAmuletEffect;
import dev.dubhe.anvilcraft.api.amulet.Amulet;
import dev.dubhe.anvilcraft.init.registry.ModRegistryKeys;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AddonAmulet {
    private static final DeferredRegister<Amulet> REGISTER = DeferredRegister.create(
            ModRegistryKeys.AMULET,
            AnvilCraftTofusThinking.MOD_ID
    );

    public static final DeferredHolder<Amulet, Amulet> WATER = REGISTER.register(
            "water",
            () -> Amulet.of(WaterAmuletEffect.INSTANCE)
    );

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
