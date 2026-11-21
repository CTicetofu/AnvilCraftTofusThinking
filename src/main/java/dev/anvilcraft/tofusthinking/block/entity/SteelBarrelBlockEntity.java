package dev.anvilcraft.tofusthinking.block.entity;

import dev.anvilcraft.tofusthinking.block.SteelBarrelBlock;
import dev.anvilcraft.tofusthinking.init.block.AddonBlockEntities;
import dev.anvilcraft.tofusthinking.init.storage.AddonStorageTypes;
import dev.anvilcraft.tofusthinking.saved.storage.SteelBarrelStorage;
import dev.dubhe.anvilcraft.block.entity.storage.StorageBlockEntity;
import dev.dubhe.anvilcraft.saved.storage.Storages;
import net.minecraft.core.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class SteelBarrelBlockEntity extends StorageBlockEntity {
    public SteelBarrelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, AddonStorageTypes.STEEL_BARREL);
    }

    public SteelBarrelBlockEntity(BlockPos pos, BlockState state){
        this(AddonBlockEntities.STEEL_BARREL.get(), pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!Objects.requireNonNull(this.level).isClientSide() && this.getBlockState().getBlock() instanceof SteelBarrelBlock) {
            this.refreshDispose();
        }
    }

    public void refreshDispose() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }
        if (this.getBlockState().getBlock() instanceof SteelBarrelBlock) {
            SteelBarrelBlock.updateDisposeState(this.level, this.getBlockPos());
        }
        this.syncDispose();
    }

    private void syncDispose() {
        if (this.level == null || this.level.isClientSide() || this.getId() == null) {
            return;
        }
        boolean dispose = this.getBlockState().getValue(SteelBarrelBlock.OPEN);
        Storages.get().get(this.getId(), SteelBarrelStorage.class)
                .ifPresent(storage -> storage.getItems().setDispose(dispose));
    }
}
