package dev.anvilcraft.tofusthinking.entity.livingEntity;

import dev.anvilcraft.tofusthinking.entity.projectile.StrangeWitherSkull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class StrangeWither extends WitherBoss {
    public StrangeWither(EntityType<? extends StrangeWither> entityType, Level level) {
        super(entityType, level);
    }
    private int liveSecond = 140;
    private int strandTimes = 0;
    private int shootCooldown = 0;
    private Vec3 lastPosition = Vec3.ZERO;
    private int normalSkullAmount = 0;
    private boolean nextDangerous = false;

    @Override
    protected void dropCustomDeathLoot(@NotNull ServerLevel level, @NotNull DamageSource damageSource, boolean recentlyHit) {}

    @Override
    public void aiStep() {
        super.aiStep();
        if(!this.level().isClientSide){
            this.noPhysics = this.getTarget() != null;
            if(shootCooldown > 0){shootCooldown--;}
            if (this.tickCount % 20 == 0) {
                if(!this.noPhysics && this.isInWall()){this.setPos(this.position().add(0,2,0));}
                if(liveSecond-- <= 0 || this.isNoAi()){
                    this.kill();
                }
                if(this.getInvulnerableTicks() > 0){
                    this.setHealth(this.getHealth() + this.getMaxHealth() * 0.05F);
                } else {
                    if(this.getHealth() > this.getMaxHealth() * 0.05F){
                        this.setHealth(this.getHealth() - this.getMaxHealth() * 0.008F);
                    } else {
                        this.kill();
                    }
                }
            }
        }
    }



    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if(this.getInvulnerableTicks() <= 0 && this.getTarget() != null){
            if(this.tickCount % 20 == 0){
                if(this.lastPosition == Vec3.ZERO){this.lastPosition = this.position();}
                if(this.getTarget() != null && this.lastPosition.distanceToSqr(this.position()) < 0.3){
                    this.strandTimes += 2;
                } else {
                    if(this.strandTimes > 0){strandTimes--;}
                    this.lastPosition = this.position();
                }
                if(this.strandTimes >= 10){
                    this.strandTimes = 10;
                    if(this.getHealth() > this.getMaxHealth() * 0.05F){
                         this.setHealth(this.getHealth() - this.getMaxHealth() * 0.05F);
                    }
                }
            }
        }
    }

    @Override
    public void kill() {
        super.kill();
        if(this.isAlive()){
            this.discard();
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if(!this.level().isClientSide && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)){
            if(this.getHealth() > this.getMaxHealth() * 0.1F){
                this.setHealth(Math.max(this.getHealth() - 4 * amount,this.getMaxHealth() * 0.1F));
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(@NotNull DamageSource damageSource) {
        this.lastHurtByPlayerTime = 0;
        this.lastHurtByPlayer = null;
        super.die(this.damageSources().genericKill());
    }

    @Override
    public void heal(float healAmount) {
        if(this.getInvulnerableTicks() > 0){
            super.heal(healAmount);
        }
    }

    @Override
    public boolean canFreeze() {return false;}

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if(target == null || target instanceof Player){
            super.setTarget(target);
        }
    }

    @Override
    public @Nullable LivingEntity getTarget() {
        if(strandTimes <= 4){
            return super.getTarget();
        } else {
            return null;
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new StrangeWither.WitherDoNothingGoal());
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0, 40, 20.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 1, false, false,(player) -> true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 600.0)
                .add(Attributes.MOVEMENT_SPEED, 0.45F)
                .add(Attributes.FLYING_SPEED, 0.45F)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.ARMOR_TOUGHNESS, 0.0);
    }

    @Override
    public void performRangedAttack(int head, double x, double y, double z, boolean isDangerous) {
        if(shootCooldown > 0){return;}
        if (!this.isSilent()) {
            this.level().levelEvent(null, 1024, this.blockPosition(), 0);
        }
        if(!nextDangerous){
            normalSkullAmount++;
            shootCooldown = 20;
        } else {
            normalSkullAmount = 0;
            shootCooldown = 80;
        }

        if(this.getTarget() == null){
            Player player = this.level().getNearestPlayer(this,40);
            this.setTarget(player);
        }

        double d0 = this.getHeadX(head);
        double d1 = this.getHeadY(head);
        double d2 = this.getHeadZ(head);
        double d3 = x - d0;
        double d4 = y - d1;
        double d5 = z - d2;
        Vec3 vec3 = new Vec3(d3, d4, d5);
        StrangeWitherSkull skull = new StrangeWitherSkull(this.level(), this, nextDangerous);
        skull.setPosRaw(d0, d1, d2);
        skull.moveTo(x, y, z, this.getYRot(), this.getXRot());
        skull.setPos(d0,d1,d2);
        skull.setDeltaMovement(vec3.normalize().scale(0.15F));
        if(head == 0 && this.getTarget() != null){skull.setSeekTarget(this.getTarget());}
        this.level().addFreshEntity(skull);
        nextDangerous = this.random.nextInt(4) < normalSkullAmount - 2;
        if(nextDangerous){
            this.bossEvent.setColor(BossEvent.BossBarColor.BLUE);
            shootCooldown = 50;
        } else {
            this.bossEvent.setColor(BossEvent.BossBarColor.PURPLE);
        }
    }

    @Override
    public boolean isCurrentlyGlowing() {return true;}

    @Override
    public boolean isPowered() {return this.level().isClientSide;}

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if(compound.contains("liveSecond")){
            this.liveSecond = compound.getInt("liveSecond");
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("liveSecond",this.liveSecond);
    }

    class WitherDoNothingGoal extends Goal {
        public WitherDoNothingGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return StrangeWither.this.getInvulnerableTicks() > 0 || StrangeWither.this.shootCooldown > 20;
        }
    }

}
