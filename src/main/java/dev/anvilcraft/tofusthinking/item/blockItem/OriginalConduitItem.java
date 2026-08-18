package dev.anvilcraft.tofusthinking.item.blockItem;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OriginalConduitItem extends BlockItem {

    public OriginalConduitItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean canBeHurtBy(@NotNull ItemStack stack, @NotNull DamageSource source) {
        return source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.original_conduit").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.original_conduit_build").withStyle(ChatFormatting.GRAY));
    }
}
