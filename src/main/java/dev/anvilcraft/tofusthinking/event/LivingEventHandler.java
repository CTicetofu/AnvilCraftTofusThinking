package dev.anvilcraft.tofusthinking.event;

import dev.anvilcraft.tofusthinking.entity.ExtraDamageSource;
import dev.anvilcraft.tofusthinking.init.AddonMobEffects;
import dev.anvilcraft.tofusthinking.init.entity.AddonDamageTypeTags;
import dev.anvilcraft.tofusthinking.init.entity.AddonDamageTypes;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.item.weapon.StarOfTheSea;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.*;

@EventBusSubscriber
public class LivingEventHandler {
    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event){
        LivingEntity entity = event.getEntity();
        if(entity.hasEffect(AddonMobEffects.CURSE.getDelegate())){event.setCanceled(true);}
        DamageSource source = entity.getLastDamageSource();
        if(source != null){
            if(source.is(AddonDamageTypeTags.REWIND)){event.setCanceled(true);}
        }
    }
    @SubscribeEvent
    public static void onLivingBlock(LivingShieldBlockEvent event){
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getDamageSource();
        if(source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)){return;}
        if(entity instanceof Player player && StarOfTheSea.isCanBlock(player)){
            event.setBlocked(true);
            boolean perfectBlock = player.getTicksUsingItem() >= 5 && player.getTicksUsingItem() <= 20;
            if(!source.is(DamageTypeTags.BYPASSES_SHIELD) || (perfectBlock && source.is(AddonDamageTypeTags.CAN_PERFECT_BLOCK))){
                ItemStack stack = player.getUseItem();
                if(perfectBlock && source.getDirectEntity() instanceof LivingEntity attacker){
                    if(stack.getOrDefault(AddonComponents.EFFECT_TICK,0) != player.tickCount && source.getEntity() != player){
                        float amount = event.getOriginalBlockedDamage();
                        amount += player.getUsedItemHand() == InteractionHand.MAIN_HAND ? (float)attacker.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0;
                        if(player.invulnerableTime <= 10 || source.is(DamageTypeTags.BYPASSES_COOLDOWN)){attacker.invulnerableTime = 0;}
                        player.invulnerableTime = Math.min(player.invulnerableTime,15);
                        attacker.hurt(AddonDamageTypes.counter(player.level(),player), amount);
                        stack.set(AddonComponents.EFFECT_TICK,player.tickCount);
                    }
                }
                if(perfectBlock && source.is(AddonDamageTypeTags.CAN_PERFECT_BLOCK)){
                    StarOfTheSea.dealAbsorb(stack,source);
                }
            } else {
                event.setBlockedDamage(event.getBlockedDamage() * 0.5F);
            }
        }
    }
    @SubscribeEvent(priority = EventPriority.HIGH,receiveCanceled = true)
    public static void onHighLivingInComing(LivingIncomingDamageEvent event){
        DamageSource source = event.getSource();
        if(source instanceof ExtraDamageSource extra){
            float amount = event.getAmount();
            event.setAmount(amount * (extra.getExtraHurtRate()) + extra.getExtraHurtAmount());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH,receiveCanceled = true)
    public static void onLowLivingDamage(LivingDamageEvent.Pre event){
        DamageSource source = event.getSource();
        if(source instanceof ExtraDamageSource extra){
            float amount = event.getNewDamage();
            event.setNewDamage(amount * (extra.getExtraDamageAmountRate()) + extra.getExtraDamageAmount());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST,receiveCanceled = true)
    public static void onLowestLivingInComing(LivingIncomingDamageEvent event){
        DamageSource source = event.getSource();
        if(source instanceof ExtraDamageSource extra && extra.isNotBlock()){
            if(event.isCanceled()){event.setCanceled(false);}
            event.setAmount(Math.max(event.getOriginalAmount(), event.getAmount()));
            event.setInvulnerabilityTicks(0);
            for (DamageContainer.Reduction value : DamageContainer.Reduction.values()) {
                event.addReductionModifier(value,((container, reductionIn) -> 0));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST,receiveCanceled = true)
    public static void onLowestLivingDamage(LivingDamageEvent.Pre event){
        DamageSource source = event.getSource();
        if(source instanceof ExtraDamageSource extra && extra.isNotBlock()){
            event.setNewDamage(Math.max(event.getOriginalDamage(), event.getNewDamage()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLowestLivingBlock(LivingShieldBlockEvent event){
        DamageSource source = event.getDamageSource();
        if(source instanceof ExtraDamageSource extra && extra.isNotBlock()){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLowLivingUseTotem(LivingUseTotemEvent event){
        DamageSource source = event.getSource();
        if(source instanceof ExtraDamageSource extra && extra.getOnDeath() == ExtraDamageSource.DeathAction.JUST_DIE){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event){
        DamageSource source = event.getSource();
        LivingEntity target = event.getEntity();
        if(source instanceof ExtraDamageSource extra && extra.getOnDeath() == ExtraDamageSource.DeathAction.NOT_CAUSE_DEATH){
            target.setHealth(1);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST,receiveCanceled = true)
    public static void onLowestLivingDeath(LivingDeathEvent event){
        DamageSource source = event.getSource();
        LivingEntity target = event.getEntity();
        if(event.isCanceled() && source instanceof ExtraDamageSource extra && extra.getOnDeath() == ExtraDamageSource.DeathAction.JUST_DIE){
            target.setHealth(0);
            event.setCanceled(false);
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event){
        if(event.getRayTraceResult() instanceof EntityHitResult result){
            Entity entity = result.getEntity();
            if(event.getProjectile() instanceof AbstractArrow arrow && entity instanceof Player player && StarOfTheSea.isCanBlock(player)){
                event.setCanceled(true);
                arrow.level().playSound(null,arrow, SoundEvents.EXPERIENCE_ORB_PICKUP,arrow.getSoundSource(),0.8F,1);
                if(player.getTicksUsingItem() <= 20 && arrow.getOwner() instanceof LivingEntity attacker && attacker.isAlive()){
                    arrow.setOwner(player);
                    arrow.setDeltaMovement(attacker.getEyePosition().subtract(arrow.position()).normalize().scale(3));
                } else {
                    arrow.setDeltaMovement(arrow.getDeltaMovement().scale(-1));
                }
            }
        }
    }
}
