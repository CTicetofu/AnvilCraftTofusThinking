package dev.anvilcraft.tofusthinking.client.renderer.model;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import net.minecraft.client.renderer.item.ItemProperties;

public class AddonItemModelProperties {
    public static void addItemModelProperties(){
        ItemProperties.register(AddonItems.ELECTROMAGNETIC_CROSSBOW.asItem(), AnvilCraftTofusThinking.of("lack_energy"),
                ((stack, level, livingEntity, i) -> stack.getOrDefault(AddonComponents.STORED_ENERGY,0) == 0 ? 1 : 0));
    }
}
