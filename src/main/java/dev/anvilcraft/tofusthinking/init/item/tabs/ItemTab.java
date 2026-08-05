package dev.anvilcraft.tofusthinking.init.item.tabs;

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
        this.accept(AddonItems.NUTRIENT_LIQUID_BUCKET);
        this.accept(AddonItems.STAR_OF_THE_SEA);
        this.accept(AddonItems.CONDUIT_STAFF);
        this.acceptFullEnergy(AddonItems.CONDUIT_STAFF);
        this.accept(AddonItems.SONIC_BOOM_STAFF);
        this.acceptFullEnergy(AddonItems.SONIC_BOOM_STAFF);
    }
}
