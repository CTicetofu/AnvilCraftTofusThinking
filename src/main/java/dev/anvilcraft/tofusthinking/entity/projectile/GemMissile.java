package dev.anvilcraft.tofusthinking.entity.projectile;

import dev.anvilcraft.tofusthinking.entity.ExtraDamageSource;
import dev.anvilcraft.tofusthinking.init.AddonMobEffects;
import dev.anvilcraft.tofusthinking.init.entity.AddonDamageTypes;
import dev.anvilcraft.tofusthinking.init.entity.AddonEntities;
import dev.anvilcraft.tofusthinking.util.EntityUtil;
import dev.anvilcraft.tofusthinking.util.UnclassifiedUtil;
import dev.dubhe.anvilcraft.init.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import org.jetbrains.annotations.NotNull;
import javax.annotation.Nullable;
import java.util.*;

public class GemMissile extends Projectile implements IEntityWithComplexSpawn {
    private static final EntityDataAccessor<Integer> TARGET = SynchedEntityData.defineId(GemMissile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(GemMissile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_PIERCE_LEVEL = SynchedEntityData.defineId(GemMissile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_SCALE = SynchedEntityData.defineId(GemMissile.class, EntityDataSerializers.INT);
    public GemMissile(EntityType<? extends GemMissile> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public GemMissile(Level level, LivingEntity attacker){
        this(AddonEntities.GEM_MISSILE.get(),level);
        this.setOwner(attacker);
    }

    private final HashMap<UUID,Integer> hitEntity = new HashMap<>();
    private int seekTime = 0;
    public boolean canBonus = false;
    public float damage = 5;
    public boolean canSeek = false;
    public boolean canFreeze = false;
    public boolean canExplode = false;
    public boolean canRetent = false;
    public boolean canSummon = false;
    public boolean canBright = false;
    public boolean ignoreBlock = false;
    private boolean isRetent = false;
    private int splitCount = 0;

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_COLOR,0xFFFFFFFF);
        builder.define(DATA_PIERCE_LEVEL,0);
        builder.define(TARGET,-1);
        builder.define(DATA_SCALE,10);
    }

    @Override
    public void tick() {
        if(this.tickCount > 200){discard();return;}
        if(this.isRetent){retentTick();return;}
        super.tick();
        if(!this.level().isClientSide && canSeek){
            if(tickCount % 5 == 0){updateTarget();}
        }
        doSeek();
        checkHitResult();
        travel();
    }

    private void retentTick(){
        Level level = this.level();
        if(!level.isClientSide && tickCount % 10 == 0){
            float rate = this.getScale() / 10F;
            if(rate <= 1){discard();return;}
            level.getEntities(this,this.getBoundingBox().inflate((rate - 1) * 0.5F),this::canHitEntity).forEach(
                    entity -> {
                        if(this.damageEntity(entity,this.damage)){
                            entity.invulnerableTime = 0;
                        }
                    });
            this.setScale(this.getScale() - 8);
        }
    }

    public void checkHitResult(){
        Vec3 pos = this.position();
        Vec3 target = pos.add(this.getDeltaMovement());
        HitResult hitresult = this.level().clip(new ClipContext(pos, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (!this.ignoreBlock && hitresult.getType() != HitResult.Type.MISS) {
            target = hitresult.getLocation();
        }
        List<EntityHitResult> entityHitResults = getAllEntityHitResult(target,pos);
        for (EntityHitResult entityHitResult : entityHitResults){
            if (!NeoForge.EVENT_BUS.post(new ProjectileImpactEvent(this, entityHitResult)).isCanceled()) {
                onHit(entityHitResult);
            }
            if (this.isRemoved()) {
                break;
            }
        }
        if (hitresult.getType() != HitResult.Type.MISS) {
            onHit(hitresult);
        }
    }

    protected void onHit(HitResult result) {
        HitResult.Type hitresult$type = result.getType();
        boolean hasHit = true;
        if (hitresult$type == HitResult.Type.ENTITY) {
            EntityHitResult entityhitresult = (EntityHitResult)result;
            this.onHitEntity(entityhitresult);
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, result.getLocation(), GameEvent.Context.of(this, null));
        } else if (hitresult$type == HitResult.Type.BLOCK && !this.ignoreBlock) {
            BlockHitResult blockhitresult = (BlockHitResult)result;
            this.onHitBlock(blockhitresult);
            BlockPos blockpos = blockhitresult.getBlockPos();
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockpos, GameEvent.Context.of(this, this.level().getBlockState(blockpos)));
        } else {
            hasHit = false;
        }
        if(hasHit && result.getType() != HitResult.Type.MISS){
            if(!this.level().isClientSide){
                this.level().playSound(null,this.getX(),this.getY(),this.getZ(), SoundEvents.AMETHYST_BLOCK_HIT,this.getSoundSource(),1.8F,1);
            }
            if(this.canExplode){doExplode();}
            if(this.canBright){doBright();}
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        if(ignoreBlock){ignoreBlock = false;}
        if(!this.level().isClientSide){
            Entity entity = result.getEntity();
            if(damageEntity(entity,this.damage)){
                doPierce();
            }
            if(this.canSeek && this.getTarget() == entity){this.setTarget(null);updateTarget();}
            if(this.canSummon){doSummon(entity,this.level());}
        }
    }

    protected boolean damageEntity(Entity entity,float damage){
        hitEntity.put(entity.getUUID(),entity.tickCount);
        int old = entity.invulnerableTime;
        entity.invulnerableTime = 0;
        ExtraDamageSource source = AddonDamageTypes.gemMissile(this.level(),this,this.getOwner());
        boolean hurt = entity.hurt(source,damage);
        entity.invulnerableTime = old;
        if(entity instanceof LivingEntity target){
            if(this.canFreeze){
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,200,1));
                entity.setTicksFrozen(entity.getTicksRequiredToFreeze() * 2);
            }
            if(this.canBright){
                target.addEffect(new MobEffectInstance(MobEffects.GLOWING,200));
                target.addEffect(new MobEffectInstance(AddonMobEffects.COVER,200));
            }
        }
        return hurt;
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);
        if(canBonus && this.getPierceLevel() > 0){
            switch (result.getDirection()) {
                case UP, DOWN ->
                        this.setDeltaMovement(this.getDeltaMovement().multiply(1, -1, 1));
                case EAST, WEST -> this.setDeltaMovement(this.getDeltaMovement().multiply(-1, 1, 1));
                case NORTH, SOUTH -> this.setDeltaMovement(this.getDeltaMovement().multiply(1, 1, -1));
            }
            doPierce();
        } else {
            if(this.canRetent && !this.level().isClientSide){
                GemMissile missile = this.copySelf();
                missile.isRetent = true;
                missile.setScale(50);
                missile.setPos(this.position());
                this.level().addFreshEntity(missile);
            }
            destroy();
        }
    }

