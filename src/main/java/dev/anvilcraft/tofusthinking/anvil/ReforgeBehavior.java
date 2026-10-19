package dev.anvilcraft.tofusthinking.anvil;

import dev.anvilcraft.tofusthinking.block.entity.OriginalConduitBlockEntity;
import dev.dubhe.anvilcraft.api.anvil.IAnvilBehavior;
import dev.dubhe.anvilcraft.api.event.AnvilEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ReforgeBehavior implements IAnvilBehavior {
    @Override
    public boolean handle(
            @NotNull Level level,
            @NotNull BlockPos hitBlockPos,
            @NotNull BlockState hitBlockState,
            float fallDistance,
            AnvilEvent.@NotNull OnLand event
    ) {
        if (!(level instanceof ServerLevel serverLevel)) return false;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos conduitPos = hitBlockPos.relative(direction);
            if (serverLevel.getBlockEntity(conduitPos) instanceof OriginalConduitBlockEntity blockEntity) {
                blockEntity.onReforgeSignal(direction);
            }
        }
        return true;
    }
}
