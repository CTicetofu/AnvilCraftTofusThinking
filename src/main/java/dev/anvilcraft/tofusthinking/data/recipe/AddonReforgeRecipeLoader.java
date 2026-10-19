package dev.anvilcraft.tofusthinking.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.data.TofusThinkingDatagen;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.recipe.reforge.ReforgeRecipe;
import dev.dubhe.anvilcraft.init.item.ModComponents;
import dev.dubhe.anvilcraft.init.item.ModItems;
import dev.dubhe.anvilcraft.item.property.component.Providence;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;

public class AddonReforgeRecipeLoader {
    public static void init(RegistrumRecipeProvider provider) {

        ReforgeRecipe.builder()
                .activatorAll()
                .requires(Items.BLAZE_POWDER, 2)
                .requires(Items.NETHERITE_SCRAP, 2)
                .addComponent(DataComponents.FIRE_RESISTANT, Unit.INSTANCE)
                .unlockedBy(TofusThinkingDatagen.hasItem(Items.NETHERITE_INGOT), TofusThinkingDatagen.has(Items.NETHERITE_INGOT))
                .save(provider, AnvilCraftTofusThinking.of("reforge/all_fire_resistant"));

        ReforgeRecipe.builder()
                .activatorAllWithComponent(DataComponents.FIRE_RESISTANT,Unit.INSTANCE)
                .require("can_repair")
                .require("damageable")
                .requires(ModItems.EMBER_METAL_INGOT)
                .fluid(Fluids.LAVA, 1000, true)
                .maxQuantity(1)
                .addComponent(ModComponents.FIRE_REFORGING, com.mojang.datafixers.util.Unit.INSTANCE)
                .unlockedBy(TofusThinkingDatagen.hasItem(ModItems.EMBER_METAL_INGOT), TofusThinkingDatagen.has(ModItems.EMBER_METAL_INGOT))
                .save(provider, AnvilCraftTofusThinking.of("reforge/can_repair_fire_reforging"));

        ReforgeRecipe.builder()
                .requires(ModItems.TRANSCENDIUM_NUGGET,8)
                .requires(Items.RABBIT_FOOT,4)
                .requires(Items.ENCHANTED_GOLDEN_APPLE)
                .maxQuantity(1)
                .addComponent(ModComponents.PROVIDENCE, Providence.INSTANCE)
                .unlockedBy(TofusThinkingDatagen.hasItem(ModItems.TRANSCENDIUM_NUGGET), TofusThinkingDatagen.has(ModItems.TRANSCENDIUM_NUGGET))
                .save(provider, AnvilCraftTofusThinking.of("reforge/providence"));

        ReforgeRecipe.builder()
                .activator(ModItems.WEATHERPROOF_SPACESUIT_CHESTPLATE)
                .requires(ModItems.TRANSCENDIUM_INGOT.asItem())
                .requires(Items.NETHERITE_BLOCK)
                .addComponent(AddonComponents.DENSE,Unit.INSTANCE)
                .maxQuantity(1)
                .unlockedBy(TofusThinkingDatagen.hasItem(ModItems.WEATHERPROOF_SPACESUIT_CHESTPLATE), TofusThinkingDatagen.has(ModItems.WEATHERPROOF_SPACESUIT_CHESTPLATE))
                .save(provider,AnvilCraftTofusThinking.of("reforge/dense_chestplate"));

        ReforgeRecipe.builder()
                .activator(ModItems.WEATHERPROOF_SPACESUIT_HELMET)
                .requires(Items.SPIDER_EYE)
                .requires(Items.PUFFERFISH)
                .requires(Items.POISONOUS_POTATO)
                .requires(Items.LILY_OF_THE_VALLEY)
                .requires(Items.WITHER_ROSE)
                .fluid(NeoForgeMod.MILK.get(), 1000, true)
                .addComponent(AddonComponents.ANTIDOTE,Unit.INSTANCE)
                .maxQuantity(1)
                .unlockedBy(TofusThinkingDatagen.hasItem(ModItems.WEATHERPROOF_SPACESUIT_HELMET), TofusThinkingDatagen.has(ModItems.WEATHERPROOF_SPACESUIT_HELMET))
                .save(provider,AnvilCraftTofusThinking.of("reforge/antidote_helmet"));
    }
}
