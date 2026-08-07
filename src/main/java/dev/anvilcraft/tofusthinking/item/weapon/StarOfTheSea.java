package dev.anvilcraft.tofusthinking.item.weapon;

import dev.anvilcraft.tofusthinking.client.ClientUtil;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.init.entity.AddonDamageTypeTags;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import dev.anvilcraft.tofusthinking.item.IToolProgress;
import dev.anvilcraft.tofusthinking.util.ItemUtil;
import dev.dubhe.anvilcraft.init.entity.ModDamageTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class StarOfTheSea extends Item implements IToolProgress {
    public static byte ZERO = (byte)0;
    public StarOfTheSea(Properties properties) {
        super(properties.component(AddonComponents.PROGRESS,0).component(AddonComponents.TYPE_NUMBER,(byte)0).component(AddonComponents.EFFECT_TICK,0));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if(entity.tickCount % 20 == 0 && entity instanceof Player player && !player.level().isClientSide){
            stack.set(AddonComponents.EFFECT_TICK,0);
            byte number = getNumber(stack);
            if(number == 0){return;}
            int progress = stack.getOrDefault(AddonComponents.PROGRESS,0);
            AbsorptionType type = AbsorptionType.values()[number];
            if(progress < type.maxProgress){
                progress = Math.max(0,progress - type.lossPreSecond);
                stack.set(AddonComponents.PROGRESS,progress);
            }
            if(progress == 0){stack.set(AddonComponents.TYPE_NUMBER,(byte)0);}
        }
    }

    @Override
    public boolean overrideStackedOnOther(@NotNull ItemStack stack, @NotNull Slot slot, @NotNull ClickAction action, @NotNull Player player) {
        if(action == ClickAction.SECONDARY && slot.allowModification(player) && !slot.getItem().isEmpty() && player.inventoryMenu.getCarried() == stack){
            ItemStack copy = stack.copy();
            ItemStack other = slot.getItem();
            switch (copy.getOrDefault(AddonComponents.TYPE_NUMBER,ZERO)){
                case 1:{
                    if(other.is(AddonItems.SONIC_BOOM_STAFF) && !other.getOrDefault(AddonComponents.IS_ACTIVE,false)){
                        other.set(AddonComponents.IS_ACTIVE,true);
                        clear(copy);
                    }
                }
                break;
                case 2:{
                    if(other.is(Items.CONDUIT)){
                        if(other.getCount() == 1){
                            slot.set(AddonBlocks.ORIGINAL_CONDUIT.asStack());
                        } else {
                            other.shrink(1);
                            ItemUtil.giveEnoughItem(player,AddonBlocks.ORIGINAL_CONDUIT.asStack(),1);
                        }
                        copy.set(AddonComponents.TYPE_NUMBER,(byte)3);
                    }
                }
                break;
            }
            if(!ItemStack.isSameItemSameComponents(stack, copy)){
                player.inventoryMenu.setCarried(copy);
                if(player.level().isClientSide){
                    player.level().playLocalSound(player, SoundEvents.ENCHANTMENT_TABLE_USE,player.getSoundSource(),1,1);
                }
                return true;
            }
        }
        return super.overrideStackedOnOther(stack, slot, action, player);
    }

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
            player.getCooldowns().addCooldown(this,Math.max(20,useTime / 2));
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
        byte number = getNumber(stack);
        tooltipComponents.add(Component.translatable(String.format("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_%d",number)).withStyle(ChatFormatting.AQUA));
        if(number <= 0){return;}
        AbsorptionType type = AbsorptionType.values()[number];
        int maxProgress = type.maxProgress;
        int percentage = maxProgress > 0 ? stack.getOrDefault(AddonComponents.PROGRESS,0) * 100 / maxProgress : 0;
        tooltipComponents.add(Component.translatable(String.format("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_effect_%d",number)).withStyle(ChatFormatting.YELLOW));
        tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.progress",Component.literal(String.valueOf(percentage)).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return stack.getOrDefault(AddonComponents.PROGRESS,0) > 0;
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return FastColor.ARGB32.lerp(getBarWidth(stack) / 13F,0xFFEEEE00,0xFF32CD32);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        byte number = getNumber(stack);
        int maxProgress = AbsorptionType.values()[number].maxProgress;
        if(maxProgress <= 0){return 0;}
        int progress = stack.getOrDefault(AddonComponents.PROGRESS,0);
        return progress * 13 / maxProgress;

    }
    @Override
    public float getToolProgress(LivingEntity entity, ItemStack stack) {
        int useTime = ClientUtil.getUseTime();
        if(useTime < 5){
            return 1;
        } else if(useTime <= 20){
            return 1 - (useTime - 5)/ 15F;
        }
        return 1 - (useTime - 20)/ 100F;
    }

    @Override
    public int getHudColor(Player player, ItemStack stack) {
        int useTime = ClientUtil.getUseTime();
        if(useTime < 5){
            return 0xFFEEEE00;
        } else if(useTime <= 20){
            return 0xFF98FB98;
        }
        return 0xFFFFF68F;
    }

    public static void dealAbsorb(ItemStack stack,DamageSource source){
        byte index = getNumber(stack);
        AbsorptionType[] types = AbsorptionType.values();
        if(index > 0){
            addNumberProgress(stack,index);
        } else {
            for (AbsorptionType type : types){
                if(type.sourcePredicate.test(source)){
                    index = (byte) type.ordinal();
                    stack.set(AddonComponents.TYPE_NUMBER,index);
                    stack.set(AddonComponents.PROGRESS,type.singleIncreaseProgress);
                    break;
                }
            }
        }
    }

    public static void addNumberProgress(ItemStack stack,byte number){
        if(number != getNumber(stack)){return;}
        AbsorptionType[] types = AbsorptionType.values();
        if(number < 0 || number > types.length){return;}
        AbsorptionType type = types[number];
        int progress = stack.getOrDefault(AddonComponents.PROGRESS,0);
        if(progress <= type.maxProgress){
            stack.set(AddonComponents.PROGRESS,Math.min(progress + type.singleIncreaseProgress,type.maxProgress));
        }
    }

    public static void clear(ItemStack stack){
        stack.set(AddonComponents.TYPE_NUMBER,ZERO);
        stack.set(AddonComponents.PROGRESS,0);
    }

    public static byte getNumber(ItemStack stack){
        return rangeOrdinal(stack.getOrDefault(AddonComponents.TYPE_NUMBER,ZERO));
    }

    private static byte rangeOrdinal(byte number){
        if(number < 0 || number > AbsorptionType.values().length){return 0;}
        return number;
    }

    enum AbsorptionType{
        NONE(damageSource -> false,0,0,0),
        SONIC_BOOM(damageSource -> damageSource.is(AddonDamageTypeTags.SONIC_BOOM),100,1,25),
        REWIND(damageSource -> damageSource.is(AddonDamageTypeTags.REWIND),1000,100,5),
        REWIND_REMAIN(damageSource -> false,1000,0,1000),
        LOST_IN_TIME(damageSource -> damageSource.is(ModDamageTypes.LOST_IN_TIME),100,0,100),
        FANG(damageSource -> false,100,0,100);

        final Predicate<DamageSource> sourcePredicate;
        final int maxProgress;
        final int lossPreSecond;
        final int singleIncreaseProgress;
        AbsorptionType(Predicate<DamageSource> sourcePredicate, int maxProgress, int lossPreSecond, int singleIncreaseProgress){
            this.sourcePredicate = sourcePredicate;
            this.maxProgress = maxProgress;
            this.lossPreSecond = lossPreSecond;
            this.singleIncreaseProgress = singleIncreaseProgress;
        }
    }
}
