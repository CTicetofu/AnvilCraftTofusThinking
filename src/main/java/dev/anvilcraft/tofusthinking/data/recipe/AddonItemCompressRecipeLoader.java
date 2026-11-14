package dev.anvilcraft.tofusthinking.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.lib.v2.util.predicate.ItemIngredientPredicate;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.item.ModItemSubPredicates;
import dev.dubhe.anvilcraft.item.property.predicate.ItemSavedEntityPredicate;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.ItemCompressRecipe;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

public class AddonItemCompressRecipeLoader {
    public static void init(RegistrumRecipeProvider provider) {
        ItemCompressRecipe.builder()
                .requires(Items.GLASS, 2)
                .requires(
                        ItemIngredientPredicate
                                .of(ModBlocks.RESIN_BLOCK.asItem())
                                .withSubPredicate(
                                        ModItemSubPredicates.SAVED_ENTITY.get(),
                                        ItemSavedEntityPredicate.of(EntityType.MOOSHROOM)
                                )
                                .build()
                )
                .result(AddonBlocks.MUSHROOM_COW_IN_JAR)
                .save(provider, AnvilCraftTofusThinking.of("item_compress/mushroom_cow_in_jar"));
    }
}
