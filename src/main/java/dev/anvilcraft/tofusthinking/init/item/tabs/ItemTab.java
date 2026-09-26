package dev.anvilcraft.tofusthinking.init.item.tabs;

import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import dev.anvilcraft.tofusthinking.util.DataClass.EnchantmentKeyInstance;
import net.minecraft.world.item.enchantment.Enchantments;

public class ItemTab extends BaseCreativeTab{
    @Override
    public void init() {
        this.accept(AddonItems.AUTO_CAN);
        this.accept(AddonItems.CHARM_AMULET);
        this.accept(AddonItems.CURSE_SNOWBALL_ITEM);
        this.acceptEnchant(AddonItems.AMETHYST_HAMMER,new EnchantmentKeyInstance(Enchantments.SMITE,2),new EnchantmentKeyInstance(Enchantments.EFFICIENCY,3),new EnchantmentKeyInstance(Enchantments.BREACH,4));
        this.acceptEnchant(AddonItems.ROYAL_STEEL_HAMMER,new EnchantmentKeyInstance(Enchantments.SMITE,2),new EnchantmentKeyInstance(Enchantments.EFFICIENCY,3),new EnchantmentKeyInstance(Enchantments.BREACH,4));
        this.accept(AddonItems.STAR_OF_THE_SEA);
        this.accept(AddonItems.CONDUIT_STAFF);
        this.acceptFullEnergy(AddonItems.CONDUIT_STAFF);
        this.accept(AddonItems.SONIC_BOOM_STAFF);
        this.acceptFullEnergy(AddonItems.SONIC_BOOM_STAFF);
        this.accept(AddonItems.ORIGINAL_CONDUIT_STAFF);
        this.acceptFullEnergy(AddonItems.ORIGINAL_CONDUIT_STAFF);
        this.accept(AddonBlocks.ORIGINAL_CONDUIT.asItem());
        this.accept(AddonBlocks.OVERLOAD_GENERATOR.asItem());
        this.accept(AddonBlocks.STABLE_PRISMARINE_BRICKS.asItem());
        this.accept(AddonBlocks.SMART_POWER_CONVERTER.asItem());
        this.accept(AddonBlocks.SMART_POWER_CONVERTER_EXTREMELY_BIG.asItem());
        this.accept(AddonBlocks.TOFU_ANVIL.asItem());
    }
}
