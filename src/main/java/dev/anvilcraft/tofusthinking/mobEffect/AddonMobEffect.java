package dev.anvilcraft.tofusthinking.mobEffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public class AddonMobEffect extends MobEffect {
    public AddonMobEffect(MobEffectCategory category, int color){
        super(category,color);
    }

    private boolean notCure = false;

    @Override
    public void fillEffectCures(@NonNull Set<EffectCure> cures, @NonNull MobEffectInstance effectInstance) {
        if(!notCure){
            super.fillEffectCures(cures, effectInstance);
        }
    }

    public AddonMobEffect withNoCure(){
        this.notCure = true;
        return this;
    }
}
