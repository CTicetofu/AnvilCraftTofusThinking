package dev.anvilcraft.tofusthinking.entity.projectile;

import dev.anvilcraft.tofusthinking.init.entity.AddonDamageTypes;
import dev.anvilcraft.tofusthinking.init.entity.AddonEntities;
import dev.anvilcraft.tofusthinking.util.UnclassifiedUtil;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
@SuppressWarnings("unused")
public class Meteor extends AbstractHurtingProjectile implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(Meteor.class, EntityDataSerializers.ITEM_STACK);

    public Meteor(EntityType<? extends Meteor> entityType, Level level) {
        super(entityType, level);
    }

    public Meteor(Level level, LivingEntity shooter){
        this(AddonEntities.METEOR.get(),level);
        this.setOwner(shooter);
    }

    protected float damage = 20;
    protected float radius = 5;

    public void setItem(ItemStack stack) {
        if (stack.isEmpty()) {
            this.getEntityData().set(DATA_ITEM_STACK, this.getDefaultItem());
        } else {
            this.getEntityData().set(DATA_ITEM_STACK, stack.copyWithCount(1));
        }
    }

    @Override
    public @NotNull ItemStack getItem() {
        return this.getEntityData().get(DATA_ITEM_STACK);
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        if(!this.level().isClientSide){
            UnclassifiedUtil.spawnCenterParticles(level(), ParticleTypes.FLAME,this.getX(),this.getY(),this.getZ(),0,0,0.4F,0.1F,50,false);
            doExplode();
            discard();
        }
    }

    protected void doExplode(){
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, this.getSoundSource(), 1F, 1);
        this.level().getEntities(this,this.getBoundingBox().inflate(radius)).forEach(entity -> {
            if(canHitEntity(entity)){
                float radio = 1 - this.distanceTo(entity) * 0.7F / this.radius;
                entity.hurt(AddonDamageTypes.meteor(this.level(),this,this.getOwner()),radio * damage);
                entity.invulnerableTime = 0;
            }
        });
    }

    @Override
    public void tick() {
        super.tick();
        if(this.level().isClientSide){
            this.level().addParticle(ParticleTypes.SMALL_FLAME,this.getX(),this.getY(),this.getZ(),0,0,0);
        } else {
            if(this.tickCount > 300){this.discard();}
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.005;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected float getLiquidInertia() {
        return 0.95F;
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity target) {
        if(target instanceof ItemEntity){return false;}
        return this.getOwner() == null || !this.getOwner().isAlliedTo(target);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ITEM_STACK, this.getDefaultItem());
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("Item", this.getItem().save(this.registryAccess()));
        compound.putInt("tickCount",this.tickCount);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Item", 10)) {
            this.setItem(ItemStack.parse(this.registryAccess(), compound.getCompound("Item")).orElse(this.getDefaultItem()));
        } else {
            this.setItem(this.getDefaultItem());
        }
        this.tickCount = compound.getInt("tickCount");
    }

    private ItemStack getDefaultItem() {
        return ModBlocks.EMBER_METAL_BLOCK.asStack();
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return false;
    }
}
