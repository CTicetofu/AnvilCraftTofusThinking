package dev.anvilcraft.tofusthinking.init.item;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;


public class AddonItemTags {

    public static final TagKey<Item> CURIOS_CHARM = createCurio("charm");
    public static final TagKey<Item> NORMAL_MOVEMENT_WHEN_USE = create("normal_movement_when_use");

    public static TagKey<Item> createC(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c",name));
    }
    public static TagKey<Item> createCurio(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("curios",name));
    }
    public static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(AnvilCraftTofusThinking.MOD_ID,name));
    }
    public static TagKey<Item> createFull(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.parse(name));
    }
}
