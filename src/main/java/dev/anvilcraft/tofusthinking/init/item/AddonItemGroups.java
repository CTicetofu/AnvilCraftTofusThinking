package dev.anvilcraft.tofusthinking.init.item;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.init.item.tabs.ItemTab;
import dev.dubhe.anvilcraft.init.item.ModItemGroups;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AddonItemGroups {
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AnvilCraftTofusThinking.MOD_ID);

    public static final String ITEM_TAB_ID = AnvilCraftTofusThinking.MOD_ID + "itemGroup.addon_items";

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ADDON_ITEMS = CREATIVE_MODE_TAB.register(
            "addon_items",
            () -> CreativeModeTab.builder()
                    .icon(AddonItems.STAR_OF_THE_SEA::asStack)
                    .displayItems(new ItemTab())
                    .title(Component.translatable(ITEM_TAB_ID))
                    .withTabsBefore(ModItemGroups.ANVILCRAFT_BUILD_BLOCK.getId())
                    .build()
    );

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TAB.register(modEventBus);
    }
}
