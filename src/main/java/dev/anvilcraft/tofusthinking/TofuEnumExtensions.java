package dev.anvilcraft.tofusthinking;

import net.minecraft.network.chat.Style;

import java.util.function.UnaryOperator;

public class TofuEnumExtensions {
    public static Object Rarity_TOFU(int idx, Class<?> type) {
        return type.cast(switch (idx) {
            case 0 -> -1;
            case 1 -> "anvilcraft_tofus_thinking:tofu";
            case 2 -> (UnaryOperator<Style>) style -> style.withColor(0x8DEEEE);
            default -> throw new IllegalArgumentException("Unexpected parameter index: " + idx);
        });
    }
}
