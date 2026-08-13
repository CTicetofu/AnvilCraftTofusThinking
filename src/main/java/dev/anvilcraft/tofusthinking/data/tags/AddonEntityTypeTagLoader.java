package dev.anvilcraft.tofusthinking.data.tags;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumTagsProvider;
import dev.anvilcraft.tofusthinking.init.entity.AddonEntities;
import dev.anvilcraft.tofusthinking.init.entity.AddonEntityTypeTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

public class AddonEntityTypeTagLoader {
    public static void init(RegistrumTagsProvider<EntityType<?>> provider) {
        provider.addTag(EntityTypeTags.WITHER_FRIENDS)
                .addOptional(AddonEntities.STRANGE_WITHER.getId());

        provider.addTag(EntityTypeTags.UNDEAD)
                .addOptional(AddonEntities.STRANGE_WITHER.getId());

        provider.addTag(AddonEntityTypeTags.DAMAGE_TEST_ENTITY)
                .addOptional(ResourceLocation.fromNamespaceAndPath("dummmmmmy","target_dummy"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("powerful_dummy","test_dummy"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("powerful_dummy","test_dummy_undead"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("powerful_dummy","test_dummy_arthropod"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("powerful_dummy","test_dummy_water"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("powerful_dummy","test_dummy_illager"));
    }
}
