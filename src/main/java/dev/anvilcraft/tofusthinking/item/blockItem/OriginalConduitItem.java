package dev.anvilcraft.tofusthinking.item.blockItem;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.util.TooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OriginalConduitItem extends BlockItem {

    public OriginalConduitItem(Block block, Properties properties) {
        super(block, properties.fireResistant().rarity(AnvilCraftTofusThinking.TOFU_RARITY));
    }

    @Override
    public boolean canBeHurtBy(@NotNull ItemStack stack, @NotNull DamageSource source) {
        return source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public int getEntityLifespan(@NotNull ItemStack itemStack, @NotNull Level level) {
        return 1000000;
    }

    @Override
    public boolean onEntityItemUpdate(@NotNull ItemStack stack, @NotNull ItemEntity entity) {
        if(entity.getAge() >= 0){entity.setExtendedLifetime();}
        double overY = entity.getY() - entity.level().getMinBuildHeight();
        if(overY < 5){
            if(overY < -60){
                entity.setPos(entity.getX(),entity.level().getMinBuildHeight(),entity.getZ());
            }
            Vec3 motion = entity.getDeltaMovement();
            entity.setDeltaMovement(motion.x,Math.max(overY < 0 ? 0.5 : 0.04,motion.y),motion.z);
        }
        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.original_conduit").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.original_conduit_build").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.original_conduit_warn").withStyle(ChatFormatting.RED));
        tooltipComponents.add(TooltipUtil.PERMANENT.copy());
    }
}
