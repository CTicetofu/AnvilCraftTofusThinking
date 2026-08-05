package dev.anvilcraft.tofusthinking.item.weapon;

import dev.anvilcraft.tofusthinking.client.ClientUtil;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StarOfTheSea extends Item {
    public StarOfTheSea(Properties properties) {
        super(properties.component(AddonComponents.PROGRESS,0).component(AddonComponents.EFFECT_TICK,0));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    /*@Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if(entity.tickCount % 20 ==0 && entity instanceof Player player && !player.level().isClientSide){
        }
    }*/

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int remainingUseDuration) {
        if(getUseDuration(stack,livingEntity) - remainingUseDuration > 120){
            livingEntity.releaseUsingItem();
        }
    }

    //之后考虑写个新的冷却，现在先将就一下
    public static boolean isCanBlock(Player player){
        return player.getUseItem().is(AddonItems.STAR_OF_THE_SEA.get()) || player.getCooldowns().getCooldownPercent(AddonItems.STAR_OF_THE_SEA.get(),0.5F) > 0.8F;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity, int timeCharged) {
        int useTime = getUseDuration(stack,livingEntity) - timeCharged;
        if(livingEntity instanceof Player player){
            player.getCooldowns().addCooldown(this,Math.max(15,useTime / 2));
        }
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 200;
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_SHIELD_ACTIONS.contains(itemAbility);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.star_of_the_sea").withStyle(ChatFormatting.GRAY));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return ClientUtil.getUseItemStack() == stack;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        int useTime = ClientUtil.getUseTime();
        if(useTime < 5){
            return 0xFFEEEE00;
        } else if(useTime <= 20){
            return 0xFF32CD32;
        }
        return 0xFFA500;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        int useTime = ClientUtil.getUseTime();
        if(useTime < 5){
            return 13;
        } else if(useTime <= 20){
            return Math.round(13 - (useTime - 5) * 13 / 15F);
        }
        return Math.round(13 - (useTime - 20) * 13 / 100F);
    }
}
