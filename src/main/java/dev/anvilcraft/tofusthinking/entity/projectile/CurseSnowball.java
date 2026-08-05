package dev.anvilcraft.tofusthinking.entity.projectile;

import dev.anvilcraft.tofusthinking.init.AddonMobEffects;
import dev.anvilcraft.tofusthinking.init.entity.AddonEntities;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import dev.anvilcraft.tofusthinking.util.EntityUtil;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class CurseSnowball extends ThrowableItemProjectile {
    public CurseSnowball(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }
    public CurseSnowball(Level level,LivingEntity shooter){
        this(AddonEntities.CURSE_SNOWBALL.get(),level);
        this.setOwner(shooter);
    }
    @Override
    protected @NotNull Item getDefaultItem() {
        return AddonItems.CURSE_SNOWBALL_ITEM.asItem();
    }

    private ParticleOptions getParticle() {
        ItemStack itemstack = this.getItem();
        return !itemstack.isEmpty() && !itemstack.is(this.getDefaultItem())
                ? new ItemParticleOption(ParticleTypes.ITEM, itemstack)
                : new ItemParticleOption(ParticleTypes.ITEM, getDefaultItem().getDefaultInstance());
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ParticleOptions particleoptions = this.getParticle();

            for (int i = 0; i < 8; i++) {
                this.level().addParticle(particleoptions, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        if(!canHitEntity(entity)){return;}
        int i = entity instanceof Blaze ? 6 : 0;
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), (float)i);
        if(EntityUtil.getMainEntity(entity) instanceof LivingEntity target){
            if(target.invulnerableTime > 14){target.invulnerableTime = 14;}
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,200,1));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,200,1));
            target.addEffect(new MobEffectInstance(MobEffects.HUNGER,200,1));
            target.addEffect(new MobEffectInstance(AddonMobEffects.CURSE.getDelegate(),200,1));
            target.addEffect(new MobEffectInstance(AddonMobEffects.SHRINK.getDelegate(),200,1));
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity target) {
        return this.getOwner() == null || (target != this.getOwner() && !target.isAlliedTo(this.getOwner()));
    }
}
