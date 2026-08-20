package dev.anvilcraft.tofusthinking.block.entity;

import com.google.common.collect.Lists;
import dev.anvilcraft.tofusthinking.block.OriginalConduitBlock;
import dev.anvilcraft.tofusthinking.init.block.AddonBlockEntities;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.init.entity.AddonDamageTypes;
import dev.anvilcraft.tofusthinking.init.entity.AddonEntityTypeTags;
import dev.anvilcraft.tofusthinking.util.EntityUtil;
import dev.anvilcraft.tofusthinking.util.ItemUtil;
import dev.dubhe.anvilcraft.api.power.IPowerProducer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import dev.dubhe.anvilcraft.block.CorruptedBeaconBlock;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//copy from vanilla
public class OriginalConduitBlockEntity extends BlockEntity implements IPowerProducer {
    public int tickCount;
    private float activeRotation;
    private boolean isActive;
    private boolean isHunting;
    private boolean huntIgnoreWater = false;
    private final List<BlockPos> effectBlocks = Lists.newArrayList();
    @Nullable
    private LivingEntity destroyTarget;
    private int executeCooldown = 200;
    @Nullable
    private UUID destroyTargetUUID;
    private long nextAmbientSoundActivation;
    private int frameCount = 0;
    private PowerGrid grid;
    private int overloadTimes = 0;
    private long lastTick = -1;
    private boolean canProduce = false;
    private boolean isLastOver = false;
    private boolean unload = false;

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
        if(tag.contains("execute_cooldown")){
            this.executeCooldown = tag.getInt("execute_cooldown");
        }
        this.frameCount = tag.getInt("frame_count");
        if(tag.contains("overload_times")){
            this.overloadTimes = tag.getInt("overload_times");
        }
        this.canProduce = tag.getBoolean("canProduce");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.destroyTarget != null) {
            tag.putUUID("Target", this.destroyTarget.getUUID());
        }
        tag.putInt("execute_cooldown",this.executeCooldown);
        tag.putInt("frame_count",this.frameCount);
        if(this.overloadTimes > 0){tag.putInt("overload_times",this.overloadTimes);}
        tag.putBoolean("canProduce",this.canProduce);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public OriginalConduitBlockEntity withActive(){this.isActive = true;return this;}

    public OriginalConduitBlockEntity withHunt(){this.isHunting = true;return this;}

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return this.saveClientCustomOnly();
    }

    public CompoundTag saveClientCustomOnly() {
        CompoundTag tag = new CompoundTag();
        if (this.destroyTarget != null) {
            tag.putUUID("Target", this.destroyTarget.getUUID());
        }
        tag.putInt("overload_times",this.overloadTimes);
        return tag;
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        unload = true;
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
        animationTick(level, pos, list, blockEntity.destroyTarget, blockEntity.tickCount, blockEntity.overloadTimes);
        if (blockEntity.isActive()) {
            blockEntity.activeRotation++;
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, OriginalConduitBlockEntity blockEntity) {
        blockEntity.tickCount++;
        long i = level.getGameTime();
        if(blockEntity.executeCooldown > 0){blockEntity.executeCooldown--;}
        if(i % 20L == 0L){
            checkOverload(level, blockEntity);
            BlockState beacon = level.getBlockState(pos.below());
            if(beacon.is(ModBlocks.CORRUPTED_BEACON.get()) && beacon.hasProperty(CorruptedBeaconBlock.LIT) && beacon.getValue(CorruptedBeaconBlock.LIT)){
                checkOverload(level, blockEntity);
            }
            if(blockEntity.isIrreversible()){
                if(blockEntity.overloadTimes++ > 10){
                    level.destroyBlock(pos,false);
                }
            }
        }
        if (i % 40L == 0L) {
            if(blockEntity.overloadTimes > 0 && blockEntity.overloadTimes < 4){
                if(!blockEntity.isLastOver){
                    blockEntity.overloadTimes--;
                    blockEntity.sendUpdate();
                }
                return;
            }

            List<BlockPos> list = blockEntity.effectBlocks;
            boolean flag = updateShape(level, pos, list,blockEntity) || blockEntity.isIrreversible();
            if(state.hasProperty(OriginalConduitBlock.OPEN) && state.getValue(OriginalConduitBlock.OPEN) != flag){
                level.setBlockAndUpdate(pos,state.setValue(OriginalConduitBlock.OPEN,flag));
            }

            if (flag != blockEntity.isActive) {
                SoundEvent soundevent = flag ? SoundEvents.CONDUIT_ACTIVATE : SoundEvents.CONDUIT_DEACTIVATE;
                level.playSound(null, pos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            if(blockEntity.isIrreversible()){return;}

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

    private static void checkOverload(Level level,OriginalConduitBlockEntity blockEntity){
        if(!blockEntity.isActive){return;}
        long currentTime = level.getGameTime();
        boolean overload = currentTime == blockEntity.lastTick;
        blockEntity.isLastOver = overload;
        if(overload && blockEntity.overloadTimes < 4){
            blockEntity.overloadTimes++;
            blockEntity.sendUpdate();
        }
        blockEntity.lastTick = currentTime;
    }

    private static void updateHunting(OriginalConduitBlockEntity blockEntity, List<BlockPos> positions) {
        blockEntity.setHunting(positions.size() >= 24);
    }

    private static boolean updateShape(Level level, BlockPos pos, List<BlockPos> positions, OriginalConduitBlockEntity blockEntity) {
        positions.clear();
        boolean canProduce = true;
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
                    BlockPos blockpos = pos.offset(x, y, z);
                    BlockState blockstate = level.getBlockState(blockpos);
                    if(canProduce  && blockstate.is(AddonBlocks.ORIGINAL_CONDUIT.get())){
                        if(x != 0 || y != 0 || z != 0){canProduce = false;}
                    }
                    int ax = Math.abs(x);
                    int ay = Math.abs(y);
                    int az = Math.abs(z);
                    if(ax <= 1 && ay<= 1 && az <= 1){continue;}

                    if (blockstate.isConduitFrame(level, blockpos, pos)) {
                        positions.add(blockpos);
                    }
                    if(blockstate.is(AddonBlocks.STABLE_PRISMARINE_BRICKS.get())){
                        reinforcedCount++;
                    }
                }
            }
        }
        blockEntity.canProduce = canProduce;
        blockEntity.frameCount = positions.size();
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
            ItemUtil.repairItem(stack,2,0.005F);
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
                        EntityUtil.clearAllEffect(target);
                        if(!target.getType().is(AddonEntityTypeTags.NOT_MUTI_ATTACK_ENTITY)){target.invulnerableTime = 0;}
                        float rate = target.getType().is(AddonEntityTypeTags.NOT_ORIGINAL_ATTACK_ENTITY) ? 1 : Math.max(1, EntityUtil.getOriginMaxHealthRate(target));
                        target.hurt(AddonDamageTypes.rewind(level),15 * rate);
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

    @Override
    public void setRemoved() {
        super.setRemoved();
        if(this.level != null && !unload){
            if(this.isIrreversible()){doIncurableExplode(this.level,this.getBlockPos());}
        }
    }

    @Nullable
    private static LivingEntity findDestroyTarget(Level level, BlockPos pos, UUID targetId) {
        List<LivingEntity> list = level.getEntitiesOfClass(
                LivingEntity.class, getDestroyRangeAABB(pos), p_352880_ -> p_352880_.getUUID().equals(targetId)
        );
        return list.size() == 1 ? list.getFirst() : null;
    }

    private static void animationTick(Level level, BlockPos pos, List<BlockPos> positions, @Nullable Entity entity, int tickCount, int rate) {
        RandomSource randomsource = level.random;
        double d0 = Mth.sin((float)(tickCount + 35) * 0.1F) / 2.0F + 0.5F;
        d0 = (d0 * d0 + d0) * 0.3F;
        Vec3 vec3 = new Vec3((double)pos.getX() + 0.5, (double)pos.getY() + 1.5 + d0, (double)pos.getZ() + 0.5);
        int cut = Mth.clamp(rate,1,4);
        for (BlockPos blockpos : positions) {
            if (randomsource.nextInt(50/cut) == 0) {
                BlockPos blockPos1 = blockpos.subtract(pos);
                float f = -0.5F + randomsource.nextFloat() + (float)blockPos1.getX();
                float f1 = -2.0F + randomsource.nextFloat() + (float)blockPos1.getY();
                float f2 = -0.5F + randomsource.nextFloat() + (float)blockPos1.getZ();
                SimpleParticleType type = randomsource.nextBoolean() ? ParticleTypes.NAUTILUS : ParticleTypes.ENCHANT;
                vec3.scale(cut);
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

    public void doIncurableExplode(Level level, BlockPos pos){
        if(level == null || level.isClientSide){return;}
        for (BlockPos blockPos:BlockPos.betweenClosed(pos.offset(1,1,1),pos.offset(-1,-1,-1))){
            BlockState state = level.getBlockState(blockPos);
            if(state.getDestroySpeed(level,pos) < 0){continue;}
            level.destroyBlock(blockPos,false);
        }
        for (BlockPos blockPos:BlockPos.betweenClosed(pos.offset(2,2,2),pos.offset(-2,-2,-2))){
            BlockState state = level.getBlockState(blockPos);
            if(state.getDestroySpeed(level,pos) < 0){continue;}
            level.destroyBlock(blockPos,true);
            if (level.getBlockState(blockPos).getBlock() instanceof LiquidBlock) {
                level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
        level.explode(null,AddonDamageTypes.rewind(level).justDie().withNotBlock(true),ORIGINAL_EXPLOSION_CALCULATOR,pos.getCenter(),10, false,Level.ExplosionInteraction.BLOCK);
    }

    public int getExecuteCooldown() {
        return executeCooldown;
    }

    public void resetExecuteCooldown(){
        executeCooldown = 200;
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

    public int getOverloadTimes() {
        return overloadTimes;
    }

    public boolean isIrreversible(){
        return this.overloadTimes > 3;
    }

    @Override
    public @org.jetbrains.annotations.Nullable Level getCurrentLevel() {
        return this.level;
    }

    @Override
    public @NotNull BlockPos getPos() {
        return this.getBlockPos();
    }

    @Override
    public void setGrid(@org.jetbrains.annotations.Nullable PowerGrid grid) {
        this.grid = grid;
    }

    @Override
    public @org.jetbrains.annotations.Nullable PowerGrid getGrid() {
        return grid;
    }

    @Override
    public int getOutputPower() {
        if(frameCount < 8 || !this.canProduce){return 0;}
        return 2 * frameCount << 2 * Mth.clamp(overloadTimes,0,4);
    }

    @Override
    public int getRange() {
        return 2;
    }

    private void sendUpdate() {
        if (this.level == null) return;
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
    }

    public static OriginalExplosionDamageCalculator ORIGINAL_EXPLOSION_CALCULATOR = new OriginalExplosionDamageCalculator();
    public static class OriginalExplosionDamageCalculator extends ExplosionDamageCalculator{
        public @NotNull Optional<Float> getBlockExplosionResistance(@NotNull Explosion explosion, @NotNull BlockGetter reader, @NotNull BlockPos pos, BlockState state, @NotNull FluidState fluid) {
            return state.isAir() && fluid.isEmpty()
                    ? Optional.empty()
                    : Optional.of(Math.max(lowExplosionResistance(state, explosion, reader, pos), fluid.getExplosionResistance(reader, pos, explosion) * 0.001F));
        }

        public boolean shouldBlockExplode(@NotNull Explosion explosion, @NotNull BlockGetter reader, @NotNull BlockPos pos, @NotNull BlockState state, float power) {
            return true;
        }

        public boolean shouldDamageEntity(@NotNull Explosion explosion, @NotNull Entity entity) {
            return !(entity instanceof ItemEntity);
        }

        public float getKnockbackMultiplier(@NotNull Entity entity) {
            if(entity instanceof ItemEntity){return 0.1F;}
            return 1.5F;
        }

        public float getEntityDamageAmount(Explosion explosion, Entity entity) {
            float f = explosion.radius() * 2.0F;
            float rate = 1;
            if(!entity.getType().is(AddonEntityTypeTags.NOT_ORIGINAL_ATTACK_ENTITY) && entity instanceof LivingEntity target){
                rate *= EntityUtil.getOriginMaxHealthRate(target) * 3;
            }
            Vec3 vec3 = explosion.center();
            double d0 = Math.sqrt(entity.distanceToSqr(vec3)) / (double)f;
            double d1 = 1.0 - d0;
            return (float)((d1 * d1 + d1) / 2.0 * 5.0 * (double)f * rate + 1.0);
        }

        protected float lowExplosionResistance(BlockState state, @NotNull Explosion explosion, @NotNull BlockGetter reader, @NotNull BlockPos pos){
            float originalResistance = state.getExplosionResistance(reader, pos, explosion);
            return state.getDestroySpeed(reader,pos) < 0 ? originalResistance : originalResistance * 0.001F;
        }
    }
}
