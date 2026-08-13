package dev.anvilcraft.tofusthinking.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.lib.v2.util.predicate.BlockStatePredicate;
import dev.anvilcraft.lib.v2.util.predicate.ChanceBlockState;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.recipe.anvil.collision.AnvilCollisionCraftRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ConduitBlock;

public class AddonAnvilCollisionCraftRecipeLoader {
    public static ResourceLocation path(String name){
        return AnvilCraftTofusThinking.of(name).withPrefix("anvil_collision/");
    }
    public static void init(RegistrumRecipeProvider provider) {
        AnvilCollisionCraftRecipe.builder()
                .anvil(ModBlocks.EMBER_ANVIL.get())
                .hitBlock(ModBlocks.CORRUPTED_BEACON.get())
                .transformBlock(
                        BlockStatePredicate.builder().of(Blocks.CONDUIT).with(ConduitBlock.WATERLOGGED,false).build(),
                        ChanceBlockState.of(AddonBlocks.ORIGINAL_CONDUIT),
                        2
                )
                .speed(256)
                .consume(true)
                .save(provider, path("original_conduit"));
    }
}
