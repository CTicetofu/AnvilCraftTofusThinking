package dev.anvilcraft.tofusthinking.init;

import dev.anvilcraft.lib.v2.registrum.util.entry.MenuEntry;
import dev.anvilcraft.tofusthinking.client.gui.screen.SimpleNumberConfigScreen;
import dev.anvilcraft.tofusthinking.inventory.SimpleNumberConfigMenu;
import dev.anvilcraft.tofusthinking.inventory.TofuAnvilMenu;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.world.inventory.AnvilMenu;

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
    public static final MenuEntry<AnvilMenu> TOFU_ANVIL = REGISTRUM
            .menu(
                    "tofu_anvil",
                    (menuType, containerId, inventory) -> new TofuAnvilMenu(containerId,inventory),
                    () -> AnvilScreen::new
            ).register();
}
