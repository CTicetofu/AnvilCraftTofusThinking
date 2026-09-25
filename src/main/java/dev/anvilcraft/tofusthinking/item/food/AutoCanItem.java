package dev.anvilcraft.tofusthinking.item.food;

import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.item.curio.CurioBaseItem;
import dev.anvilcraft.tofusthinking.util.ItemUtil;
import dev.dubhe.anvilcraft.api.item.IExtraItemDisplay;
import dev.dubhe.anvilcraft.init.item.ModComponents;
import dev.dubhe.anvilcraft.item.property.component.StoredItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Optional;

public class AutoCanItem extends CurioBaseItem implements IExtraItemDisplay {
    public AutoCanItem(Properties properties) {
        super(properties.component(AddonComponents.NUTRITION_VALUE,0));
    }
    public static final int maxNutritionValue = 100000;

    @Override
    public void verifyComponentsAfterLoad(@NotNull ItemStack stack) {
        if (!stack.has(ModComponents.DISPLAY_ITEM)) {
            stack.set(ModComponents.DISPLAY_ITEM, new StoredItem(Items.BREAD.getDefaultInstance()));
        }
        super.verifyComponentsAfterLoad(stack);
    }

    @Override
    public boolean overrideStackedOnOther(@NotNull ItemStack stack, @NotNull Slot slot, @NotNull ClickAction action, @NotNull Player player) {
        ItemStack target = slot.getItem();
        if(action == ClickAction.SECONDARY && slot.allowModification(player) && target.has(DataComponents.FOOD)){
            int currentValue = stack.getOrDefault(AddonComponents.NUTRITION_VALUE,0);
            FoodProperties properties = target.getFoodProperties(player);
            if(properties != null && currentValue < maxNutritionValue){
                int value = properties.nutrition() + (int) properties.saturation();
                int consumeCount = Math.min((maxNutritionValue - currentValue)/value,target.getCount());
                if(consumeCount <= 0){return true;}
                ItemStack consumed = target.copyWithCount(target.getCount() - consumeCount);
                ItemStack example = target.copyWithCount(1);
                stack.set(AddonComponents.NUTRITION_VALUE,currentValue + value * consumeCount);
                stack.set(ModComponents.DISPLAY_ITEM, new StoredItem(example.getItem().getDefaultInstance()));
                ItemStack remainItem = ItemUtil.getFoodRemainingItems(example);
                int needCount = remainItem.getCount() * consumeCount;
                if(!player.isCreative()){
                    slot.set(consumed);
                    if(!remainItem.isEmpty()){
                        ItemUtil.giveEnoughItem(player,remainItem,needCount);
                    }
                }
                if(player.level().isClientSide){
                    player.level().playLocalSound(player, SoundEvents.ARMOR_EQUIP_LEATHER.value(),player.getSoundSource(),1,1);
                }
                return true;
            }
        }
        return super.overrideStackedOnOther(stack, slot, action, player);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if(!level.isClientSide && entity instanceof Player player && player.tickCount % 40 == 0 && !player.isCreative() && !player.getCooldowns().isOnCooldown(this)){
            if(tryEat(stack,player)){
                player.getCooldowns().addCooldown(this,10);
            }
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if(slotContext.entity() instanceof Player player && !player.level().isClientSide && player.tickCount % 40 == 35 && !player.isCreative() && !player.getCooldowns().isOnCooldown(this)){
            if(tryEat(stack,player)){
                player.getCooldowns().addCooldown(this,10);
            }
        }
    }

    public static boolean tryEat(ItemStack stack, Player player){
        int currentValue = stack.getOrDefault(AddonComponents.NUTRITION_VALUE,0);
        if(currentValue <= 0){return false;}
        int planFood = 0;
        FoodData data = player.getFoodData();
        if(data.getFoodLevel() >= 20 && data.getSaturationLevel() >= 6){return true;}
        if(data.getFoodLevel() < 20){
            planFood = Math.min(5,Math.min(20 - data.getFoodLevel(),currentValue));
            currentValue -= planFood;
        }
        if(data.getSaturationLevel() < 5 && currentValue >= 1){
            currentValue -= 1;
        }
        data.eat(planFood,0);
        data.setSaturation(data.getSaturationLevel() + 1);
        stack.set(AddonComponents.NUTRITION_VALUE,currentValue);
        return true;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        int nutritionValue = stack.getOrDefault(AddonComponents.NUTRITION_VALUE,0);
        tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.auto_can_storage",Component.literal(String.valueOf(nutritionValue)).withStyle(ChatFormatting.YELLOW),Component.literal(String.valueOf(maxNutritionValue)).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.auto_can1").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.auto_can2").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public @NotNull ItemStack getDisplayedItem(@NotNull ItemStack stack) {
        return Optional.ofNullable(stack.get(ModComponents.DISPLAY_ITEM)).map(StoredItem::stored).orElse(ItemStack.EMPTY);
    }

    @Override
    public int offsetX(@NotNull ItemStack stack) {
        return 5;
    }

    @Override
    public int offsetY(@NotNull ItemStack stack) {
        return 2;
    }

    @Override
    public float scale(@NotNull ItemStack stack) {
        return 0.5F;
    }

}
