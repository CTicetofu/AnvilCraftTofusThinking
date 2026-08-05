package dev.anvilcraft.tofusthinking.util;

import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class ItemUtil {

    //因为上下文没有实体
    public static ItemStack getFoodRemainingItems(ItemStack stack){
        if(stack.hasCraftingRemainingItem()){
            return stack.getCraftingRemainingItem();
        } else {
           return Optional.ofNullable(stack.get(DataComponents.FOOD))
                    .flatMap(FoodProperties::usingConvertsTo)
                    .orElse(ItemStack.EMPTY);
        }
    }

    //麻烦的批处理
    public static void giveEnoughItem(Player player,ItemStack stack,int count){
        int maxCount = stack.getMaxStackSize();
        while (count > 0){
            int operationCount = Math.min(maxCount,count);
            if(!player.getInventory().add(stack.copyWithCount(operationCount))){
                player.drop(stack.copyWithCount(operationCount),false);
            }
            count -= operationCount;
        }
    }
    public static void repairItem(ItemStack stack,int amount,float rate,boolean force){
        if(stack.isEmpty()){return;}
        if(!force && !stack.isRepairable()){return;}
        if(stack.getDamageValue() > 0){
            stack.setDamageValue(Math.max(0,stack.getDamageValue() - amount - (int)(stack.getMaxDamage() * rate)));
        }
    }
    public static void repairItem(ItemStack stack,int amount,float rate){
        repairItem(stack,amount,rate,false);
    }

    public static boolean consumeEnergy(LivingEntity entity, ItemStack stack, int consumption){
        if(entity instanceof Player player && player.getAbilities().instabuild){return true;}
        int energy = stack.getOrDefault(AddonComponents.STORED_ENERGY,0);
        if(energy >= consumption){
            stack.set(AddonComponents.STORED_ENERGY,energy - consumption);
            return true;
        }
        return false;
    }

    public static boolean hasEnoughEnergy(ItemStack stack, int amount){
        return stack.getOrDefault(AddonComponents.STORED_ENERGY,0) >= amount;
    }

    public static void addEnergy(ItemStack stack, int amount){
        int energy = stack.getOrDefault(AddonComponents.STORED_ENERGY,0);
        int maxEnergy = stack.getOrDefault(AddonComponents.MAX_ENERGY,0);
        if(energy < maxEnergy){
            stack.set(AddonComponents.STORED_ENERGY,Math.min(maxEnergy,energy + amount));
        }
    }

}
