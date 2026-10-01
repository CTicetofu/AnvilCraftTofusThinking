package dev.anvilcraft.tofusthinking.client.event;

import dev.anvilcraft.tofusthinking.client.ClientCooldownCache;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
@EventBusSubscriber(Dist.CLIENT)
public class ClientManageHandler {
    public static int TICK_COUNT = 0;
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event){
        if(TICK_COUNT++ > 72000){
            TICK_COUNT = 0;
        }
        ClientCooldownCache.tickClient();
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientCooldownCache.clear();
    }
}
