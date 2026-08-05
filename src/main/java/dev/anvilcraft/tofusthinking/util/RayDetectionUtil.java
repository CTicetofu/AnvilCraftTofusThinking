package dev.anvilcraft.tofusthinking.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.CollisionContext;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * 狂蹬AI，爽
 */
public class RayDetectionUtil {
    private static final Predicate<Entity> DEFAULT_FILTER = entity ->
            entity.isAlive() && !entity.isSpectator() && entity.isPickable();

    private final Level level;
    private Vec3 start;
    private Vec3 end;
    private boolean ignoreBlocks = false;
    private double entityBoundingBoxInflation = 0.0;
    private Predicate<Entity> filter = DEFAULT_FILTER;
    @Nullable
    private Entity excludedEntity;  // 排除检测的实体（通常为施法者）

    private RayDetectionUtil(Level level) {
        this.level = level;
    }

    public static RayDetectionUtil create(Level level) {
        return new RayDetectionUtil(level);
    }

    // ---- 设置起点 ----
    public RayDetectionUtil start(Vec3 start) {
        this.start = start;
        return this;
    }

    /**
     * 从实体的眼睛位置设置起点。
     */
    public RayDetectionUtil startFromEntity(Entity entity) {
        this.start = entity.getEyePosition();
        return this;
    }

    // ---- 设置终点 ----
    public RayDetectionUtil end(Vec3 end) {
        this.end = end;
        return this;
    }

    /**
     * 根据实体的视线方向和距离设置终点。
     */
    public RayDetectionUtil endByLook(Entity entity, float range) {
        this.start = entity.getEyePosition();
        this.end = this.start.add(entity.getLookAngle().scale(range));
        this.excludedEntity = entity;
        return this;
    }

    public RayDetectionUtil ignoreBlocks(boolean ignore) {
        this.ignoreBlocks = ignore;
        return this;
    }

    /**
     * 检测实体时，对其包围盒进行膨胀的宽度（用于检测靠近射线的实体）。
     */
    public RayDetectionUtil extendInflation(double inflation) {
        this.entityBoundingBoxInflation = inflation;
        return this;
    }

    public RayDetectionUtil filter(Predicate<Entity> filter) {
        this.filter = filter;
        return this;
    }

    public RayDetectionUtil excludeEntity(@Nullable Entity entity) {
        this.excludedEntity = entity;
        return this;
    }

    public CollisionContext getCollisionContext(){
        return excludedEntity == null ? CollisionContext.empty() : CollisionContext.of(excludedEntity);
    }

    // ---- 核心检测方法 ----
    public HitResult raycast() {
        Objects.requireNonNull(start, "Start position must be set");
        Objects.requireNonNull(end, "End position must be set");

        // 1. 方块检测（若未忽略）
        BlockHitResult blockHit = null;
        Vec3 rayEnd = end;
        if (!ignoreBlocks) {
            ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, getCollisionContext());
            blockHit = level.clip(context);
            rayEnd = blockHit.getLocation(); // 碰撞点或终点
        }

        // 2. 实体检测
        List<EntityHitResult> entityHits = performEntityDetection(rayEnd);

        // 3. 综合结果（优先返回最近的实体命中）
        if (!entityHits.isEmpty()) {
            return entityHits.getFirst();
        }

        // 若有方块碰撞（非miss）则返回方块结果，否则返回miss
        if (!ignoreBlocks && blockHit != null && blockHit.getType() != HitResult.Type.MISS) {
            return blockHit;
        }

        return BlockHitResult.miss(rayEnd, Direction.getNearest(end.subtract(start)), BlockPos.containing(rayEnd));
    }

    public List<EntityHitResult> raycastAllEntities() {
        Objects.requireNonNull(start, "Start must be set");
        Objects.requireNonNull(end, "End must be set");

        Vec3 rayEnd = end;
        if (!ignoreBlocks) {
            BlockHitResult blockHit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, getCollisionContext()));
            rayEnd = blockHit.getLocation();
        }
        return performEntityDetection(rayEnd);
    }

    private List<EntityHitResult> performEntityDetection(Vec3 rayEnd) {
        AABB searchBox = new AABB(start, rayEnd).inflate(entityBoundingBoxInflation);
        Predicate<Entity> combinedFilter = filter;
        if (excludedEntity != null) {
            combinedFilter = filter.and(e -> e != excludedEntity);
        }
        List<Entity> entities = level.getEntities((Entity) null, searchBox, combinedFilter);

        List<EntityHitResult> hits = new ArrayList<>();
        for (Entity entity : entities) {
            AABB inflatedBox = entity.getBoundingBox().inflate(entityBoundingBoxInflation);
            inflatedBox.clip(start, rayEnd).ifPresent(hitPos ->
                    hits.add(new EntityHitResult(entity, hitPos))
            );
        }
        hits.sort(Comparator.comparingDouble(h -> h.getLocation().distanceToSqr(start)));
        return hits;
    }
}

