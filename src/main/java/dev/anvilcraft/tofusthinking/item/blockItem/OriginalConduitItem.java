package dev.anvilcraft.tofusthinking.item.blockItem;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class OriginalConduitItem extends BlockItem {

    public OriginalConduitItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean canBeHurtBy(@NotNull ItemStack stack, @NotNull DamageSource source) {
        return source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }
}
