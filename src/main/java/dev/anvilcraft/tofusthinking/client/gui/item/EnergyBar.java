package dev.anvilcraft.tofusthinking.client.gui.item;

import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
//这真的是有必要的吗
public interface EnergyBar extends ProgressBar{
    @Override
    default PlanePoint getStartPoint() {
        return PlanePoint.ENERGY_BAR_START;
    }

    @Override
    default PlanePoint getEndPoint() {
        return PlanePoint.ENERGY_BAR_END;
    }

    default PlanePoint getBackgroundEnd(){
        return PlanePoint.ENERGY_BACKGROUND_END;
    }

    @Override
    default boolean isVisible(ItemStack stack) {
        return true;
    }

    @Override
    default int getColor(ItemStack stack) {
        return FastColor.ARGB32.lerp(getProgress(stack),getEnergyEmptyColor(),getEnergyFilledColor());
    }

    @Override
    default float getProgress(ItemStack stack) {
        int max = stack.getOrDefault(AddonComponents.MAX_ENERGY, 1000);
        if(max == 0){return 0;}
        return (float) stack.getOrDefault(AddonComponents.STORED_ENERGY, 0) / max;
    }

    default int getEnergyEmptyColor(){
        return 0xFFEE7600;
    }

    default int getEnergyFilledColor(){
        return 0xFF00FFFF;
    }
}
