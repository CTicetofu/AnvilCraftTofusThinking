package dev.anvilcraft.tofusthinking.item.weapon.StaffProcess;


import dev.anvilcraft.tofusthinking.entity.ExtraDamageSource;
import dev.anvilcraft.tofusthinking.util.EntityUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

//写的什么构思
@SuppressWarnings("unused")
public record AbilityHandler(String name, float discount,ParticleOptions particle, Consumer<StaffContext> onRaycast,Consumer<StaffContext> preHit,Consumer<ExtraDamageSource> modifyDamageSource,Consumer<StaffContext> postHit,OnInventorySec preSec) {

    public static AbilityBuilder builder(String name){
        return new AbilityBuilder(name);
    }

    @FunctionalInterface
    public interface OnInventorySec{
        void apply(ItemStack stack,Level level,LivingEntity entity, int slotId, boolean isSelected);
    }
    public static class AbilityBuilder{
        private final String name;
        private float discount = 1F;
        private ParticleOptions particle = ParticleTypes.ENCHANT;
        private Consumer<StaffContext> onRaycast = NONE;
        private Consumer<StaffContext> preHit = NONE;
        private Consumer<StaffContext> postHit = NONE;
        private Consumer<ExtraDamageSource> modifyDamageSource = IGNORE;
        private OnInventorySec inventorySec = NOTHING;

        public AbilityBuilder(String name) {
            this.name = name;
        }

        public AbilityHandler build(){
            return new AbilityHandler(name,discount,particle,onRaycast,preHit,modifyDamageSource,postHit,inventorySec);
        }

        public AbilityBuilder setDiscount(float discount){
            this.discount = discount;
            return this;
        }

        public AbilityBuilder setParticle(ParticleOptions particle){
            this.particle = particle;
            return this;
        }

        public AbilityBuilder setOnRaycast(Consumer<StaffContext> onRaycast){
            this.onRaycast = onRaycast;
            return this;
        }

        public AbilityBuilder setPreHit(Consumer<StaffContext> preHit){
            this.preHit = preHit;
            return this;
        }

        public AbilityBuilder setHit(Consumer<StaffContext> hit){
            this.postHit = hit;
            return this;
        }

        public AbilityBuilder setModifyDamageSource(Consumer<ExtraDamageSource> modifyDamageSource){
            this.modifyDamageSource = modifyDamageSource;
            return this;
        }

        public AbilityBuilder setInventorySec(OnInventorySec inventorySec) {
            this.inventorySec = inventorySec;
            return this;
        }

        public static OnInventorySec NOTHING = (stack, level, entity, slotId, isSelected) -> {};
        public static Consumer<StaffContext> NONE = context -> {};
        public static Consumer<ExtraDamageSource> IGNORE = source -> {};
        public static Consumer<StaffContext> CLEAR_EFFECT = context -> {
            for (LivingEntity target: context.targets){
                EntityUtil.clearAllEffect(target);
            }
        };

    }

}
