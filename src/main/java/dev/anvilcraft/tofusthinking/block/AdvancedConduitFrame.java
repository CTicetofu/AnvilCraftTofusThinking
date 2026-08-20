package dev.anvilcraft.tofusthinking.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class AdvancedConduitFrame extends Block {
    public AdvancedConduitFrame(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isConduitFrame(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockPos conduit) {
        return true;
    }

    @Override
    public float getEnchantPowerBonus(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        return 2;
    }

    @Override
    public boolean canEntityDestroy(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Entity entity) {
        return (entity instanceof Player || entity.getType().is(Tags.EntityTypes.BOSSES)) && super.canEntityDestroy(state, level, pos, entity);
    }
}
