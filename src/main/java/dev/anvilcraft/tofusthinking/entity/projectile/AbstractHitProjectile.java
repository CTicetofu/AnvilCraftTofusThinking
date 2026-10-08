package dev.anvilcraft.tofusthinking.entity.projectile;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractHitProjectile extends Projectile {
    private static final EntityDataAccessor<Integer> DATA_PIERCE_LEVEL = SynchedEntityData.defineId(AbstractHitProjectile.class, EntityDataSerializers.INT);
    public AbstractHitProjectile(EntityType<? extends AbstractHitProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public boolean ignoreBlock = false;

    @Override
    protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {
        builder.define(DATA_PIERCE_LEVEL,0);
    }

    @Override
    public void tick() {
        super.tick();
        checkHitResult();
        applyGravity();
        travel();
        adjustPoseMotion();
    }

    public void projectileTick(){super.tick();}

    public void travel(){
        this.setPos(this.position().add(this.getDeltaMovement()));
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

    protected List<EntityHitResult> getAllEntityHitResult(Vec3 targetPoint, Vec3 position){
        AABB range = this.getBoundingBox().expandTowards(targetPoint.subtract(position)).inflate(0.2 + 0.3F );
        List<EntityHitResult> list = new ArrayList<>();
        for(Entity entity : this.level().getEntities(this, range, this::canHitEntity)) {
            AABB aabb = entity.getBoundingBox().inflate(0.15);
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

    public void doPierce() {
        if(this.isRemoved()){return;}
        int p = getPierceLevel();
        if (p > 0) {
            setPierceLevel(p - 1);
        } else if (p == 0) {
            destroy();
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void adjustPoseMotion(){
        boolean flag = this.noPhysics;
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = vec3.horizontalDistance();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)180.0F / (double)(float)Math.PI));
            this.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)180.0F / (double)(float)Math.PI));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }
        double d5 = vec3.x;
        double d6 = vec3.y;
        double d1 = vec3.z;
        if (flag) {
            this.setYRot((float)(Mth.atan2(-d5, -d1) * (double)180.0F / (double)(float)Math.PI));
        } else {
            this.setYRot((float)(Mth.atan2(d5, d1) * (double)180.0F / (double)(float)Math.PI));
        }
        this.setXRot((float)(Mth.atan2(d6, d0) * (double)180.0F / (double)(float)Math.PI));
        this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
        this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
    }

    public void destroy(){discard();}

    @Override
    protected boolean canHitEntity(@NotNull Entity target) {
        if(target.isAlive()){
            Entity owner = this.getOwner();
            return owner == null || (target != owner && !owner.isAlliedTo(target));
        }
        return false;
    }

    public void setPierceLevel(int level) {
        this.entityData.set(DATA_PIERCE_LEVEL, level);
    }

    public int getPierceLevel(){return this.entityData.get(DATA_PIERCE_LEVEL);}
}
