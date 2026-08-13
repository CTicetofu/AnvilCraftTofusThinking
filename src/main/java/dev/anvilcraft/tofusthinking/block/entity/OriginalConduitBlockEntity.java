package dev.anvilcraft.tofusthinking.block.entity;

import com.google.common.collect.Lists;
import dev.anvilcraft.tofusthinking.block.OriginalConduitBlock;
import dev.anvilcraft.tofusthinking.init.block.AddonBlockEntities;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.init.entity.AddonDamageTypes;
import dev.anvilcraft.tofusthinking.util.ItemUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

//copy from vanilla
public class OriginalConduitBlockEntity extends BlockEntity {
    public int tickCount;
    private float activeRotation;
    private boolean isActive;
    private boolean isHunting;
    private boolean huntIgnoreWater = false;
    private final List<BlockPos> effectBlocks = Lists.newArrayList();
    @Nullable
    private LivingEntity destroyTarget;
    @Nullable
    private UUID destroyTargetUUID;
    private long nextAmbientSoundActivation;

    public OriginalConduitBlockEntity(BlockPos pos, BlockState blockState) {
        this(AddonBlockEntities.ORIGINAL_CONDUIT.get(), pos, blockState);
    }


    public OriginalConduitBlockEntity(BlockEntityType<? extends BlockEntity> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.hasUUID("Target")) {
            this.destroyTargetUUID = tag.getUUID("Target");
        } else {
            this.destroyTargetUUID = null;
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.destroyTarget != null) {
            tag.putUUID("Target", this.destroyTarget.getUUID());
        }
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public OriginalConduitBlockEntity withActive(){this.isActive = true;return this;}

