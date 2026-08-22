package dev.anvilcraft.tofusthinking.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.DataGenContext;
import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.tofusthinking.data.TofusThinkingDatagen;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.block.Block;

public class AddonBlockRecipeLoader {

    public static <T extends Block> void overloadGenerator(DataGenContext<Block, T> ctx, RegistrumRecipeProvider provider) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), 1)
                .pattern("BDB")
                .pattern("CAC")
                .pattern("BDB")
                .define('A', ModBlocks.VOID_ENERGY_COLLECTOR)
                .define('B', ModBlocks.CUT_FROST_METAL_BLOCK)
                .define('C', ModBlocks.AMBER_BLOCK)
                .define('D', AddonBlocks.STABLE_PRISMARINE_BRICKS)
                .unlockedBy(TofusThinkingDatagen.hasItem(ModBlocks.VOID_ENERGY_COLLECTOR), TofusThinkingDatagen.has(ModBlocks.VOID_ENERGY_COLLECTOR))
                .save(provider);
    }
}
