package dev.anvilcraft.tofusthinking.util;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;

import java.util.ArrayList;
import java.util.List;

public class EntityUtil {

    //这种实体破事最多了
    public static Entity getMainEntity(Entity entity){
        if(entity instanceof PartEntity<?> part){
            return part.getParent();
        }
        return entity;
    }

    public static Vec3 calculateViewVector(float xRot, float yRot) {
        float f = xRot * (float) (Math.PI / 180.0);
        float f1 = -yRot * (float) (Math.PI / 180.0);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }

    public static void clearAllEffect(LivingEntity entity){
        List<MobEffectInstance> list = new ArrayList<>(entity.getActiveEffects());
        for (MobEffectInstance ins : list) {
            entity.removeEffect(ins.getEffect());
            if (entity.hasEffect(ins.getEffect())) {
                entity.getActiveEffectsMap().remove(ins.getEffect());
            }
        }
    }
}
