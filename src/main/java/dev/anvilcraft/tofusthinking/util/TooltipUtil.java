package dev.anvilcraft.tofusthinking.util;

import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

public class TooltipUtil {
    public static int YELLOW = 0xFFFF00;
    public static int CRAY = 0x00FFFF;
    public static int GRAY = 0xA9A9A9;
    public static int KHAKI1 = 0xFFF68F;
    public static int MEDIUM_TURQUOISE = 0x48D1CC;
    public static int ROYAL_BLUE = 0x4169E1;
    public static int WHITE_SMOKE = 0xF5F5F5;
    public static int GREY31 = 0x4F4F4F;
    public static int NAVAJO_WHITE_3 = 0xCDB38B;
    public static String[] energyUnitList = {"FE","KFE","MFE","GFE"};
    public static int[] energyBaseList = {1, 1000,1000000,1000000000};

    public static Component NOT_ACTIVE = Component.translatable("tooltip.anvilcraft_tofus_thinking.not_active").withColor(YELLOW);
    public static Component PERMANENT = Component.translatable("tooltip.anvilcraft_tofus_thinking.permanent").withStyle(Style.EMPTY.withColor(MEDIUM_TURQUOISE));
    public static Component HOLD_SHIFT_FOR_MORE = Component.translatable("tooltip.anvilcraft_tofus_thinking.hold_shift_for_more",Component.literal("[Shift]").withColor(WHITE_SMOKE)).withColor(GREY31);

    public static Component getItemEnergyTooltip(ItemStack stack, boolean full){
        int energy = stack.getOrDefault(AddonComponents.STORED_ENERGY,0);
        int maxEnergy = stack.getOrDefault(AddonComponents.MAX_ENERGY,0);
        String leftNumber = full ? getFullFE(energy) : getSimpleFE(energy);
        String rightNumber = full ? getFullFE(maxEnergy) : getSimpleFE(maxEnergy);
        return Component.literal(leftNumber).withColor(YELLOW).append(Component.literal("/").withColor(GRAY)).append(Component.literal(rightNumber).withColor(CRAY));
    }

    public static Component getItemNeedEnergy(int amount,boolean full){
        String number = full ? getFullFE(amount) : getSimpleFE(amount);
        return Component.translatable("tooltip.anvilcraft_tofus_thinking.need_energy",Component.literal(number).withColor(KHAKI1)).withColor(GRAY);
    }
    public static int getEnergyLevel(int energy){
        if(energy <= 10000){return 0;}
        if(energy <= 1000000){return 1;}
        if(energy <= 1000000000){return 2;}
        return 3;
    }
    public static String getSimpleFE(int energy){
        int level = getEnergyLevel(energy);
        if(level == 0){return getFullFE(energy);}
        return String.format("%.2f%s",(float)energy / energyBaseList[level],energyUnitList[level]);
    }
    public static String getFullFE(int energy){return String.format("%d%s",energy,energyUnitList[0]);}
}
