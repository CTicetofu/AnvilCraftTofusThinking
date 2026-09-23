package dev.anvilcraft.tofusthinking.item.weapon.StaffProcess;

import dev.anvilcraft.tofusthinking.entity.ExtraDamageSource;
import dev.anvilcraft.tofusthinking.entity.projectile.Meteor;
import dev.anvilcraft.tofusthinking.init.AddonMobEffects;
import dev.anvilcraft.tofusthinking.util.EntityUtil;
import dev.anvilcraft.tofusthinking.util.ItemUtil;
import dev.dubhe.anvilcraft.block.ExpFluidBlock;
import dev.dubhe.anvilcraft.init.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.EventHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


public class Abilities {
    public static final AbilityHandler NONE = AbilityHandler.builder("none").build();

    public static final AbilityHandler TINNED_GLASS = AbilityHandler.builder("tinned_glass").setPreHit(Abilities::tinnedTarget).setParticle(ParticleTypes.SMOKE).build();
    public static final AbilityHandler ROYAL_GLASS = AbilityHandler.builder("royal_glass").setDiscount(0.2F).build();
    public static final AbilityHandler FROST_GLASS = AbilityHandler.builder("frost_glass").setPreHit(Abilities::disintegrateTarget).setParticle(ParticleTypes.SNOWFLAKE).build();
    public static final AbilityHandler EMBER_GLASS = AbilityHandler.builder("ember_glass").setPreHit(Abilities::summonMeteor).setParticle(ParticleTypes.FLAME).build();

    public static final AbilityHandler CURSE_GOLD_BLOCK = AbilityHandler.builder("curse_gold_block").setPreHit(Abilities::curseTarget).setModifyDamageSource(ExtraDamageSource::justDie).build();
    public static final AbilityHandler ROYAL_STEEL_BLOCK = AbilityHandler.builder("royal_steel_block").setOnRaycast(Abilities::reFindTarget).build();
    public static final AbilityHandler FROST_METAL_BLOCK = AbilityHandler.builder("frost_metal_block").setInventorySec(Abilities::frostSec).build();
    public static final AbilityHandler EMBER_METAL_BLOCK = AbilityHandler.builder("ember_metal_block").setInventorySec(Abilities::emberSec).build();

    private static void tinnedTarget(StaffContext context){
        for(LivingEntity target:context.targets){
            if(target instanceof Player){continue;}
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,300,1));
            target.addEffect(new MobEffectInstance(AddonMobEffects.COVER,300,1));
        }
    }

    private static void disintegrateTarget(StaffContext context){
        if(!(context.level instanceof ServerLevel serverLevel) || !(context.attacker instanceof Player player)){return;}
        for(LivingEntity target:context.targets){
            if(target instanceof Mob mob){
                if(!target.isAlive() || EntityUtil.BELONG_PLAYER.test(target) || target.getType().is(Tags.EntityTypes.BOSSES)){continue;}
                if(EntityUtil.getOriginMaxHealth(target) > 200){return;}
                int xp = EventHooks.getExperienceDrop(target,player,mob.getExperienceReward(serverLevel,context.attacker)) * 4;
                Vec3 pos = target.position();
                mob.dropPreservedEquipment();
                EntityUtil.eraseLivingEntity(target);
                if(target.isRemoved() || xp <= 0){continue;}
                List<ItemEntity> stacks = new ArrayList<>();
                int maxSize = EXP_STACK.get().getMaxStackSize();
                int maxAccept = ExpFluidBlock.XP_POINTS * maxSize * 9;
                int count = Math.min(maxAccept, xp) / ExpFluidBlock.XP_POINTS;
                int left = xp - count * ExpFluidBlock.XP_POINTS;
                while (count > 0){
                    stacks.add(new ItemEntity(serverLevel,pos.x,pos.y,pos.z,EXP_STACK.get().copyWithCount(Math.min(count,maxSize))));
                    count -= maxSize;
                }
                if(left > 0){
                    if(xp > maxAccept){
                        ExperienceOrb orb = new ExperienceOrb(serverLevel,pos.x,pos.y,pos.z,left);
                        serverLevel.addFreshEntity(orb);
                    } else {
                        if(context.level.getRandom().nextInt(50) < left){
                            stacks.add(new ItemEntity(serverLevel,pos.x,pos.y,pos.z,EXP_STACK.get().copyWithCount(1)));
                        }
                    }
                }
                stacks.forEach(item -> {
                    item.setNoPickUpDelay();
                    serverLevel.addFreshEntity(item);
                });
            }
        }
    }

    private static void curseTarget(StaffContext context){
        for(LivingEntity target:context.targets){
            EntityUtil.clearPredicateEffect(target,instance -> instance.getEffect().value().isBeneficial());
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,200,1));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,200,1));
            target.addEffect(new MobEffectInstance(MobEffects.HUNGER,200,1));
            target.addEffect(new MobEffectInstance(AddonMobEffects.CURSE.getDelegate(),200,1));
        }
    }

    private static void reFindTarget(StaffContext context){
        if(context.hitResult.getType() != HitResult.Type.ENTITY){
            Vec3 location = context.hitResult.getLocation();
            List<Monster> monsters = context.level.getEntitiesOfClass(Monster.class,new AABB(location.add(5,5,5),location.subtract(5,5,5)), monster -> !monster.isAlliedTo(context.attacker) && monster.isAlive());
            if(!monsters.isEmpty()){
                Monster monster = monsters.getFirst();
                context.hitResult = new EntityHitResult(monster,monster.position());
            }
        }
    }

    private static void summonMeteor(StaffContext context){
        Vec3 targetPos = context.hitResult.getLocation();
        double angle = Math.random() * 2 * Math.PI;
        Vec3 offset = new Vec3(3 * Math.sin(angle),8,3 * Math.cos(angle));
        Meteor meteor = new Meteor(context.level,context.attacker);
        meteor.setPos(targetPos.add(offset));
        meteor.setDeltaMovement(offset.normalize().scale(-1));
        context.level.addFreshEntity(meteor);
    }

    private static void emberSec(ItemStack stack,Level level,LivingEntity entity, int slotId, boolean isSelected){
        if(level.dimensionType().ultraWarm() || entity.isInLava() || entity.isOnFire()){
            ItemUtil.addEnergy(stack,40000);
        }
        entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,300,0,false,false,true));
    }

    private static void frostSec(ItemStack stack,Level level,LivingEntity entity, int slotId, boolean isSelected){
        entity.setRemainingFireTicks(Math.min(entity.getRemainingFireTicks(),-100));
        entity.setTicksFrozen(Math.min(entity.getTicksFrozen(),-100));
        entity.addEffect(new MobEffectInstance(AddonMobEffects.TEMPERATURE_TOLERANCE.getDelegate(),300,0,false,false,true));
    }

    private final static Supplier<ItemStack> EXP_STACK = ModItems.EXP_GEM::asStack;
}
