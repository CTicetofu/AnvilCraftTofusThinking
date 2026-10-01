package dev.anvilcraft.tofusthinking.event;

import dev.anvilcraft.tofusthinking.attachment.magic.CooldownManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber
public class MagicEventHandler {
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event){
        if (event.getLevel().isClientSide) {return;}
        event.getLevel().players().stream().toList().forEach(player -> {
            if(player instanceof ServerPlayer serverPlayer){
                CooldownManager.tick(serverPlayer);
            }
        });
    }
    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CooldownManager.syncAll(player);
        }
    }
}
