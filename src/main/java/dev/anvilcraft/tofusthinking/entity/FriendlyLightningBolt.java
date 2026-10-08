package dev.anvilcraft.tofusthinking.entity;

import com.google.common.collect.Sets;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

//有空再改
public class FriendlyLightningBolt extends LightningBolt implements TraceableEntity {
    private final Set<Entity> hitEntities = Sets.newHashSet();
    private Entity owner;

    public FriendlyLightningBolt(EntityType<? extends LightningBolt> entityType, Level level) {
        super(entityType, level);
        setVisualOnly(true);
    }

    public FriendlyLightningBolt(Level level, Entity owner){
        this(EntityType.LIGHTNING_BOLT, level);
        this.owner = owner;
    }

    private boolean hasHit = false;

    @Override
    public void setVisualOnly(boolean visualOnly) {
        super.setVisualOnly(true);
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.hasHit){
            this.hasHit = true;
            if(level().isClientSide){return;}
            List<Entity> list1 = this.level().getEntities(this, new AABB(this.getX() - 3.0F, this.getY() - 3.0F, this.getZ() - 3.0F, this.getX() + 3.0F, this.getY() + 9.0F, this.getZ() + 3.0F), this::canHit);

            for(Entity entity : list1) {
                if (!EventHooks.onEntityStruckByLightning(entity, this)) {
                    entity.hurt(new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.LIGHTNING_BOLT), null, owner),this.getDamage());
                    entity.thunderHit((ServerLevel)this.level(), this);
                }
            }

            this.hitEntities.addAll(list1);
            if (this.getCause() != null) {
                CriteriaTriggers.CHANNELED_LIGHTNING.trigger(this.getCause(), list1);
            }
        }
    }

    public boolean canHit(Entity entity){
        if(entity.isAlive() && entity != owner){
            if(entity instanceof ItemEntity || entity instanceof ExperienceOrb){return false;}
            return owner == null || !owner.isAlliedTo(entity);
        }
        return false;
    }

    @Override
    public void spawnFire(int extraIgnitions) {}

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public @NotNull Stream<Entity> getHitEntities() {
        return hitEntities.stream().filter(Entity::isAlive);
    }


    @Override
    public @Nullable Entity getOwner() {
        return owner;
    }
}
