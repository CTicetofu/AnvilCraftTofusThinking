package dev.anvilcraft.tofusthinking.init;

import dev.anvilcraft.lib.v2.registrum.util.entry.MenuEntry;
import dev.anvilcraft.tofusthinking.client.gui.screen.SimpleNumberConfigScreen;
import dev.anvilcraft.tofusthinking.inventory.SimpleNumberConfigMenu;

import static dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking.REGISTRUM;

public class AddonMenuTypes {
    public static void register() {
    }
    public static final MenuEntry<SimpleNumberConfigMenu> SIMPLE_NUMBER_CONFIG = REGISTRUM
            .menu(
                    "simple_number_config",
                    (menuType, containerId, inventory) -> new SimpleNumberConfigMenu(menuType, containerId),
                    () -> SimpleNumberConfigScreen::new
            ).register();
}
