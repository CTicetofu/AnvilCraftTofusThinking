package dev.anvilcraft.tofusthinking.client.gui.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;

public interface ProgressBar {
    default PlanePoint getStartPoint(){
        return PlanePoint.VANILLA_BAR_START;
    }

    default PlanePoint getEndPoint(){
        return PlanePoint.VANILLA_BAR_START;
    }

    default boolean isVisible(ItemStack stack){
        return false;
    }

    default float getProgress(ItemStack stack){
        return 0;
    }

    default int getColor(ItemStack stack){
        return 0xFFFFFFFF;
    }
}
