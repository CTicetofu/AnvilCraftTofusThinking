package dev.anvilcraft.tofusthinking.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.TimeWarpRecipe;
import net.minecraft.world.item.Items;

public class AddonTimeWarpRecipeLoader {
    public static void init(RegistrumRecipeProvider provider) {
        TimeWarpRecipe.builder()
                .requires(AddonBlocks.ORIGINAL_CONDUIT.asItem())
                .result(Items.CONDUIT)
                .save(provider, AnvilCraftTofusThinking.of("time_warp/low_original_condduit"));
    }
}
