package dev.anvilcraft.tofusthinking.entity.projectile;

import dev.anvilcraft.tofusthinking.entity.FriendlyLightningBolt;
import dev.anvilcraft.tofusthinking.init.entity.AddonDamageTypes;
import dev.anvilcraft.tofusthinking.init.entity.AddonEntities;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.item.property.component.ProjectileInfo;
import dev.anvilcraft.tofusthinking.util.UnclassifiedUtil;
import dev.dubhe.anvilcraft.init.item.ModItemTags;
import dev.dubhe.anvilcraft.init.item.ModItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ElectromagneticProjectile extends AbstractHitProjectile{
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(ElectromagneticProjectile.class, EntityDataSerializers.ITEM_STACK);
    public ElectromagneticProjectile(EntityType<? extends ElectromagneticProjectile> entityType, Level level) {
        super(entityType, level);
        this.soundEvent = this.getDefaultHitGroundSoundEvent();
    }

    public ElectromagneticProjectile(Level level, LivingEntity shooter,ItemStack self,ItemStack weapon){
        this(AddonEntities.ELECTROMAGNETIC_PROJECTILE.get(),level);
        this.setOwner(shooter);
        this.setCustomName(self.getDisplayName());
        this.setItem(self);
        if (weapon != null && level instanceof ServerLevel serverlevel) {
            if(weapon.isEmpty()){throw new IllegalArgumentException("Invalid weapon firing an projectile");}
            this.weapon = weapon.copy();
            int i = EnchantmentHelper.getPiercingCount(serverlevel, weapon, self.copy());
            if (i > 0) {
                this.setPierceLevel((byte)i);
            }
            Arrow arrow = new Arrow(EntityType.ARROW,serverlevel);
            EnchantmentHelper.onProjectileSpawned(serverlevel,weapon,arrow,(item) -> this.weapon = null);
            arrow.firedFromWeapon = this.weapon;
            if(arrow.isOnFire()){
                this.setSharedFlagOnFire(true);
                this.setRemainingFireTicks(100);
            }

            ProjectileInfo info = weapon.getOrDefault(AddonComponents.PROJECTILE_INFO,ProjectileInfo.DEFAULT);
            this.isNugget = self.is(Tags.Items.NUGGETS);
            if (!this.isNugget) {
                this.setSoundEvent(SoundEvents.AMETHYST_BLOCK_HIT);
            }
            this.damage = info.base();
            this.type = EnumUtils.getEnum(SpecialType.class, info.type(),SpecialType.NONE);
        }
    }
    private SoundEvent soundEvent;
    private float damage = 10F;
    private boolean isNugget = true;
    @Nullable
    public ItemStack weapon;
    private boolean isMain = false;
    private SpecialType type = SpecialType.NONE;

    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ITEM_STACK, this.getDefaultItem());
    }

    @Override
    public void tick() {
        if(this.tickCount > 300){discard();}
        super.tick();
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        switch (this.type){
            case LIGHTNING -> takeLightning(result);
            case FREEZE -> takeFreeze(result);
            case BURN -> takeBurn(result);
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);
        if(!this.isSilent()){
            this.playSound(this.getSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        }
        destroy();
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        if(entity.level() instanceof ServerLevel level){
            if(!this.isSilent()){
                this.playSound(this.getSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            }
            DamageSource source = isNugget ? AddonDamageTypes.nugget(level,this,this.getOwner()) : AddonDamageTypes.gemMissile(level,this,this.getOwner());
            if (this.weapon != null) {
                this.damage *= EnchantmentHelper.modifyDamage(level, this.weapon, entity, source, 2);
                System.out.println(EnchantmentHelper.modifyDamage(level, this.weapon, entity, source, 2));
            }
            int fire = this.isOnFire() ? 5 : 0;
            float rate = 1;
            if(entity instanceof LivingEntity living){
                switch (this.type){
                    case RADIATION -> {
                        living.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 1));
                        float cover = living.getArmorCoverPercentage();
                        if(cover < 1){
                            rate += 1 - cover;
                        }
                    }
                    case SLIVER -> {if(living.getType().is(EntityTypeTags.UNDEAD)){rate *= 1.5F;}}
                    case EMBER -> fire += 10;
                    case CURSE -> living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,200,1));
                }
            }
            fire *= 20;
            if(entity.getRemainingFireTicks() < fire){entity.setRemainingFireTicks(fire);}
            if(entity.hurt(source,this.damage * rate)){
                if(entity instanceof LivingEntity living){
                    doKnockback(living,source);
                    EnchantmentHelper.doPostAttackEffectsWithItemSource(level, living, source, this.weapon);
                }
            }
            doPierce();
        }
    }

    protected void doKnockback(LivingEntity entity, DamageSource damageSource) {
        float knock = 0;
        if (this.weapon != null) {
            if (this.level() instanceof ServerLevel serverlevel) {
                knock = EnchantmentHelper.modifyKnockback(serverlevel, this.weapon, entity, damageSource, 0.0F);
            }
        }
        if (knock > (double)0.0F) {
            double resistance = Math.max(0.0F, (double)1.0F - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            Vec3 vec3 = this.getDeltaMovement().multiply(1.0F, 0.0F, 1.0F).normalize().scale(knock * 0.6 * resistance);
            if (vec3.lengthSqr() > (double)0.0F) {
                entity.push(vec3.x, 0.1, vec3.z);
            }
        }
    }

    @Override
    public void destroy() {
        if(this.level().isClientSide){
            ItemStack stack = this.getItem().isEmpty() ? this.getDefaultItem() : this.getItem();
            ParticleOptions particleoptions = new ItemParticleOption(ParticleTypes.ITEM, stack);
            for (int i = 0; i < 8; i++) {
                this.level().addParticle(particleoptions, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
        super.destroy();
    }

    private void takeLightning(HitResult result){
        if(this.level() instanceof ServerLevel level){
            this.setPos(result.getLocation());
            FriendlyLightningBolt bolt = new FriendlyLightningBolt(level,this.getOwner());
            bolt.setDamage(this.damage * 1.5F);
            bolt.setPos(this.position());
            level.addFreshEntity(bolt);
            float startAngel = this.random.nextFloat() * Mth.PI * 2;
            for (int i = 0; i < 8; i++) {
                float angel = startAngel + 45 * i + 0.3F * this.random.nextFloat() - 0.15F;
                float distance = this.random.nextFloat() + 6;
                FriendlyLightningBolt otherBolt = new FriendlyLightningBolt(level,this.getOwner());
                Vec3 offset = new Vec3(Math.cos(angel),0,Math.sin(angel)).scale(distance);
                otherBolt.setPos(this.position().add(offset));
                otherBolt.setDamage(this.damage * 1.5F + 5);
                otherBolt.setSilent(true);
                level.addFreshEntity(otherBolt);
            }
        }
        destroy();
    }

    private void takeFreeze(HitResult result){
        if(this.level() instanceof ServerLevel level){
            this.setPos(result.getLocation());
            Entity owner = this.getOwner();
            level.getEntitiesOfClass(LivingEntity.class,this.getBoundingBox().inflate(8)).forEach(living -> {
                if(this.canHitEntity(living)){
                    living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,300,2));
                    living.setTicksFrozen(living.getTicksFrozen() + 1000);
                    if(isMain){living.invulnerableTime = 0;}
                    living.hurt(new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.FREEZE),this,owner),this.damage * 1.5F + 7);
                }
            });
            UnclassifiedUtil.spawnCenterParticles(level,ParticleTypes.SNOWFLAKE,this.getX(),this.getY(),this.getZ(),0,0,0.6F,0.4F,60,false);
        }
        destroy();
    }

    private void takeBurn(HitResult result){
        if(this.level() instanceof ServerLevel level){
            this.setPos(result.getLocation());
            Entity owner = this.getOwner();
            level.getEntitiesOfClass(LivingEntity.class,this.getBoundingBox().inflate(8)).forEach(living -> {
                if(this.canHitEntity(living)){
                    if(living.invulnerableTime <= 10){
                        for (EquipmentSlot slot:EquipmentSlot.values()){
                            ItemStack stack = living.getItemBySlot(slot);
                            if(!stack.isEmpty()){stack.hurtAndBreak((int) (200 + stack.getMaxDamage() * 0.3F),living,slot);}
                        }
                    }
                    if(living.getRemainingFireTicks() < 800){living.setRemainingFireTicks(800);}
                    if(isMain){living.invulnerableTime = 0;}
                    living.hurt(new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.ON_FIRE),this,owner),this.damage * 2F + 8);
                    living.invulnerableTime = Math.max(living.invulnerableTime,15);
                }
            });
            UnclassifiedUtil.spawnCenterParticles(level,ParticleTypes.FLAME,this.getX(),this.getY(),this.getZ(),0,0,0.6F,0.4F,60,false);
        }
        destroy();
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setItem(ItemStack stack) {
        this.getEntityData().set(DATA_ITEM_STACK, stack.copyWithCount(1));
    }

    public ItemStack getItem() {
        return this.getEntityData().get(DATA_ITEM_STACK);
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.ARROW_HIT;
    }

    public void setSoundEvent(SoundEvent soundEvent) {
        this.soundEvent = soundEvent;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    public SoundEvent getSoundEvent() {
        return soundEvent;
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("damage",this.damage);
        compound.putBoolean("is_nugget",this.isNugget);
        compound.putInt("PierceLevel",this.getPierceLevel());
        ResourceLocation sound = BuiltInRegistries.SOUND_EVENT.getKey(this.soundEvent);
        if(sound != null){
            compound.putString("SoundEvent", sound.toString());
        }
        compound.put("item", this.getItem().save(this.registryAccess()));
        if (this.weapon != null) {
            compound.put("weapon", this.weapon.save(this.registryAccess(), new CompoundTag()));
        }
        compound.putString("type",this.type.toString());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if(compound.contains("damage")){
            this.damage = compound.getFloat("damage");
        }
        this.isNugget = compound.getBoolean("is_nugget");
        this.setPierceLevel(compound.getInt("PierceLevel"));
        if (compound.contains("SoundEvent", 8)) {
            this.soundEvent = BuiltInRegistries.SOUND_EVENT.getOptional(ResourceLocation.parse(compound.getString("SoundEvent"))).orElse(this.getDefaultHitGroundSoundEvent());
        }
        if (compound.contains("item", 10)) {
            this.setItem(ItemStack.parse(this.registryAccess(), compound.getCompound("item")).orElse(this.getDefaultItem()));
        } else {
            this.setItem(this.getDefaultItem());
        }

        if (compound.contains("weapon", 10)) {
            this.weapon = ItemStack.parse(this.registryAccess(), compound.getCompound("weapon")).orElse(null);
        } else {
            this.weapon = null;
        }
        if(compound.contains("type")){
            String s = compound.getString("type");
            this.type = EnumUtils.getEnum(SpecialType.class,s,SpecialType.NONE);
        }
    }

    private ItemStack getDefaultItem() {
        return Items.IRON_NUGGET.getDefaultInstance();
    }

    public static SpecialType getTypeFormStack(ItemStack stack){
        if(stack.is(Tags.Items.NUGGETS)){
            if(stack.is(ModItems.EMBER_METAL_INGOT)){return SpecialType.EMBER;}
            if(stack.is(ModItems.CURSED_GOLD_NUGGET)){return SpecialType.CURSE;}
            if(stack.is(ModItemTags.PLUTONIUM_NUGGETS) || stack.is(ModItemTags.URANIUM_NUGGETS)){return SpecialType.RADIATION;}
            if(stack.is(ModItemTags.SILVER_NUGGETS)){return SpecialType.SLIVER;}
        } else if(stack.is(Tags.Items.GEMS)){
            if(stack.is(ModItemTags.GEMS_TOPAZ)){return SpecialType.LIGHTNING;}
            if(stack.is(ModItemTags.GEMS_RUBY)){return SpecialType.BURN;}
            if(stack.is(ModItemTags.GEMS_SAPPHIRE)){return SpecialType.FREEZE;}
        }
        return SpecialType.NONE;
    }

    public enum SpecialType{
        NONE,CURSE,EMBER,RADIATION,FREEZE,BURN,LIGHTNING,SLIVER
    }
}
