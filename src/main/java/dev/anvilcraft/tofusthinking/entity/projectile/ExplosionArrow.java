package dev.anvilcraft.tofusthinking.entity.projectile;

import dev.anvilcraft.tofusthinking.init.entity.AddonEntities;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.Optional;

public class ExplosionArrow extends AbstractArrow {
    public ExplosionArrow(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public ExplosionArrow(Level level, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(AddonEntities.EXPLOSION_ARROW.get(), owner, level, pickupItemStack, firedFromWeapon);
        this.destroyItem = pickupItemStack.getOrDefault(AddonComponents.DESTROY_ITEM,false);
    }

    public ExplosionArrow(Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(AddonEntities.EXPLOSION_ARROW.get(), x, y, z, level, pickupItemStack, firedFromWeapon);
        this.destroyItem = pickupItemStack.getOrDefault(AddonComponents.DESTROY_ITEM,false);
    }
    private boolean destroyItem = false;

    @Override
    protected void onHitEntity(@NonNull EntityHitResult result) {
        makeExplode();
    }

    @Override
    protected void onHitBlock(@NonNull BlockHitResult result) {
        BlockState blockstate = this.level().getBlockState(result.getBlockPos());
        blockstate.onProjectileHit(this.level(), blockstate, result, this);
        makeExplode();
    }

    private void makeExplode(){
        if(this.level() instanceof ServerLevel serverLevel){
            Entity owner = this.getOwner();
            Vec3 pos = this.position();
            DamageSource source = this.damageSources().explosion(this,owner);
            ExplosionDamageCalculator calculator = destroyItem ? EXPLOSION_ARROW_DESTROY_EXPLOSION_CALCULATOR : EXPLOSION_ARROW_EXPLOSION_CALCULATOR;
            Explosion.BlockInteraction interaction = destroyItem ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
            Explosion explosion = new Explosion(
                    this.level(),
                    this,
                    source,
                    calculator,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    4,
                    false,
                    interaction,
                    ParticleTypes.EXPLOSION,
                    ParticleTypes.EXPLOSION_EMITTER,
                    SoundEvents.GENERIC_EXPLODE
            );
            if (net.neoforged.neoforge.event.EventHooks.onExplosionStart(this.level(), explosion)) {return;}
            explosion.explode();
            for (ServerPlayer serverplayer : serverLevel.players()) {
                if (serverplayer.distanceToSqr(pos.x, pos.y, pos.z) < 4096.0) {
                    serverplayer.connection
                            .send(
                                    new ClientboundExplodePacket(
                                            pos.x,
                                            pos.y,
                                            pos.z,
                                            4,
                                            explosion.getToBlow(),
                                            explosion.getHitPlayers().get(serverplayer),
                                            explosion.getBlockInteraction(),
                                            explosion.getSmallExplosionParticles(),
                                            explosion.getLargeExplosionParticles(),
                                            explosion.getExplosionSound()
                                    )
                            );
                }
            }
            this.discard();
        }
    }

    public static ExplosionArrowDamageCalculator EXPLOSION_ARROW_EXPLOSION_CALCULATOR = new ExplosionArrowDamageCalculator(false);
    public static ExplosionArrowDamageCalculator EXPLOSION_ARROW_DESTROY_EXPLOSION_CALCULATOR = new ExplosionArrowDamageCalculator(true);
    public static class ExplosionArrowDamageCalculator extends ExplosionDamageCalculator {
        public ExplosionArrowDamageCalculator(boolean destroyItem){
            this.destroyItem = destroyItem;
        }
        private final boolean destroyItem;
        public @NotNull Optional<Float> getBlockExplosionResistance(@NotNull Explosion explosion, @NotNull BlockGetter reader, @NotNull BlockPos pos, BlockState state, @NotNull FluidState fluid) {
            return state.isAir() && fluid.isEmpty()
                    ? Optional.empty()
                    : Optional.of(Math.max(lowExplosionResistance(state, explosion, reader, pos), fluid.getExplosionResistance(reader, pos, explosion) * 0.001F));
        }

        public boolean shouldBlockExplode(@NotNull Explosion explosion, @NotNull BlockGetter reader, @NotNull BlockPos pos, @NotNull BlockState state, float power) {
            return !state.is(Tags.Blocks.ORES);
        }

        public boolean shouldDamageEntity(@NotNull Explosion explosion, @NotNull Entity entity) {
            Entity owner = explosion.getIndirectSourceEntity();
            if(owner != null && (owner == entity || owner.isAlliedTo(entity))){return false;}
            return destroyItem || !(entity instanceof ItemEntity);
        }

        public float getKnockbackMultiplier(@NotNull Entity entity) {
            if(entity instanceof ItemEntity){return 0.1F;}
            return 1F;
        }

        public float getEntityDamageAmount(@NonNull Explosion explosion, @NonNull Entity entity) {
            float f = explosion.radius() * 2.0F;
            float rate = 1;
            Vec3 vec3 = explosion.center();
            double d0 = Math.sqrt(entity.distanceToSqr(vec3)) / (double)f;
            double d1 = 1.0 - d0;
            return (float)((d1 * d1 + d1) / 2.0 * 5.0 * (double)f * rate + 1.0);
        }

        protected float lowExplosionResistance(BlockState state, @NotNull Explosion explosion, @NotNull BlockGetter reader, @NotNull BlockPos pos){
            float originalResistance = state.getExplosionResistance(reader, pos, explosion);
            if(state.getDestroySpeed(reader,pos) < 0){
                return originalResistance;
            } else {
                return originalResistance > 100 ? originalResistance - 99 : originalResistance * 0.01F;
            }
        }
    }

    @Override
    protected @NonNull ItemStack getDefaultPickupItem() {
        return AddonItems.EXPLOSION_ARROW.asStack();
    }
}
