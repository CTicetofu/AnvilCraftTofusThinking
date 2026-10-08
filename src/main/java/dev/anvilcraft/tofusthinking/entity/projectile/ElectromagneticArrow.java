package dev.anvilcraft.tofusthinking.entity.projectile;

import dev.anvilcraft.tofusthinking.init.entity.AddonEntities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ElectromagneticArrow extends Arrow{
    private static final EntityDataAccessor<Byte> ID_LOYALTY = SynchedEntityData.defineId(ElectromagneticArrow.class, EntityDataSerializers.BYTE);
    private boolean shouldReturn;
    private int clientSideReturnTridentTickCount;
    private boolean isMain = false;

    public ElectromagneticArrow(EntityType<? extends ElectromagneticArrow> entityType, Level level) {
        super(entityType, level);
    }
    public ElectromagneticArrow(Level level, LivingEntity shooter, ItemStack pickupItemStack, @Nullable ItemStack weapon) {
        this(AddonEntities.ELECTROMAGNETIC_ARROW.get(), level);
        this.setOwner(shooter);
        this.setPickupItemStack(pickupItemStack.copy());
        this.setCustomName(pickupItemStack.get(DataComponents.CUSTOM_NAME));
        Unit unit = pickupItemStack.remove(DataComponents.INTANGIBLE_PROJECTILE);
        if (unit != null) {
            this.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        } else {
            this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(weapon));
        }
        if (weapon != null && level instanceof ServerLevel serverlevel) {
            if (weapon.isEmpty()) {
                throw new IllegalArgumentException("Invalid weapon firing an arrow");
            }

            this.firedFromWeapon = weapon.copy();
            int i = EnchantmentHelper.getPiercingCount(serverlevel, weapon, this.getPickupItemStackOrigin());
            if (i > 0) {
                this.setPierceLevel((byte)i);
            }

            EnchantmentHelper.onProjectileSpawned(serverlevel, weapon, this, (p_348347_) -> this.firedFromWeapon = null);
        }
    }

    @Override
    public void tick() {
        if(this.inGroundTime > 30 && this.pickup != Pickup.ALLOWED){discard();return;}
        Entity entity = this.getOwner();
        int i = Math.min(this.entityData.get(ID_LOYALTY),20);
        if ((this.shouldReturn || this.isNoPhysics()) && entity != null) {
            if (!this.isAcceptedReturnOwner() && entity.level() == this.level()) {
                if (!this.level().isClientSide && this.pickup == Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }

                this.discard();
            } else {
                this.setNoPhysics(true);
                Vec3 vec3 = entity.getEyePosition().subtract(this.position());
                this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.015 * (double)i, this.getZ());
                if (this.level().isClientSide) {
                    this.yOld = this.getY();
                }

                double d0 = 0.05 * (double)i;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(vec3.normalize().scale(d0)));
                if (this.clientSideReturnTridentTickCount == 0) {
                    this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
                }

                ++this.clientSideReturnTridentTickCount;
            }
        }
        super.tick();
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        Entity entity = result.getEntity();
        if(isMain){entity.invulnerableTime = 0;}
        super.onHitEntity(result);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);
        int i = this.entityData.get(ID_LOYALTY);
        if(i > 0 && this.pickup == Pickup.ALLOWED){this.shouldReturn = true;}
    }

    protected boolean tryPickup(@NotNull Player player) {
        return super.tryPickup(player) || (this.shouldReturn && this.isNoPhysics() && this.ownedBy(player) && player.getInventory().add(this.getPickupItem()));
    }

    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_LOYALTY, (byte)0);
    }

    private byte getLoyaltyFromItem(ItemStack stack) {
        Level var3 = this.level();
        byte level;
        if (var3 instanceof ServerLevel serverlevel) {
            level = (byte) Mth.clamp(EnchantmentHelper.getTridentReturnToOwnerAcceleration(serverlevel, stack, this), 0, 127);
        } else {
            level = 0;
        }
        return level;
    }

    private boolean isAcceptedReturnOwner() {
        Entity entity = this.getOwner();
        return entity != null && entity.isAlive() && (!(entity instanceof ServerPlayer) || !entity.isSpectator());
    }

    public byte getLoyalty(){
        return this.entityData.get(ID_LOYALTY);
    }

    public void setLoyalty(byte level){
        this.entityData.set(ID_LOYALTY,level);
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("should_return",this.shouldReturn);
        compound.putBoolean("is_main",this.isMain);
        compound.putByte("loyalty",this.getLoyalty());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.shouldReturn = compound.getBoolean("should_return");
        this.isMain = compound.getBoolean("is_main");
        this.setLoyalty(compound.getByte("loyalty"));
    }
}
