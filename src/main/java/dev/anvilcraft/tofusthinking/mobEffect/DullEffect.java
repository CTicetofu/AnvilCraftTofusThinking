package dev.anvilcraft.tofusthinking.mobEffect;


import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class DullEffect extends AddonMobEffect {
    public DullEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B4513);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {
        if(livingEntity instanceof Player){return false;}
        livingEntity.stopUsingItem();
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
