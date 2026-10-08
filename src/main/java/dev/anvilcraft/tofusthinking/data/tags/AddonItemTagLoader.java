package dev.anvilcraft.tofusthinking.data.tags;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumTagsProvider;
import dev.anvilcraft.tofusthinking.init.item.AddonItemTags;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

public class AddonItemTagLoader {
    public static void init(RegistrumTagsProvider<Item> provider) {
        provider.addTag(AddonItemTags.STORAGE_BLOCKS_AMETHYST)
                .add(findResourceKey(Items.AMETHYST_BLOCK));
        provider.addTag(AddonItemTags.ELECTROMAGNETIC_CROSSBOW_AMMO)
                .add(findResourceKey(Items.ENDER_PEARL))
                .add(findResourceKey(Items.FIREWORK_ROCKET))
                .addTag(Tags.Items.NUGGETS)
                .addTag(Tags.Items.GEMS);

        provider.addTag(AddonItemTags.ELECTROMAGNETIC_CROSSBOW_CAN_MUTI)
                .add(findResourceKey(Items.FIREWORK_ROCKET))
                .addTag(Tags.Items.NUGGETS)
                .addTag(Tags.Items.GEMS)
                .addTag(ItemTags.ARROWS)
                .add(findResourceKey(Items.SNOWBALL))
                .add(findResourceKey(Items.SPLASH_POTION))
                .add(findResourceKey(Items.LINGERING_POTION))
                .add(findResourceKey(Items.FIRE_CHARGE))
                .addOptional(AddonItems.CURSE_SNOWBALL_ITEM.getId());
    }
    private static ResourceKey<Item> findResourceKey(Item item) {
        return ResourceKey.create(Registries.ITEM, BuiltInRegistries.ITEM.getKey(item));
    }
}