    public void destroy(){
        if(!this.level().isClientSide){
            ParticleOptions options = ModParticles.PLASMA_JETS.get();
            UnclassifiedUtil.spawnCenterParticles(this.level(), options,this.getX(),this.getY(),this.getZ(),0,0F,0.2F,0.1F,10,false);
        }
        this.discard();
    }

    public void doPierce() {
        int p = getPierceLevel();
        if (p > 0) {
            setPierceLevel(p - 1);
        } else if (p == 0) {
            destroy();
        }
    }

    protected List<EntityHitResult> getAllEntityHitResult(Vec3 targetPoint, Vec3 position){
        float scale = this.getScale() / 10F;
        AABB range = this.getBoundingBox().expandTowards(targetPoint.subtract(position)).inflate(0.2 + 0.3F * (scale - 1));
        List<EntityHitResult> list = new ArrayList<>();
        for(Entity entity : this.level().getEntities(this, range, this::canHitEntity)) {
            AABB aabb = entity.getBoundingBox().inflate(0.15 + 0.15F * scale);
            Optional<Vec3> optional = aabb.clip(position, targetPoint);
            if (optional.isPresent()) {
                double d1 = position.distanceToSqr(optional.get());
                if (d1 < Double.MAX_VALUE) {
                    list.add(new EntityHitResult(entity,optional.get()));
                }
            }
        }
        return list;
    }

    public void travel(){
        this.setPos(this.position().add(this.getDeltaMovement()));
    }

