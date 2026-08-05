package dev.anvilcraft.tofusthinking.inventory;

import dev.anvilcraft.tofusthinking.init.AddonMenuTypes;
import dev.dubhe.anvilcraft.util.Callback;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleNumberConfigMenu extends AbstractContainerMenu {
    private final Callback<Integer> callback;
    public SimpleNumberConfigMenu(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
        this.callback = null;
    }

    public SimpleNumberConfigMenu(int containerId, Callback<Integer> callback) {
        super(AddonMenuTypes.SIMPLE_NUMBER_CONFIG.get(), containerId);
        this.callback = callback;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;
        return sourceSlot.getItem();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    public Callback<Integer> getCallback() {
        return this.callback;
    }
}
