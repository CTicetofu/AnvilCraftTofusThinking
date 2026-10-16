package dev.anvilcraft.tofusthinking.client.event;

import dev.anvilcraft.tofusthinking.client.ClientCooldownCache;
import dev.anvilcraft.tofusthinking.client.ClientUtil;
import dev.anvilcraft.tofusthinking.init.item.AddonItemTags;
import dev.anvilcraft.tofusthinking.item.weapon.OriginalConduitStaff;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ClientPlayerHandler {
    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event){
        if(ClientUtil.getUseItemStack().is(AddonItemTags.NORMAL_MOVEMENT_WHEN_USE)){
            event.getInput().leftImpulse *= 5;
            event.getInput().forwardImpulse *= 5;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingBreath(LivingBreatheEvent event){
        if(event.getEntity() instanceof Player player && player.level().isClientSide){
            if(ClientCooldownCache.isOnCooldown(OriginalConduitStaff.BREATH)){
                event.setCanBreathe(true);
                event.setConsumeAirAmount(0);
                event.setRefillAirAmount(player.getMaxAirSupply());
            }
        }
    }
}
