package dev.anvilcraft.tofusthinking.init.entity;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class AddonEntityTypeTags {
    public static TagKey<EntityType<?>> DAMAGE_TEST_ENTITY = create("damage_test_entity");

    private static TagKey<EntityType<?>> create(String id) {
        return TagKey.create(Registries.ENTITY_TYPE, AnvilCraftTofusThinking.of(id));
    }
}
