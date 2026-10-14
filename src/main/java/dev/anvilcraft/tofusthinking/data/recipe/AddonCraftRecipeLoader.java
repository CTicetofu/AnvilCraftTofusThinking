package dev.anvilcraft.tofusthinking.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.data.TofusThinkingDatagen;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.ItemStack;

public class AddonCraftRecipeLoader {
    public static void init(RegistrumRecipeProvider provider) {
        ItemStack explosionArrow = AddonItems.EXPLOSION_ARROW.asStack();
        explosionArrow.set(AddonComponents.DESTROY_ITEM,true);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, explosionArrow)
                .requires(AddonItems.EXPLOSION_ARROW)
                .unlockedBy(TofusThinkingDatagen.hasItem(AddonItems.EXPLOSION_ARROW), RegistrumRecipeProvider.has(AddonItems.EXPLOSION_ARROW))
                .save(provider, AnvilCraftTofusThinking.of("change_explosion_arrow"));
    }
}
