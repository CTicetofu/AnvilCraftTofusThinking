package dev.anvilcraft.tofusthinking.data.tags;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumTagsProvider;
import dev.anvilcraft.tofusthinking.init.item.AddonItemTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class AddonItemTagLoader {
    public static void init(RegistrumTagsProvider<Item> provider) {
        provider.addTag(AddonItemTags.STORAGE_BLOCKS_AMETHYST)
                .add(findResourceKey(Items.AMETHYST_BLOCK));
    }
    private static ResourceKey<Item> findResourceKey(Item item) {
        return ResourceKey.create(Registries.ITEM, BuiltInRegistries.ITEM.getKey(item));
    }
}
