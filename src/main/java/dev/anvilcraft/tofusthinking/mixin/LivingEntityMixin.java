package dev.anvilcraft.tofusthinking.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Shadow
    @Nullable
    protected Player lastHurtByPlayer;

    @WrapOperation(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"))
    public void awardExp(ServerLevel level, Vec3 pos, int amount, Operation<Void> original){
        if(this.lastHurtByPlayer instanceof ServerPlayer player && player.isHolding(stack -> stack.has(AddonComponents.MAGNETIC_ATTRACTION))){
            player.takeXpDelay = 0;
            pos = player.position().add(0,0.1,0);
            while(amount > 0) {
                int i = ExperienceOrb.getExperienceValue(amount);
                amount -= i;
                ExperienceOrb orb = new ExperienceOrb(level, pos.x(), pos.y(), pos.z(), i);
                orb.playerTouch(player);
                player.takeXpDelay = 0;
            }
        } else {
            original.call(level, pos, amount);
        }
    }
}
