package dev.anvilcraft.tofusthinking.entity.projectile;

import dev.anvilcraft.tofusthinking.api.entity.SeekableEntity;
import dev.anvilcraft.tofusthinking.entity.livingEntity.StrangeWither;
import dev.anvilcraft.tofusthinking.init.entity.AddonDamageTypes;
import dev.anvilcraft.tofusthinking.init.entity.AddonEntities;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import dev.anvilcraft.tofusthinking.item.weapon.StarOfTheSea;
import dev.anvilcraft.tofusthinking.util.EntityUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class StrangeWitherSkull extends AbstractHurtingProjectile implements SeekableEntity {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(StrangeWitherSkull.class, EntityDataSerializers.BOOLEAN);

    public StrangeWitherSkull(EntityType<? extends StrangeWitherSkull> entityType, Level level) {
        super(entityType, level);
    }

    public StrangeWitherSkull(Level level,LivingEntity shooter, boolean isDangerous){
        this(AddonEntities.STRANGE_WITHER_SKULL.get(), level);
        this.accelerationPower = 0.05;
        this.setOwner(shooter);
        this.setDangerous(isDangerous);
        if(isDangerous){
            this.setDamage(5);
        } else {
            this.setBounceTimes(1);
        }
    }

    protected float damage = 10;
    protected int bounceTimes = 0;
    protected boolean hasReach = false;
    private int deathTime = 200;

    @javax.annotation.Nullable
    private UUID seekUUID;
    @javax.annotation.Nullable
    private Entity seekTarget;

    @Override
    public void tick() {
        super.tick();
        if(!this.level().isClientSide){
            Vec3 currentMotion = this.getDeltaMovement();
            double speed = currentMotion.length();
            if(speed >= 0.25){this.accelerationPower = 0.03;}
            if(this.tickCount % 20 == 0){
                Entity target = this.getSeekTarget();
                if(target != null){
                    if(!target.isAlive() || target.isInvulnerable()){discard();return;}
                    if(this.bounceTimes > 0){
                        if(target.isAlive() && target.level().dimension() == this.level().dimension()){
                            this.bounceTimes--;
                            Vec3 newMotion = target.getEyePosition().subtract(this.position()).normalize();
                            this.setDeltaMovement(newMotion.scale(speed));
                        }
                    }
                    if(!hasReach && this.isDangerous()){
                        Vec3 offset = target.getEyePosition().subtract(this.position());
                        double distance = offset.length();
                        if(distance < 4){hasReach = true;}
                        double rate = Mth.lerp((distance - 4)/ 20,0.1,0.4);
                        this.setDeltaMovement(currentMotion.scale(1- rate).add(offset.normalize().scale(rate)));
                    }
                }

                if(speed <= 0.1){this.discard();}
            }
            if(this.tickCount >= this.deathTime){discard();}
        }
        if(this.level().isClientSide){
            if(this.tickCount % 3 == 0){
                SimpleParticleType type = this.isDangerous() ? ParticleTypes.ENCHANT : ParticleTypes.END_ROD;
                this.level().addParticle(type,this.getX(),this.getY(),this.getZ(),0,0,0);
            }
        }
    }

    @Override
    protected float getInertia() {
        return 0.98F;
    }

    @Override
    protected float getLiquidInertia() {
        return 0.98F;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }


    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level() instanceof ServerLevel) {
            if(this.getDeltaMovement().length() < 0.1){return;}
            Entity entity = result.getEntity();
            if(this.getOwner() instanceof StrangeWither wither && wither.getHealth() > wither.getMaxHealth() * 0.05F){wither.setHealth(wither.getHealth() - wither.getMaxHealth() * 0.02F);}
            if(entity instanceof Player player && dealBlock(player)){return;}
            dealDamage();
        }
    }

    private void dealDamage(){
        if(this.level().isClientSide){return;}
        Entity owner = this.getOwner();
        if(owner instanceof LivingEntity && owner.isAlive()){
            Vec3 location = this.position();
            if(this.isDangerous()){
                this.level().getEntitiesOfClass(LivingEntity.class,new AABB(location.add(2,2,2),location.subtract(2,2,2))).forEach(
                        target -> {
                            if(target == owner || owner.isAlliedTo(this)){return;}
                            EntityUtil.clearAllEffect(target);
                            DamageSource source = AddonDamageTypes.rewindAttack(this.level(),this,owner).withNotBlock(true).justDie();
                            float addAmount = target instanceof Player || target instanceof OwnableEntity ? target.getMaxHealth() * 0.03F : 0;
                            target.hurt(source,this.damage + addAmount);
                            target.invulnerableTime = 0;
                        }
                );
            } else {
                this.level().getEntitiesOfClass(LivingEntity.class,new AABB(location.add(1,1,1),location.subtract(1,1,1))).forEach(
                        target -> {
                            if(target == owner || owner.isAlliedTo(this)){return;}
                            DamageSource source = AddonDamageTypes.bounceWitherSkull(this.level(),this,owner);
                            float addAmount = target instanceof Player || target instanceof OwnableEntity ? target.getMaxHealth() * 0.03F : 0;
                            target.hurt(source,this.damage + addAmount);
                            target.invulnerableTime = 0;
                        }
                );
            }
        }
    }

    private boolean dealBlock(Player player){
        if(player.getUseItem().is(AddonItems.STAR_OF_THE_SEA.get()) && player.getTicksUsingItem() >= 5 && player.getTicksUsingItem() <= 20){
            if(isDangerous()){
                this.discard();
                if(this.getOwner() instanceof StrangeWither wither){
                    ItemStack stack = player.getUseItem();
                    UUID recordUUID = stack.get(AddonComponents.UUID);
                    if(!wither.getUUID().equals(recordUUID)){
                        StarOfTheSea.clear(stack);
                        stack.set(AddonComponents.UUID,wither.getUUID());
                    }
                    StarOfTheSea.addNumberProgress(player.getUseItem(), (byte) 2);
                }

            } else {
                this.setDeltaMovement(this.getDeltaMovement().scale(-1));
                this.bounceTimes = 0;
            }
            this.level().playSound(null,player.getX(),player.getY(),player.getZ(), SoundEvents.SHIELD_BLOCK,player.getSoundSource(),1,1);
            return true;
        }
        return false;
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        if(result.getType() == HitResult.Type.ENTITY){
            super.onHit(result);
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_DANGEROUS, false);
    }

    public boolean isDangerous() {
        return this.entityData.get(DATA_DANGEROUS);
    }

    public void setDangerous(boolean invulnerable) {
        this.entityData.set(DATA_DANGEROUS, invulnerable);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setBounceTimes(int bounceTimes) {
        this.bounceTimes = bounceTimes;
    }

    public void setDeathTime(int deathTime) {
        this.deathTime = deathTime;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public boolean isCurrentlyGlowing() {return true;}

    @Override
    public int getTeamColor() {
        return isDangerous() ? 0xF8F8FF : 0x4F4F4F;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.seekUUID != null) {
            compound.putUUID("Owner", this.seekUUID);
        }
        compound.putBoolean("dangerous", this.isDangerous());
        compound.putFloat("damage",this.damage);
        compound.putBoolean("has_reach",this.hasReach);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("Owner")) {
            this.seekUUID = compound.getUUID("Owner");
            this.seekTarget = null;
        }
        this.setDangerous(compound.getBoolean("dangerous"));
        this.damage = compound.getFloat("damage");
        this.hasReach = compound.getBoolean("has_reach");
    }

    @Override
    public void setSeekTarget(@Nullable Entity entity) {
        if (entity != null) {
            this.seekUUID = entity.getUUID();
            this.seekTarget = entity;
        }
    }

    @Override
    public @Nullable Entity getSeekTarget() {
        if (this.seekTarget != null && !this.seekTarget.isRemoved()) {
            return this.seekTarget;
        } else if (this.seekUUID != null && this.level() instanceof ServerLevel serverlevel) {
            this.seekTarget = serverlevel.getEntity(this.seekUUID);
            return this.seekTarget;
        } else {
            return null;
        }
    }

}
