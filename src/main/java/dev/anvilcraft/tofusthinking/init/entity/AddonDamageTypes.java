package dev.anvilcraft.tofusthinking.init.entity;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.entity.ExtraDamageSource;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

public class AddonDamageTypes {
    public static final ResourceKey<DamageType> REWIND =
            ResourceKey.create(Registries.DAMAGE_TYPE, AnvilCraftTofusThinking.of("rewind"));
    public static final ResourceKey<DamageType> REWIND_ATTACK =
            ResourceKey.create(Registries.DAMAGE_TYPE, AnvilCraftTofusThinking.of("rewind_attack"));
    public static final ResourceKey<DamageType> EX_REWIND =
            ResourceKey.create(Registries.DAMAGE_TYPE, AnvilCraftTofusThinking.of("ex_rewind"));
    public static final ResourceKey<DamageType> COUNTER =
            ResourceKey.create(Registries.DAMAGE_TYPE, AnvilCraftTofusThinking.of("counter"));
    public static final ResourceKey<DamageType> BOUNCE_WITHER_SKULL =
            ResourceKey.create(Registries.DAMAGE_TYPE, AnvilCraftTofusThinking.of("bounce_wither_skull"));

    @ApiStatus.Internal
    public static void bootstrap(BootstrapContext<DamageType> ctx) {

        ctx.register(REWIND,new DamageType("tofusThinking.rewind", DamageScaling.NEVER,0.0F));
        ctx.register(REWIND_ATTACK,new DamageType("tofusThinking.rewind_attack", DamageScaling.NEVER,0.0F));
        ctx.register(EX_REWIND,new DamageType("tofusThinking.ex_rewind", DamageScaling.NEVER,0.0F));
        ctx.register(COUNTER,new DamageType("tofusThinking.counter", DamageScaling.NEVER,0.0F));
        ctx.register(BOUNCE_WITHER_SKULL,new DamageType("tofusThinking.bounce_wither_skull", DamageScaling.NEVER,0.0F));
    }

    public static ExtraDamageSource rewind(Level level){
        return extraSource(REWIND,level).justDie();
    }

    public static ExtraDamageSource rewindAttack(Level level, Entity directEntity, Entity causingEntity){
        return extraSource(REWIND_ATTACK,level,directEntity,causingEntity);
    }

    public static ExtraDamageSource exRewind(Level level){
        return extraSource(EX_REWIND,level).withNotBlock(true).justDie();
    }

    public static DamageSource counter(Level level, Entity attacker){
        return source(COUNTER,level,attacker,attacker);
    }

    public static DamageSource bounceWitherSkull(Level level,Entity directEntity,Entity causingEntity){
        return source(BOUNCE_WITHER_SKULL,level,directEntity,causingEntity);
    }

    public static DamageSource source(ResourceKey<DamageType> key, LevelReader level, @Nullable Entity directEntity, @Nullable Entity causingEntity){
        Registry<DamageType> registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        return new DamageSource(registry.getHolderOrThrow(key),directEntity,causingEntity);
    }

    public static DamageSource source(ResourceKey<DamageType> key, LevelReader level) {
        return source(key,level,null,null);
    }

    public static ExtraDamageSource extraSource(ResourceKey<DamageType> key, LevelReader level, @Nullable Entity directEntity, @Nullable Entity causingEntity){
        Registry<DamageType> registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        return new ExtraDamageSource(registry.getHolderOrThrow(key),directEntity,causingEntity);
    }

    public static ExtraDamageSource extraSource(ResourceKey<DamageType> key, LevelReader level) {
        return extraSource(key,level,null,null);
    }
}
