package dev.anvilcraft.tofusthinking.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccess {
    //我不想这样，我草，本体写的什么东西
    @Accessor
    EntityDataAccessor<Integer> getDATA_AIR_SUPPLY_ID();
}
