package dev.anvilcraft.tofusthinking.util.DataClass;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentKeyInstance {
    public final ResourceKey<Enchantment> enchantment;

    public final int level;

    public EnchantmentKeyInstance(ResourceKey<Enchantment> enchantment, int level) {
        this.enchantment = enchantment;
        this.level = level;
    }
}
