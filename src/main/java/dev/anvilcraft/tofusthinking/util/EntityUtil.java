package dev.anvilcraft.tofusthinking.util;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;

import java.util.ArrayList;
import java.util.HashMap;
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

    /*
    我觉得这玩意其实跨存档污染也是符合预期的，且不太会内存泄漏，反正用的都是定死的有限实体类型
    这些数据是加载的时候决定的，如果有模组修改了导致不同存档可以有不同基础数值
    你可以向我汇报，之后我会在存档关闭时重置，但现在没有问题不是吗
     */
    private static final HashMap<EntityType<?>,Float> ENTITY_MAX_HEALTH = new HashMap<>();
    @SuppressWarnings("unchecked")
    public static float getOriginMaxHealth(LivingEntity entity){
        Float MAX_HEALTH = ENTITY_MAX_HEALTH.get(entity.getType());
        if(MAX_HEALTH == null){
            AttributeSupplier supplier = DefaultAttributes.getSupplier((EntityType<? extends LivingEntity>) entity.getType());
            float maxHealth = 20;
            if(supplier.hasAttribute(Attributes.MAX_HEALTH)){
                maxHealth = Math.max((float) supplier.getBaseValue(Attributes.MAX_HEALTH),1);
            }
            ENTITY_MAX_HEALTH.put(entity.getType(),maxHealth);
            return maxHealth;
        }
        return MAX_HEALTH;
    }

    public static float getOriginMaxHealthRate(LivingEntity entity){
        return entity.getMaxHealth() / getOriginMaxHealth(entity);
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
