package dev.anvilcraft.tofusthinking.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public interface OnEnderChestInventoryTick {
    default Item self(){return (Item) this;}
    default void inventoryTick(@NonNull ItemStack stack, @NonNull Level level, @NonNull Player player, int slotId) {
        self().inventoryTick(stack, level, player, slotId, false);
    }
}
