package dev.anvilcraft.tofusthinking.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IToolProgress {
    default boolean isToolProgressVisible(Player player, ItemStack stack){
        return player.getUseItem() == stack;
    }
    default float getToolProgress(LivingEntity entity, ItemStack stack){
        return 0;
    }

    default int getHudColor(Player player, ItemStack stack){
        return 0xFFFFFFFF;
    }
}
