package dev.anvilcraft.tofusthinking.client.event;

import dev.anvilcraft.tofusthinking.client.ClientCooldownCache;
import dev.anvilcraft.tofusthinking.item.LeftClickAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
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
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if(player == null){return;}
        if(minecraft.screen == null && minecraft.options.keyAttack.isDown()){
            ItemStack stack = player.getMainHandItem();
            if(stack.getItem() instanceof LeftClickAction action){
                action.onClientClick(stack,player);
            }
        }
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientCooldownCache.clear();
    }
}
