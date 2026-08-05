package dev.anvilcraft.tofusthinking.data.lang;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.init.AddonMobEffects;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.init.block.AddonFluids;
import dev.anvilcraft.tofusthinking.init.entity.AddonEntities;
import dev.anvilcraft.tofusthinking.init.item.AddonItemGroups;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class AddonEnUsLangGen extends LanguageProvider {
    public AddonEnUsLangGen(PackOutput output) {
        super(output, AnvilCraftTofusThinking.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        itemName();
        blockName();
        tooltipLang();
        entityName();
        addOther();
    }
    private void itemName(){
        add(AddonItems.AUTO_CAN.asItem(),"Auto Can");
        add(AddonItems.CHARM_AMULET.asItem(),"Charm Amulet");
        add(AddonItems.CURSE_SNOWBALL_ITEM.asItem(),"Curse Snowball");
        add(AddonItems.AMETHYST_HAMMER.asItem(),"Amethyst Hammer");
        add(AddonItems.ROYAL_STEEL_HAMMER.asItem(),"Royal Steel Hammer");
        add(AddonItems.NUTRIENT_LIQUID_BUCKET.asItem(),"Nutrient Liquid Bucket");
        add(AddonItems.STAR_OF_THE_SEA.asItem(),"Star of the Sea");
        add(AddonItems.CONDUIT_STAFF.asItem(),"Conduit Staff");
        add(AddonItems.SONIC_BOOM_STAFF.asItem(),"Sonic Boom Staff");
    }
    private void blockName(){
        add(AddonBlocks.STABLE_PRISMARINE_BRICKS.get(),"Stable Prismarine Bricks");
        add(AddonBlocks.ORIGINAL_CONDUIT.get(),"Originalization Conduit");
        add(AddonBlocks.NUTRIENT_EXTRACTOR.get(),"Nutrient Extractor");
        add(AddonBlocks.SMART_POWER_CONVERTER.get(),"Smart Power Converter");
    }
    private void tooltipLang(){
        add("tooltip.anvilcraft_tofus_thinking.auto_can_storage","The nutritional value of storage: %1$d/%2$d");
        add("tooltip.anvilcraft_tofus_thinking.auto_can","Right-click the item on food to absorb its nutritional value \nwhich will replenish the energy of the holder when carried");
        add("tooltip.anvilcraft_tofus_thinking.curse_snowball","Cause the target to be cursed and haunted");
        add("tooltip.anvilcraft_tofus_thinking.wither_immune","Immune to Wither");
        add("tooltip.anvilcraft_tofus_thinking.hammer_mite_undead","Deal an additional 50% damage to undead creatures");
        add("tooltip.anvilcraft_tofus_thinking.hammer_interrupt_use","Interrupting the target's use of an item");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea","Used at the right time, it can backfire on the attacker");

        add("tooltip.anvilcraft_tofus_thinking.right_switch_in_inventory","Right-click in the inventory to toggle whether it is in %s");
        add("tooltip.anvilcraft_tofus_thinking.auto_hunting_mode","Auto Attack Mode");
        add("tooltip.anvilcraft_tofus_thinking.conduit_staff_auto","it automatically deals damage to nearby monsters when holding");
        add("tooltip.anvilcraft_tofus_thinking.conduit_staff_normal","Deal damage to nearby creatures pointed by the crosshair");
        add("tooltip.anvilcraft_tofus_thinking.conduit_staff_recovery","Restore energy slowly when in the rain or water");

        add("tooltip.anvilcraft_tofus_thinking.need_energy","Consume %s when use");
        add("tooltip.anvilcraft_tofus_thinking.not_active","Not Active");

    }
    private void entityName(){
        add(AddonEntities.CURSE_SNOWBALL.get(),"Curse Snowball");
    }
    private void addOther(){
        add(AddonFluids.NUTRIENT_LIQUID_TYPE.get().getDescriptionId(),"Nutrient Liquid");

        add(AddonMobEffects.CURSE.get().getDescriptionId(),"Curse");
        add(AddonMobEffects.SHRINK.get().getDescriptionId(),"Shrink");
        add(AddonMobEffects.DULL.get().getDescriptionId(),"Dull");

        add("death.attack.tofusThinking.rewind","%s has never been born");
        add("death.attack.tofusThinking.counter","%s impulsively attacked %s");

        add(AddonItemGroups.ITEM_TAB_ID,"AnvilCraft: Tofu's Thinking");
    }
}
