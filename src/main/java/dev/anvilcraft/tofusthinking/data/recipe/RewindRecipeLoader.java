package dev.anvilcraft.tofusthinking.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.block.OriginalConduitBlock;
import dev.anvilcraft.tofusthinking.data.TofusThinkingDatagen;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.recipe.anvil.RewindRecipe;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.item.ModComponents;
import dev.dubhe.anvilcraft.init.item.ModItems;
import dev.dubhe.anvilcraft.init.recipe.ModRecipeTriggers;
import dev.dubhe.anvilcraft.recipe.anvil.builder.ExtendInWorldRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class RewindRecipeLoader {
    public static ResourceLocation path(String name){
        return AnvilCraftTofusThinking.of(name).withPrefix("rewind/");
    }
    public static void init(RegistrumRecipeProvider provider) {

        RewindRecipe.builder()
                .requires(ModBlocks.CURSED_GOLD_BLOCK)
                .result(Blocks.GOLD_BLOCK)
                .save(provider,path("gold_block"));

        RewindRecipe.builder()
                .requires(ModItems.CURSED_GOLD_INGOT.get())
                .result(Items.GOLD_INGOT)
                .save(provider, path("gold_ingot"));

        RewindRecipe.builder()
                .requires(ModItems.CURSED_GOLD_NUGGET.get())
                .result(Items.GOLD_NUGGET)
                .save(provider, path("gold_nugget"));

        RewindRecipe.builder()
                .requires(ModBlocks.AMBER_BLOCK)
                .result(ModBlocks.RESIN_BLOCK)
                .save(provider,path("resin_block"));

        RewindRecipe.builder()
                .requires(ModBlocks.CORRUPTED_BEACON)
                .result(Blocks.BEACON)
                .save(provider,path("beacon"));

        RewindRecipe.builder()
                .requires(Blocks.PRISMARINE_BRICKS)
                .requires(Items.PRISMARINE_CRYSTALS)
                .result(AddonBlocks.STABLE_PRISMARINE_BRICKS)
                .save(provider,path("stable_prismarine_bricks"));

        RewindRecipe.builder()
                .requires(ModItems.AMBER)
                .result(ModItems.RESIN)
                .save(provider,path("resin"));

        RewindRecipe.builder()
                .requires(ModItems.TRANSCENDENCE_ANVIL_HAMMER)
                .result(ModItems.EMBER_ANVIL_HAMMER)
                .result(ModItems.FROST_ANVIL_HAMMER)
                .save(provider,path("divide_anvil_hammer"));

        RewindRecipe.builder()
                .requires(ModItems.TRANSCENDENCE_HEAVY_HALBERD)
                .result(ModItems.EMBER_METAL_HEAVY_HALBERD)
                .result(ModItems.FROST_METAL_HEAVY_HALBERD)
                .save(provider,path("divide_heavy_halberd"));

        RewindRecipe.builder()
                .requires(ModItems.TRANSCENDENCE_RESONATOR)
                .result(ModItems.EMBER_METAL_RESONATOR)
                .result(ModItems.FROST_METAL_RESONATOR)
                .save(provider,path("divide_resonator"));

        RewindRecipe.builder()
                .requires(ModItems.TRANSCENDENCE_DRAGON_ROD)
                .result(ModItems.EMBER_DRAGON_ROD)
                .result(ModItems.FROST_DRAGON_ROD)
                .save(provider,path("divide_dragon_rod"));

        RewindRecipe.builder()
                .requires(ModBlocks.TRANSCENDENCE_ANVIL)
                .result(ModBlocks.EMBER_ANVIL)
                .result(ModBlocks.FROST_ANVIL)
                .save(provider,path("divide_anvil"));

        ExtendInWorldRecipeBuilder.extendCompatible(ModRecipeTriggers.ON_ANVIL_FALL_ON)
                .hasCauldron(0, -1, 0)
                .hasBlock(builder -> builder
                        .of(AddonBlocks.ORIGINAL_CONDUIT.get())
                        .with(OriginalConduitBlock.OPEN, true)
                        .offset(0, -2, 0)
                )
                .hasItemIngredient(builder -> builder
                        .of(ModBlocks.MOB_AMBER_BLOCK)
                        .offset(0.0, -0.375, 0.0)
                        .range(0.75, 0.75, 0.75)
                        .saveComponent(ModComponents.SAVED_ENTITY, AnvilCraft.of("saved_entity"))
                )
                .spawnItem(builder -> builder
                        .item(ModBlocks.RESIN_BLOCK)
                        .offset(0.0, -0.75, 0.0)
                        .applyComponent(ModComponents.SAVED_ENTITY, AnvilCraft.of("saved_entity"))
                )
                .maxEfficiency(1)
                .unlockedBy(TofusThinkingDatagen.hasItem(ModBlocks.MOB_AMBER_BLOCK), TofusThinkingDatagen.has(ModBlocks.MOB_AMBER_BLOCK))
                .group("rewind")
                .icon(ModBlocks.MOB_AMBER_BLOCK.asStack())
                .save(provider, AnvilCraftTofusThinking.of("mob_amber_block"));

        /*ExtendInWorldRecipeBuilder.extendCompatible(ModRecipeTriggers.ON_ANVIL_FALL_ON)
                .hasCauldron(0, -1, 0)
                .hasBlock(builder -> builder
                        .of(AddonBlocks.ORIGINAL_CONDUIT.get())
                        .with(OriginalConduitBlock.OPEN, true)
                        .offset(0, -2, 0)
                )
                .hasItemIngredient(builder -> builder
                        .of(ModBlocks.MOB_AMBER_BLOCK)
                        .offset(0.0, -0.375, 0.0)
                        .range(0.75, 0.75, 0.75)
                        .with(
                                ModItemSubPredicates.SAVED_ENTITY.get(),
                                ItemSavedEntityPredicate.any()
                        )
                        .saveComponent(ModComponents.SAVED_ENTITY, AnvilCraft.of("saved_entity"))
                )
                .out(new ResurrectionAmberOutcome(
                        new Vec3(0.0, -0.75, 0.0),
                        AnvilCraft.of("saved_entity")
                ))
                .maxEfficiency(1)
                .unlockedBy(TofusThinkingDatagen.hasItem(ModBlocks.AMBER_BLOCK.get()), TofusThinkingDatagen.has(ModBlocks.AMBER_BLOCK))
                .group("rewind")
                .icon(ModBlocks.RESENTFUL_AMBER_BLOCK.asStack())
                .save(provider, AnvilCraftTofusThinking.of("resurrect_amber_block"));*/
    }
}