    public void doSeek(){
        if(!canSeek){return;}
        Entity target = getTarget();
        if(target != null && target.isAlive()){
            Vec3 nowVec = this.getDeltaMovement();
            double speed = nowVec.length();
            seekTime++;
            Vec3 planVec = target.getEyePosition().subtract(position()).normalize().scale(Math.max(speed,0.3));
            if(seekTime < 40){
                this.setDeltaMovement(nowVec.scale(0.5).add(planVec.scale(0.5)));
            } else {
                this.setDeltaMovement(planVec);
            }
            if(seekTime == 40){
                this.setPos(target.getEyePosition().subtract(planVec));
                this.onHit(new EntityHitResult(target));
            }
        }
    }

    public void doExplode(){
        Level level = this.level();
        if(level.isClientSide){return;}
        level.getEntitiesOfClass(LivingEntity.class,this.getBoundingBox().inflate(4),this::canHitEntity).forEach(living -> {
            if(living.distanceToSqr(this) <= 16){
                if(EntityUtil.canSee(this,living,16)){this.damageEntity(living,this.damage * 0.5F);}
            }
        });
        level.getEntitiesOfClass(PartEntity.class,this.getBoundingBox().inflate(4),this::canHitEntity).forEach(part -> {
            if(part.distanceToSqr(this) <= 16){
                this.damageEntity(part,this.damage * 0.5F);
            }
        });
        ParticleOptions options = ParticleTypes.END_ROD;
        UnclassifiedUtil.spawnCenterParticles(level, options,this.getX(),this.getY(),this.getZ(),0,0F,0.4F,0.2F,20,false);
        this.level().playSound(null,this.getX(),this.getY(),this.getZ(), SoundEvents.WIND_CHARGE_BURST,this.getSoundSource(),1.8F,1);
    }

    public void doBright(){
        Level level = this.level();
        if(level.isClientSide){return;}
        level.getEntitiesOfClass(LivingEntity.class,this.getBoundingBox().inflate(4),this::canHitEntity).forEach(living -> {
            if(living.distanceToSqr(this) <= 16){
                living.addEffect(new MobEffectInstance(MobEffects.GLOWING,200));
            }
        });
    }

    public void doSummon(Entity entity,Level level){
        GemMissile missile = this.copySelf();
        float rad = this.random.nextFloat() * 2 * Mth.PI;
        Vec3 motion = new Vec3(Math.sin(rad),0,Math.cos(rad));
        missile.setPos(entity.getEyePosition().subtract(motion.scale(3)));
        missile.setDeltaMovement(motion.scale(this.getDeltaMovement().length()));
        missile.ignoreBlock = true;
        missile.canSummon = false;
        level.addFreshEntity(missile);
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity target) {
        if(target.isAlive() && target.isAttackable() && this.canHitThisTime(target)){
            Entity owner = this.getOwner();
            return owner == null || (target != owner && !owner.isAlliedTo(target));
        }
        return false;
    }

    public boolean canHitThisTime(@NotNull Entity target){
        Integer integer = hitEntity.get(target.getUUID());
        return integer == null || target.tickCount - integer >= 10;
    }

    private void updateTarget() {
        Entity target = this.getTarget();
        if (target != null && (!target.isAlive() || !this.canHitEntity(target) || this.distanceToSqr(target) > 4096)) {
            target = null;
            this.setTarget(null);
        }
        if(target == null){
            seekTime = 0;
            List<Mob> mobs = this.level().getEntitiesOfClass(Mob.class,this.getBoundingBox().inflate(10), mob -> canHitEntity(mob) && mob.isAlive() && !hitEntity.containsKey(mob.getUUID()));
            Entity owner = getOwner();
            if(getOwner() != null){
                for (Mob mob:mobs){
                    if(mob.getTarget() == owner){
                        this.setTarget(mob);
                        return;
                    }
                }
            }
            mobs.removeIf(mob -> EntityUtil.BELONG_PLAYER.test(mob));
            if(!mobs.isEmpty()){
                int index = random.nextInt(mobs.size());
                Mob mob = mobs.get(index);
                if(EntityUtil.canSee(this,mob,16)){this.setTarget(mobs.get(index));return;}
            }
            for (Mob mob:mobs){
                if(!EntityUtil.canSee(this,mob,16)){continue;}
                this.setTarget(mob);
            }
        }
    }

