package dev.anvilcraft.tofusthinking.entity;

import dev.anvilcraft.tofusthinking.api.block.Falling;
import dev.anvilcraft.tofusthinking.init.entity.AddonEntities;
import dev.dubhe.anvilcraft.api.event.AnvilEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

public class FallingImitativeBlockEntity extends FallingBlockEntity {
    private float beforeLandDistance = 0f;
    public FallingImitativeBlockEntity(EntityType<? extends FallingImitativeBlockEntity> entityType, Level level) {
        super(entityType, level);
        this.disableDrop();
    }

    public FallingImitativeBlockEntity(Level level, double x, double y, double z, BlockState state){
        this(AddonEntities.FALLING_SPECTRAL_BLOCK.get(), level);
        this.blockState = state;
        this.blocksBuilding = true;
        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setStartPos(this.blockPosition());
    }

    @Override
    public void tick() {
        if (this.getBlockState().isAir()) {
            this.discard();
        } else {
            BlockState state = this.getBlockState();
            Block block = state.getBlock();
            ++this.time;
            this.applyGravity();
            this.move(MoverType.SELF, this.getDeltaMovement());
            if (!this.level().isClientSide && (this.isAlive() || this.forceTickAfterTeleportToDuplicate)) {
                BlockPos blockpos = this.blockPosition();
                if (!this.onGround()) {
                    if (!this.level().isClientSide && (this.time > 100 && (blockpos.getY() <= this.level().getMinBuildHeight() || blockpos.getY() > this.level().getMaxBuildHeight()) || this.time > 200)) {
                        this.discard();
                    }
                } else {
                    BlockState blockstate = this.level().getBlockState(blockpos);
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5F, 0.7));
                    if (!blockstate.is(Blocks.MOVING_PISTON)) {
                        this.discard();
                        if (block instanceof Fallable fallable) {
                            fallable.onLand(this.level(), blockpos, this.blockState, blockstate, this);
                        }
                        if(state.is(BlockTags.ANVIL)){
                            AnvilEvent.OnLand event = new AnvilEvent.OnLand(this.level(), this.blockPosition(), this, beforeLandDistance);
                            NeoForge.EVENT_BUS.post(event);
                        }
                    }
                }
            }

            this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        }
    }

    @Override
    public boolean canChangeDimensions(@NotNull Level oldLevel, @NotNull Level newLevel) {
        return false;
    }

    public static FallingImitativeBlockEntity fall(Level level, BlockPos pos, BlockState blockState) {
        FallingImitativeBlockEntity fallingBlockEntity = new FallingImitativeBlockEntity(
                level,
                (double) pos.getX() + 0.5,
                pos.getY(),
                (double) pos.getZ() + 0.5,
                blockState.hasProperty(BlockStateProperties.WATERLOGGED)
                        ? blockState.setValue(BlockStateProperties.WATERLOGGED, false)
                        : blockState
        );
        if(blockState.getBlock() instanceof Falling falling){
            falling.anvilCraftTofusThinking$toFalling(fallingBlockEntity);
        } else {
            fallingBlockEntity.setHurtsEntities(2.0F, 40);
        }
        level.addFreshEntity(fallingBlockEntity);
        return fallingBlockEntity;
    }

    @Override
    public void resetFallDistance() {
        beforeLandDistance = this.fallDistance;
        super.resetFallDistance();
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        beforeLandDistance = compound.getFloat("BeforeLandDistance");
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("BeforeLandDistance",beforeLandDistance);
    }
}
