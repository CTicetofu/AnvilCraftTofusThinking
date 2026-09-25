package dev.anvilcraft.tofusthinking.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.DataGenContext;
import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.tofusthinking.data.TofusThinkingDatagen;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.init.item.AddonItemTags;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.item.ModItemTags;
import dev.dubhe.anvilcraft.init.item.ModItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

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

    public static <T extends Block> void smartPowerConverter(DataGenContext<Block, T> ctx, RegistrumRecipeProvider provider) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), 1)
                .pattern("BAB")
                .pattern("ACA")
                .pattern("BAB")
                .define('A', ModItems.CIRCUIT_BOARD)
                .define('B', ModItemTags.BRASS_PLATES)
                .define('C', ModBlocks.POWER_CONVERTER_BIG)
                .unlockedBy(TofusThinkingDatagen.hasItem(ModBlocks.POWER_CONVERTER_BIG), TofusThinkingDatagen.has(ModBlocks.POWER_CONVERTER_BIG))
                .save(provider);
    }

    public static <T extends Block> void smartPowerConverterExtremelyBig(DataGenContext<Block, T> ctx, RegistrumRecipeProvider provider) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), 1)
                .pattern("BAB")
                .pattern("ACA")
                .pattern("BAB")
                .define('A', ModItems.CIRCUIT_BOARD)
                .define('B', ModItemTags.BRASS_PLATES)
                .define('C', ModBlocks.POWER_CONVERTER_EXTREMELY_BIG)
                .unlockedBy(TofusThinkingDatagen.hasItem(ModBlocks.POWER_CONVERTER_EXTREMELY_BIG), TofusThinkingDatagen.has(ModBlocks.POWER_CONVERTER_EXTREMELY_BIG))
                .save(provider);
    }

    public static <T extends Block> void tofuAnvil(DataGenContext<Block, T> ctx, RegistrumRecipeProvider provider) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), 1)
                .pattern("BCD")
                .pattern("AEA")
                .pattern("FGH")
                .define('A', ModBlocks.SPECTRAL_ANVIL)
                .define('B', Tags.Items.STORAGE_BLOCKS_DIAMOND)
                .define('C', AddonItemTags.STORAGE_BLOCKS_AMETHYST)
                .define('D', Tags.Items.STORAGE_BLOCKS_EMERALD)
                .define('E', ModItemTags.STORAGE_BLOCKS_AMBER)
                .define('F', ModItemTags.STORAGE_BLOCKS_RUBY)
                .define('G', ModItemTags.STORAGE_BLOCKS_TOPAZ)
                .define('H', ModItemTags.STORAGE_BLOCKS_SAPPHIRE)
                .unlockedBy(TofusThinkingDatagen.hasItem(ModBlocks.SPECTRAL_ANVIL), TofusThinkingDatagen.has(ModBlocks.SPECTRAL_ANVIL))
                .save(provider);
    }
}
