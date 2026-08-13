package dev.anvilcraft.tofusthinking.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;

public class TofuThinkingRecipeHandler {
    public static void init(RegistrumRecipeProvider provider) {
        AddonAnvilCollisionCraftRecipeLoader.init(provider);
    }
}
