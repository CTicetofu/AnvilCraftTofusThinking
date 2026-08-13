package dev.anvilcraft.tofusthinking.setup;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.entity.livingEntity.StrangeWither;
import dev.anvilcraft.tofusthinking.init.entity.AddonEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = AnvilCraftTofusThinking.MOD_ID)
public class CommonSetup {
    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(AddonEntities.STRANGE_WITHER.get(), StrangeWither.createAttributes().build());
    }
}
