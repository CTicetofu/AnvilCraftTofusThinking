package dev.anvilcraft.tofusthinking.mixin;

import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Player.class)
public class PlayerMixin {
    @ModifyArg(method = "disableShield",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemCooldowns;addCooldown(Lnet/minecraft/world/item/Item;I)V"),index = 1)
    private int lessCooldown(int ticks){
        Player player = (Player) (Object)this;
        if(player.getUseItem().is(AddonItems.STAR_OF_THE_SEA.get())){
            ticks = Math.min(ticks,60);
        }
        return ticks;
    }
}
