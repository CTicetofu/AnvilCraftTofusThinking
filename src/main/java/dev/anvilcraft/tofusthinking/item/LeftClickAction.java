package dev.anvilcraft.tofusthinking.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface LeftClickAction {
    default void onClientClick(ItemStack stack, Player player){}
    default void onServerClick(ItemStack stack, Player player){}
}
