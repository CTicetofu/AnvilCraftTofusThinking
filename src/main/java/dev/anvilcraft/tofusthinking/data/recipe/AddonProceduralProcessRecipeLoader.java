package dev.anvilcraft.tofusthinking.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.lib.v2.util.predicate.BlockStatePredicate;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.block.OriginalConduitBlock;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import net.minecraft.resources.ResourceLocation;

public class AddonProceduralProcessRecipeLoader {
    public static ResourceLocation path(String name){
        return AnvilCraftTofusThinking.of(name).withPrefix("procedural_process/");
    }
    public static BlockStatePredicate OPEN_ORIGINAL_CONDUIT = BlockStatePredicate.builder()
            .of(AddonBlocks.ORIGINAL_CONDUIT.get())
            .with(OriginalConduitBlock.OPEN, true)
            .build();

    public static void init(RegistrumRecipeProvider provider) {

    }
}
