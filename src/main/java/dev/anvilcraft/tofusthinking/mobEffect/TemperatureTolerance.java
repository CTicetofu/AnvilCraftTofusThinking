package dev.anvilcraft.tofusthinking.mobEffect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class TemperatureTolerance extends AddonMobEffect{
    public TemperatureTolerance() {
        super(MobEffectCategory.BENEFICIAL, 0x00CD66);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {
        if(livingEntity.getRemainingFireTicks() > 0){livingEntity.setRemainingFireTicks(0);}
        if(livingEntity.getTicksFrozen() > 0){livingEntity.setTicksFrozen(0);}
        return true;
    }
}
