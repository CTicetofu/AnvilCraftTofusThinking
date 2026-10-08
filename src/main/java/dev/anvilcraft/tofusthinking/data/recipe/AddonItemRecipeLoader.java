package dev.anvilcraft.tofusthinking.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.DataGenContext;
import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.data.TofusThinkingDatagen;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import dev.anvilcraft.tofusthinking.util.DataClass.EnchantmentKeyInstance;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.item.ModItemTags;
import dev.dubhe.anvilcraft.init.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;

public class AddonItemRecipeLoader {
    public static <T extends Item> void autoCan(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                .pattern(" B ")
                .pattern(" A ")
                .pattern(" B ")
                .define('A', ModItems.TIN_CAN)
                .define('B', Items.HOPPER)
                .unlockedBy("has_tin_can", RegistrumRecipeProvider.has(ModItems.TIN_CAN))
                .save(provider);
    }
    public static <T extends Item> void curseSnowball(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(),4)
                .pattern(" B ")
                .pattern("BAB")
                .pattern(" B ")
                .define('A', ModItems.CURSED_GOLD_NUGGET.asItem())
                .define('B', Items.SNOWBALL)
                .unlockedBy("has_snowball", RegistrumRecipeProvider.has(ModItems.CURSED_GOLD_INGOT.asItem()))
                .save(provider);
    }

    public static <T extends Item> void amethystHammer(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, enchant(ctx.get(),provider.getProvider(),new EnchantmentKeyInstance(Enchantments.EFFICIENCY,3),new EnchantmentKeyInstance(Enchantments.BREACH,4)))
                .pattern("BBB")
                .pattern("BAB")
                .pattern(" A ")
                .define('A', Items.STICK)
                .define('B', Items.AMETHYST_SHARD)
                .unlockedBy("has_amethyst", RegistrumRecipeProvider.has(Items.AMETHYST_SHARD))
                .save(provider);
    }

    public static <T extends Item> void royalSteelHammer(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider){
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.ROYAL_STEEL_UPGRADE_SMITHING_TEMPLATE),Ingredient.of(AddonItems.AMETHYST_HAMMER),Ingredient.of(ModItems.ROYAL_STEEL_INGOT),RecipeCategory.TOOLS,ctx.get()
                )
                .unlocks("has_item", TofusThinkingDatagen.has(AddonItems.AMETHYST_HAMMER))
                .save(provider, AnvilCraftTofusThinking.of("smithing/royal_steel_hammer"));
    }

    public static <T extends Item> void lightningHammer(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider){
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.TOPAZ),Ingredient.of(AddonItems.ROYAL_STEEL_HAMMER),Ingredient.of(ModItems.CURSED_GOLD_INGOT),RecipeCategory.TOOLS,ctx.get()
                )
                .unlocks("has_item", TofusThinkingDatagen.has(AddonItems.ROYAL_STEEL_HAMMER))
                .save(provider, AnvilCraftTofusThinking.of("smithing/lightning_hammer"));
    }

    public static <T extends Item> void amethystGoldenRIng(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                .pattern("BAB")
                .pattern("A A")
                .pattern("BAB")
                .define('A', Items.GOLD_INGOT)
                .define('B', Items.AMETHYST_SHARD)
                .unlockedBy(TofusThinkingDatagen.hasItem(Items.AMETHYST_SHARD), RegistrumRecipeProvider.has(Items.AMETHYST_SHARD))
                .save(provider);
    }

    public static <T extends Item> void elasticAntiFireShield(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                .pattern("BCB")
                .pattern("BAB")
                .pattern(" B ")
                .define('A', ModBlocks.RESIN_BLOCK.asItem())
                .define('B', ModItemTags.TUNGSTEN_NUGGETS)
                .define('C', ModItemTags.TUNGSTEN_PLATES)
                .unlockedBy(TofusThinkingDatagen.hasItem(ModBlocks.RESIN_BLOCK.asItem()), RegistrumRecipeProvider.has(ModBlocks.RESIN_BLOCK.asItem()))
                .save(provider);
    }

    public static <T extends Item> void speedCharm(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                .pattern("BAC")
                .pattern("BBA")
                .pattern("BAC")
                .define('A', Items.AMETHYST_SHARD)
                .define('B', ModItemTags.STORAGE_BLOCKS_SUGAR)
                .define('C', Items.PRISMARINE_SHARD)
                .unlockedBy(TofusThinkingDatagen.hasItem(Items.AMETHYST_SHARD), RegistrumRecipeProvider.has(Items.AMETHYST_SHARD))
                .save(provider);
    }

    public static <T extends Item> void starOfTheSea(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                .pattern("BCB")
                .pattern("BAB")
                .pattern(" B ")
                .define('A', Items.NETHER_STAR)
                .define('B', Items.NAUTILUS_SHELL)
                .define('C', Items.HEART_OF_THE_SEA)
                .unlockedBy("has_heart_of_the_sea", RegistrumRecipeProvider.has(Items.HEART_OF_THE_SEA))
                .save(provider);
    }

    public static <T extends Item> void gemStaff(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                .pattern("CBC")
                .pattern("DAD")
                .pattern(" A ")
                .define('A', ModItems.ROYAL_STEEL_INGOT)
                .define('B', Items.TINTED_GLASS)
                .define('C', Items.AMETHYST_BLOCK)
                .define('D', ModItems.RESIN)
                .unlockedBy(TofusThinkingDatagen.hasItem(Items.AMETHYST_BLOCK), RegistrumRecipeProvider.has(Items.AMETHYST_BLOCK))
                .save(provider);
    }

    public static <T extends Item> void electromagneticCrossbow(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                .pattern("AC ")
                .pattern("CAD")
                .pattern(" DB")
                .define('A', ModBlocks.MAGNETO_ELECTRIC_CORE_BLOCK.asItem())
                .define('B', Items.CROSSBOW)
                .define('C', Items.IRON_INGOT)
                .define('D', ModItems.CAPACITOR_EMPTY)
                .unlockedBy(TofusThinkingDatagen.hasItem(Items.CROSSBOW), RegistrumRecipeProvider.has(Items.CROSSBOW))
                .save(provider);
    }

    public static <T extends Item> void conduitStaff(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
                .pattern("CBC")
                .pattern("BAB")
                .pattern("CBC")
                .define('A', Items.CONDUIT)
                .define('B', Items.SEA_LANTERN)
                .define('C', ModBlocks.INDUCTION_LIGHT.asItem())
                .unlockedBy(TofusThinkingDatagen.hasItem(Items.CONDUIT), RegistrumRecipeProvider.has(Items.CONDUIT))
                .save(provider);
    }

    public static <T extends Item> void originalConduitStaff(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider){
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.SUPER_CAPACITOR_EMPTY),
                        Ingredient.of(AddonItems.CONDUIT_STAFF),
                        Ingredient.of(AddonBlocks.ORIGINAL_CONDUIT.asItem()),
                        RecipeCategory.TOOLS,ctx.get()
                )
                .unlocks(TofusThinkingDatagen.hasItem(AddonBlocks.ORIGINAL_CONDUIT.asItem()), TofusThinkingDatagen.has(AddonBlocks.ORIGINAL_CONDUIT.asItem()))
                .save(provider, AnvilCraftTofusThinking.of("smithing/original_conduit_staff"));
    }

    public static <T extends Item> void sonicBoomStaff(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider){
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.CAPACITOR_EMPTY),
                        Ingredient.of(ModItems.ROYAL_ANVIL_HAMMER),
                        Ingredient.of(Items.SCULK_SHRIEKER),
                        RecipeCategory.TOOLS,ctx.get()
                )
                .unlocks(TofusThinkingDatagen.hasItem(Items.SCULK_SHRIEKER), TofusThinkingDatagen.has(Items.SCULK_SHRIEKER))
                .save(provider, AnvilCraftTofusThinking.of("smithing/sonic_boom_staff"));
    }

    public static ItemStack enchant(ItemLike item, HolderLookup.Provider registries, EnchantmentKeyInstance... instances){
        ItemStack stack = item.asItem().getDefaultInstance();
        return enchantStack(stack,registries,instances);
    }

    public static ItemStack enchantStack(ItemStack stack, HolderLookup.Provider registries, EnchantmentKeyInstance... instances){
        for (EnchantmentKeyInstance instance : instances){
            var holder = registries.holder(instance.enchantment);
            holder.ifPresent(enchantmentReference -> stack.enchant(enchantmentReference, instance.level));
        }
        return stack;
    }
}
