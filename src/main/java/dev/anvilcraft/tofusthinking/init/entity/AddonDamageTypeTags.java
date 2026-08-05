package dev.anvilcraft.tofusthinking.init.entity;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public class AddonDamageTypeTags {
    public static final TagKey<DamageType> REWIND = create("rewind");
    public static final TagKey<DamageType> CAN_PERFECT_BLOCK = create("can_perfect_block");

    private static TagKey<DamageType> create(String id) {
        return TagKey.create(Registries.DAMAGE_TYPE, AnvilCraftTofusThinking.of(id));
    }
}
