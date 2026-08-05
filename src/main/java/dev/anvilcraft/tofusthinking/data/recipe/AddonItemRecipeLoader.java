package dev.anvilcraft.tofusthinking.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.DataGenContext;
import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.data.TofusThinkingDatagen;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import dev.anvilcraft.tofusthinking.util.DataClass.EnchantmentKeyInstance;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
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
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, enchant(ctx.get(),provider.getProvider(),new EnchantmentKeyInstance(Enchantments.SMITE,2),new EnchantmentKeyInstance(Enchantments.EFFICIENCY,3),new EnchantmentKeyInstance(Enchantments.BREACH,4)))
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

    public static <T extends Item> void starOfTheSea(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, enchant(ctx.get(),provider.getProvider(),new EnchantmentKeyInstance(Enchantments.SMITE,2),new EnchantmentKeyInstance(Enchantments.EFFICIENCY,3),new EnchantmentKeyInstance(Enchantments.BREACH,4)))
                .pattern("BCB")
                .pattern("BAB")
                .pattern(" B ")
                .define('A', Items.NETHER_STAR)
                .define('B', Items.NAUTILUS_SHELL)
                .define('C', Items.HEART_OF_THE_SEA)
                .unlockedBy("has_amethyst", RegistrumRecipeProvider.has(Items.HEART_OF_THE_SEA))
                .save(provider);
    }

    public static <T extends Item> void conduitStaff(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, enchant(ctx.get(),provider.getProvider(),new EnchantmentKeyInstance(Enchantments.SMITE,2),new EnchantmentKeyInstance(Enchantments.EFFICIENCY,3),new EnchantmentKeyInstance(Enchantments.BREACH,4)))
                .pattern("CBC")
                .pattern("BAB")
                .pattern("CBC")
                .define('A', Items.CONDUIT)
                .define('B', Items.SEA_LANTERN)
                .define('C', ModBlocks.INDUCTION_LIGHT.asItem())
                .unlockedBy("has_amethyst", RegistrumRecipeProvider.has(Items.CONDUIT))
                .save(provider);
    }

    public static <T extends Item> void sonicBoomStaff(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider){
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.CAPACITOR_EMPTY),
                        Ingredient.of(ModItems.ROYAL_ANVIL_HAMMER),
                        Ingredient.of(Items.SCULK_SHRIEKER),
                        RecipeCategory.TOOLS,ctx.get()
                )
                .unlocks("has_item", TofusThinkingDatagen.has(Items.SCULK_SHRIEKER))
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
