package dev.anvilcraft.tofusthinking.init.item;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.lib.v2.registrum.util.entry.ItemEntry;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.data.TofusThinkingDatagen;
import dev.anvilcraft.tofusthinking.data.recipe.AddonItemRecipeLoader;
import dev.anvilcraft.tofusthinking.init.block.AddonFluids;
import dev.anvilcraft.tofusthinking.item.curio.CurioBaseItem;
import dev.anvilcraft.tofusthinking.item.food.AutoCanItem;
import dev.anvilcraft.tofusthinking.item.tool.HammerItem;
import dev.anvilcraft.tofusthinking.item.weapon.ConduitStaff;
import dev.anvilcraft.tofusthinking.item.weapon.CurseSnowballItem;
import dev.anvilcraft.tofusthinking.item.weapon.SonicBoomStaff;
import dev.anvilcraft.tofusthinking.item.weapon.StarOfTheSea;
import dev.anvilcraft.tofusthinking.util.DataClass.AttributeInstance;
import dev.anvilcraft.tofusthinking.util.DataClass.EnchantmentKeyInstance;
import dev.dubhe.anvilcraft.init.item.ModItems;
import dev.dubhe.anvilcraft.item.ModTiers;
import dev.dubhe.anvilcraft.util.DataGenUtil;
import dev.dubhe.anvilcraft.util.registrater.ModelProviderUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import top.theillusivec4.curios.api.SlotAttribute;

import static dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking.REGISTRUM;

@SuppressWarnings("unused")
public class AddonItems {
    public static void register() {
    }

    public static final ItemEntry<AutoCanItem> AUTO_CAN = REGISTRUM.item("auto_can",properties -> new AutoCanItem(properties.rarity(Rarity.UNCOMMON).stacksTo(1)))
            .model(DataGenUtil::noExtraModelOrState)
            .register();
    public static final ItemEntry<CurioBaseItem> CHARM_AMULET = REGISTRUM.item("charm_amulet",properties ->
            new CurioBaseItem(properties.stacksTo(1).rarity(Rarity.UNCOMMON)).withAttribute(new AttributeInstance(SlotAttribute.getOrCreate("charm"),3, AttributeModifier.Operation.ADD_VALUE)))
            .defaultModel()
            .tag(AddonItemTags.CURIOS_CHARM)
            .register();
    public static final ItemEntry<CurseSnowballItem> CURSE_SNOWBALL_ITEM = REGISTRUM.item("curse_snowball",properties -> new CurseSnowballItem())
            .defaultModel()
            .recipe(AddonItemRecipeLoader::curseSnowball)
            .register();

    public static final ItemEntry<HammerItem> AMETHYST_HAMMER = REGISTRUM.item("amethyst_hammer",properties -> new HammerItem(ModTiers.AMETHYST,properties.attributes(HammerItem.createAttributes(ModTiers.AMETHYST,8f, -3.3f))))
            .model((ctx, provider) -> provider.handheld(ctx))
            .tag(ItemTags.PICKAXES,ItemTags.AXES,ItemTags.WEAPON_ENCHANTABLE,Tags.Items.MELEE_WEAPON_TOOLS,Tags.Items.MINING_TOOL_TOOLS)
            .recipe(AddonItemRecipeLoader::amethystHammer)
            .register();

    public static final ItemEntry<HammerItem> ROYAL_STEEL_HAMMER = REGISTRUM.item("royal_steel_hammer",properties -> new HammerItem(ModTiers.ROYAL,properties.attributes(HammerItem.createAttributes(ModTiers.ROYAL,8f, -3.2f))))
            .model((ctx, provider) -> provider.handheld(ctx))
            .tag(ItemTags.PICKAXES,ItemTags.AXES,ItemTags.WEAPON_ENCHANTABLE,Tags.Items.MELEE_WEAPON_TOOLS,Tags.Items.MINING_TOOL_TOOLS)
            .recipe(AddonItemRecipeLoader::royalSteelHammer)
            .register();

    public static final ItemEntry<BucketItem> NUTRIENT_LIQUID_BUCKET = REGISTRUM.item("nutrient_liquid_bucket", props -> new BucketItem(AddonFluids.NUTRIENT_LIQUID.get(), props.stacksTo(1).craftRemainder(Items.BUCKET)))
            .model(ModelProviderUtil::bucket)
            .register();

    public static final ItemEntry<StarOfTheSea> STAR_OF_THE_SEA = REGISTRUM.item("star_of_the_sea",properties ->
                    new StarOfTheSea(properties.stacksTo(1).rarity(Rarity.RARE)))
            .recipe(AddonItemRecipeLoader::starOfTheSea)
            .defaultModel()
            .register();

    public static final ItemEntry<ConduitStaff> CONDUIT_STAFF = REGISTRUM.item("conduit_staff",properties ->
                    new ConduitStaff(properties.stacksTo(1).rarity(Rarity.RARE)))
            .model(DataGenUtil::noExtraModelOrState)
            .recipe(AddonItemRecipeLoader::conduitStaff)
            .tag(AddonItemTags.NORMAL_MOVEMENT_WHEN_USE)
            .register();

    public static final ItemEntry<SonicBoomStaff> SONIC_BOOM_STAFF = REGISTRUM.item("sonic_boom_staff", properties ->
                    new SonicBoomStaff(properties.stacksTo(1).rarity(Rarity.RARE)))
            .model(DataGenUtil::noExtraModelOrState)
            .recipe(AddonItemRecipeLoader::sonicBoomStaff)
            .tag(AddonItemTags.NORMAL_MOVEMENT_WHEN_USE)
            .register();

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
