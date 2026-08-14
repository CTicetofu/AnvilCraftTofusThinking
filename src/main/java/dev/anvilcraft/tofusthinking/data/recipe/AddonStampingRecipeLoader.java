package dev.anvilcraft.tofusthinking.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.init.item.AddonItemTags;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import dev.dubhe.anvilcraft.recipe.anvil.StampingUniqueItemsRecipe;

public class AddonStampingRecipeLoader {
    public static void init(RegistrumRecipeProvider provider) {
        StampingUniqueItemsRecipe.builderUnique()
                .requires(AddonItemTags.CURIOS_CHARM, 6)
                .result(AddonItems.CHARM_AMULET)
                .save(provider, AnvilCraftTofusThinking.of("stamping/charm_amulet"));
    }
}