    public void doSplit(){
        if(this.splitCount < 1){return;}
        Vec3 planeVec3;
        if(this.getOwner() instanceof LivingEntity living){
            float f1 = -living.getYRot() * ((float)Math.PI / 180F);
            float f2 = Mth.cos(f1);
            float f3 = Mth.sin(f1);
            planeVec3 = new Vec3(f3,0,f2);
        } else {
            planeVec3 = this.getDeltaMovement().normalize().multiply(1,0,1);
            if(planeVec3.x < 0.01 && planeVec3.z < 0.01){
                planeVec3 = new Vec3(1,0,0);
            }
        }
        float scale = this.getScale() / 10F;
        Vec3 offset = planeVec3.normalize().yRot(Mth.PI / 2).scale(0.4 * scale);
        Vec3 start = this.position().add(offset.scale(0.5F * this.splitCount));
        this.setPos(start);
        for (int i = 0; i < this.splitCount; i++) {
            GemMissile missile = this.copySelf();
            missile.setDeltaMovement(this.getDeltaMovement());
            missile.setPos(start.subtract(offset.scale(i + 1)));
            this.level().addFreshEntity(missile);
        }
        this.splitCount = 0;
    }

    public GemMissile copySelf(){
        GemMissile missile = new GemMissile(AddonEntities.GEM_MISSILE.get(),this.level());
        missile.canBonus = this.canBonus;
        missile.canSeek = this.canSeek;
        missile.canFreeze = this.canFreeze;
        missile.canExplode = this.canExplode;
        missile.damage = this.damage;
        missile.setOwner(this.getOwner());
        missile.setPierceLevel(this.getPierceLevel());
        missile.setColor(this.getColor());
        missile.setTarget(this.getTarget());
        missile.setScale(this.getScale());
        return missile;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    private @Nullable Entity getTarget() {
        return this.level().getEntity(this.entityData.get(TARGET));
    }

    private void setTarget(@Nullable Entity e) {
        this.entityData.set(TARGET, e == null ? -1 : e.getId());
    }

    public void setScale(int scale){
        this.entityData.set(DATA_SCALE,scale);
    }

    public int getScale(){
        return this.entityData.get(DATA_SCALE);
    }

    public void setSplitCount(int splitCount) {
        this.splitCount = splitCount;
    }

    public int getSplitCount() {
        return splitCount;
    }

    public void setColor(int color){this.entityData.set(DATA_COLOR,color);}

    public int getColor(){return this.entityData.get(DATA_COLOR);}

    public void setPierceLevel(int level) {
        this.entityData.set(DATA_PIERCE_LEVEL, level);
    }

    public int getPierceLevel(){return this.entityData.get(DATA_PIERCE_LEVEL);}

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("tick_count",this.tickCount);
        compound.putFloat("damage",this.damage);
        compound.putBoolean("can_bonus",this.canBonus);
        compound.putBoolean("can_freeze",this.canFreeze);
        compound.putBoolean("can_seek",this.canSeek);
        compound.putBoolean("can_explode",this.canExplode);
        compound.putBoolean("can_summon",this.canSummon);
        compound.putBoolean("can_bright",this.canBright);
        compound.putBoolean("can_retent",this.canRetent);
        compound.putBoolean("is_retent",this.isRetent);
        compound.putBoolean("ignore_block",this.ignoreBlock);
        compound.putInt("scale",this.getScale());
        compound.putInt("color",this.getColor());
        compound.putInt("pierce_level",this.getPierceLevel());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.tickCount = compound.getInt("tick_count");
        this.damage = compound.getFloat("damage");
        this.canBonus = compound.getBoolean("can_bonus");
        this.canFreeze = compound.getBoolean("can_freeze");
        this.canSeek = compound.getBoolean("can_seek");
        this.canExplode = compound.getBoolean("can_explode");
        this.canSummon = compound.getBoolean("can_summon");
        this.canBright = compound.getBoolean("can_bright");
        this.canRetent = compound.getBoolean("can_retent");
        this.isRetent = compound.getBoolean("is_retent");
        this.ignoreBlock = compound.getBoolean("ignore_block");
        this.setScale(compound.getInt("scale"));
        this.setColor(compound.getInt("color"));
        this.setPierceLevel(compound.getInt("pierce_level"));
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(this.isRetent);
        buffer.writeBoolean(this.canBonus);
        buffer.writeBoolean(this.canSeek);
    }

    @Override
    public void readSpawnData(@NotNull RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        this.isRetent = registryFriendlyByteBuf.readBoolean();
        this.canBonus = registryFriendlyByteBuf.readBoolean();
        this.canSeek = registryFriendlyByteBuf.readBoolean();
    }

}
