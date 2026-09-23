package dev.anvilcraft.tofusthinking.mobEffect;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public interface NotApplyEffect {
    boolean isNotApply(MobEffectInstance instance, LivingEntity entity);
}
