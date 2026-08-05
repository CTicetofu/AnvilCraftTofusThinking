package dev.anvilcraft.tofusthinking.client.event;

import dev.anvilcraft.tofusthinking.client.ClientUtil;
import dev.anvilcraft.tofusthinking.init.item.AddonItemTags;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ClientPlayerHandler {
    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event){
        if(ClientUtil.getUseItemStack().is(AddonItemTags.NORMAL_MOVEMENT_WHEN_USE)){
            event.getInput().leftImpulse *= 5;
            event.getInput().forwardImpulse *= 5;
        }
    }
}
