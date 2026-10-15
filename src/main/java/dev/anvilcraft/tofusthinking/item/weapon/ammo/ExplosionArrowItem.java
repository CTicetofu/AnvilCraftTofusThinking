package dev.anvilcraft.tofusthinking.item.weapon.ammo;

import dev.anvilcraft.tofusthinking.entity.projectile.ExplosionArrow;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.List;

public class ExplosionArrowItem extends ArrowItem {
    public ExplosionArrowItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull AbstractArrow createArrow(@NonNull Level level, ItemStack ammo, @NonNull LivingEntity shooter, @Nullable ItemStack weapon) {
        return new ExplosionArrow(level, shooter, ammo.copyWithCount(1), weapon);
    }

    @Override
    public @NonNull Projectile asProjectile(@NonNull Level level, Position pos, ItemStack stack, @NonNull Direction direction) {
        ExplosionArrow arrow = new ExplosionArrow(level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1), null);
        arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        return arrow;
    }

    @Override
    public boolean isInfinite(@NonNull ItemStack ammo, @NonNull ItemStack bow, @NonNull LivingEntity livingEntity) {
        return ammo.getEnchantmentLevel(livingEntity.level().holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.INFINITY)) > 0;
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull List<Component> tooltipComponents, @NonNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        boolean destroy = stack.getOrDefault(AddonComponents.DESTROY_ITEM,false);
        tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.right_switch_in_inventory",Component.translatable("tooltip.anvilcraft_tofus_thinking.destroy_item_explosion").withStyle(destroy ? ChatFormatting.AQUA : ChatFormatting.STRIKETHROUGH)).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean overrideOtherStackedOnMe(@NonNull ItemStack stack, @NonNull ItemStack other, @NonNull Slot slot, @NonNull ClickAction action, @NonNull Player player, @NonNull SlotAccess access) {
        if(action == ClickAction.SECONDARY && slot.allowModification(player) && other.isEmpty()){
            boolean destroy = stack.getOrDefault(AddonComponents.DESTROY_ITEM,false);
            stack.set(AddonComponents.DESTROY_ITEM,!destroy);
            if(player.level().isClientSide){
                player.level().playLocalSound(player, SoundEvents.STONE_BUTTON_CLICK_OFF,player.getSoundSource(),1,1);
            }
            return true;
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
    }
}