    public OriginalConduitBlockEntity withHunt(){this.isHunting = true;return this;}

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return this.saveCustomOnly(registries);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, OriginalConduitBlockEntity blockEntity) {
        blockEntity.tickCount++;
        long i = level.getGameTime();
        List<BlockPos> list = blockEntity.effectBlocks;
        if (i % 40L == 0L) {
            blockEntity.isActive = updateShape(level, pos, list,blockEntity);
            updateHunting(blockEntity, list);
        }

        updateClientTarget(level, pos, blockEntity);
        animationTick(level, pos, list, blockEntity.destroyTarget, blockEntity.tickCount);
        if (blockEntity.isActive()) {
            blockEntity.activeRotation++;
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, OriginalConduitBlockEntity blockEntity) {
        blockEntity.tickCount++;
        long i = level.getGameTime();
        List<BlockPos> list = blockEntity.effectBlocks;
        if (i % 40L == 0L) {
            boolean flag = updateShape(level, pos, list,blockEntity);
            if(state.hasProperty(OriginalConduitBlock.OPEN) && state.getValue(OriginalConduitBlock.OPEN) != flag){
                level.setBlockAndUpdate(pos,state.setValue(OriginalConduitBlock.OPEN,flag));
            }

            if (flag != blockEntity.isActive) {
                SoundEvent soundevent = flag ? SoundEvents.CONDUIT_ACTIVATE : SoundEvents.CONDUIT_DEACTIVATE;
                level.playSound(null, pos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            blockEntity.isActive = flag;
            updateHunting(blockEntity, list);
            if (flag) {
                applyEffects(level, pos, list);
                updateDestroyTarget(level, pos, state, list, blockEntity);
            }
        }

        if (blockEntity.isActive()) {
            if (i % 80L == 0L) {
                level.playSound(null, pos, SoundEvents.CONDUIT_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            if (i > blockEntity.nextAmbientSoundActivation) {
                blockEntity.nextAmbientSoundActivation = i + 60L + (long)level.getRandom().nextInt(40);
                level.playSound(null, pos, SoundEvents.CONDUIT_AMBIENT_SHORT, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    private static void updateHunting(OriginalConduitBlockEntity blockEntity, List<BlockPos> positions) {
        blockEntity.setHunting(positions.size() >= 24);
    }

    private static boolean updateShape(Level level, BlockPos pos, List<BlockPos> positions, OriginalConduitBlockEntity blockEntity) {
        positions.clear();
        int reinforcedCount = 0;
        for (int i = -1; i <= 1; i++) {
            for (int k = -1; k <= 1; k++) {
                BlockPos blockpos = pos.offset(i, 0, k);
                if (!level.isWaterAt(blockpos)) {
                    return false;
                }
            }

        }

        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    int ax = Math.abs(x);
                    int ay = Math.abs(y);
                    int az = Math.abs(z);
                    if(ax <= 1 && ay<= 1 && az <= 1){continue;}
                    BlockPos blockpos = pos.offset(x, y, z);
                    BlockState blockstate = level.getBlockState(blockpos);
                    if (blockstate.isConduitFrame(level, blockpos, pos)) {
                        positions.add(blockpos);
                    }
                    if(blockstate.is(AddonBlocks.STABLE_PRISMARINE_BRICKS.get())){
                        reinforcedCount++;
                    }
                }
            }
        }
        blockEntity.huntIgnoreWater = reinforcedCount >= 8;
        return positions.size() >= 8;
    }

    private static void applyEffects(Level level, BlockPos pos, List<BlockPos> positions) {
        int i = positions.size();
        int j = Math.min(i / 4 * 16,96);
        int k = pos.getX();
        int l = pos.getY();
        int i1 = pos.getZ();
        AABB aabb = new AABB(k, l, i1, k + 1, l + 1, i1 + 1)
                .inflate(j)
                .expandTowards(0.0, level.getHeight(), 0.0);
        List<Player> list = level.getEntitiesOfClass(Player.class, aabb);
        if (!list.isEmpty()) {
            for (Player player : list) {
                if (pos.closerThan(player.blockPosition(), j) && (i > 16 || player.isInWaterOrRain())) {
                    repairPlayerInventory(player);
                    player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 300, 0, true, true));
                }
            }
        }
    }

    public static void repairPlayerInventory(Player player){
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            ItemUtil.repairItem(stack, 2, 0.01F);
        }
        Inventory inventory = player.getInventory();
        for (int i = 0; i < 9; i++) {
            if(i == inventory.selected){continue;}
            ItemStack stack = inventory.getItem(i);
            ItemUtil.repairItem(stack,2,0.01F);
        }
    }
    private static void updateDestroyTarget(Level level, BlockPos pos, BlockState state, List<BlockPos> positions, OriginalConduitBlockEntity blockEntity) {
        LivingEntity livingentity = blockEntity.destroyTarget;
        int i = positions.size();
        if (i < 24) {
            blockEntity.destroyTarget = null;
        } else if (blockEntity.destroyTarget == null && blockEntity.destroyTargetUUID != null) {
            blockEntity.destroyTarget = findDestroyTarget(level, pos, blockEntity.destroyTargetUUID);
            blockEntity.destroyTargetUUID = null;
        } else if (blockEntity.destroyTarget == null) {
            List<LivingEntity> list = level.getEntitiesOfClass(
                    LivingEntity.class, getDestroyRangeAABB(pos), target -> target instanceof Enemy && target.isAlive() && (blockEntity.huntIgnoreWater || target.isInWaterOrRain())
            );
            if (!list.isEmpty()) {
                blockEntity.destroyTarget = list.get(level.random.nextInt(list.size()));
            }
        } else if (!blockEntity.destroyTarget.isAlive() || !pos.closerThan(blockEntity.destroyTarget.blockPosition(), 8.0)) {
            blockEntity.destroyTarget = null;
        }

        if (blockEntity.destroyTarget != null) {
            level.playSound(
                    null,
                    blockEntity.destroyTarget.getX(),
                    blockEntity.destroyTarget.getY(),
                    blockEntity.destroyTarget.getZ(),
                    SoundEvents.CONDUIT_ATTACK_TARGET,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F
            );
            level.getEntitiesOfClass(LivingEntity.class,new AABB(blockEntity.destroyTarget.blockPosition()).inflate(2),target -> target instanceof Enemy && target.isAlive()).forEach(
                    target -> {
                        target.invulnerableTime = 0;
                        target.hurt(AddonDamageTypes.rewind(level).withHurtInfo(10,0),15);
                    }
            );
            blockEntity.destroyTarget.hurt(level.damageSources().magic(), 12.0F);
        }

        if (livingentity != blockEntity.destroyTarget) {
            level.sendBlockUpdated(pos, state, state, 2);
        }
    }

    private static void updateClientTarget(Level level, BlockPos pos, OriginalConduitBlockEntity blockEntity) {
        if (blockEntity.destroyTargetUUID == null) {
            blockEntity.destroyTarget = null;
        } else if (blockEntity.destroyTarget == null || !blockEntity.destroyTarget.getUUID().equals(blockEntity.destroyTargetUUID)) {
            blockEntity.destroyTarget = findDestroyTarget(level, pos, blockEntity.destroyTargetUUID);
            if (blockEntity.destroyTarget == null) {
                blockEntity.destroyTargetUUID = null;
            }
        }
    }

    private static AABB getDestroyRangeAABB(BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        return new AABB(i, j, k, i + 1, j + 1, k + 1).inflate(12.0);
    }

    @Nullable
    private static LivingEntity findDestroyTarget(Level level, BlockPos pos, UUID targetId) {
        List<LivingEntity> list = level.getEntitiesOfClass(
                LivingEntity.class, getDestroyRangeAABB(pos), p_352880_ -> p_352880_.getUUID().equals(targetId)
        );
        return list.size() == 1 ? list.getFirst() : null;
    }

    private static void animationTick(Level level, BlockPos pos, List<BlockPos> positions, @Nullable Entity entity, int tickCount) {
        RandomSource randomsource = level.random;
        double d0 = Mth.sin((float)(tickCount + 35) * 0.1F) / 2.0F + 0.5F;
        d0 = (d0 * d0 + d0) * 0.3F;
        Vec3 vec3 = new Vec3((double)pos.getX() + 0.5, (double)pos.getY() + 1.5 + d0, (double)pos.getZ() + 0.5);

        for (BlockPos blockpos : positions) {
            if (randomsource.nextInt(50) == 0) {
                BlockPos blockpos1 = blockpos.subtract(pos);
                float f = -0.5F + randomsource.nextFloat() + (float)blockpos1.getX();
                float f1 = -2.0F + randomsource.nextFloat() + (float)blockpos1.getY();
                float f2 = -0.5F + randomsource.nextFloat() + (float)blockpos1.getZ();
                SimpleParticleType type = randomsource.nextBoolean() ? ParticleTypes.NAUTILUS : ParticleTypes.ENCHANT;
                level.addParticle(type, vec3.x, vec3.y, vec3.z, f, f1, f2);
            }
        }

        if (entity != null && entity.isAlive()) {
            Vec3 vec31 = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
            float f3 = (-0.5F + randomsource.nextFloat()) * (3.0F + entity.getBbWidth());
            float f4 = -1.0F + randomsource.nextFloat() * entity.getBbHeight();
            float f5 = (-0.5F + randomsource.nextFloat()) * (3.0F + entity.getBbWidth());
            Vec3 vec32 = new Vec3(f3, f4, f5);
            SimpleParticleType type = randomsource.nextBoolean() ? ParticleTypes.NAUTILUS : ParticleTypes.ENCHANT;
            level.addParticle(type, vec31.x, vec31.y, vec31.z, vec32.x, vec32.y, vec32.z);
        }
    }

    public boolean isActive() {
        return this.isActive;
    }

    public boolean isHunting() {
        return this.isHunting;
    }

    private void setHunting(boolean isHunting) {
        this.isHunting = isHunting;
    }

    public float getActiveRotation(float partialTick) {
        return (this.activeRotation + partialTick) * -0.0375F;
    }
}
