package dev.anvilcraft.tofusthinking.event;

import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;

@EventBusSubscriber
public class GenericEventHandler {
    @SubscribeEvent
    public static void onItemExpire(ItemExpireEvent event){
        ItemEntity entity = event.getEntity();
        if(entity.getItem().has(AddonComponents.IMMORTAL)){
            entity.setExtendedLifetime();
        }
    }
}
