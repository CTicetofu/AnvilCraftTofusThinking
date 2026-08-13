package dev.anvilcraft.tofusthinking.api.entity;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public interface SeekableEntity {

    void setSeekTarget(@Nullable Entity entity);

    @Nullable
    Entity getSeekTarget();
}
