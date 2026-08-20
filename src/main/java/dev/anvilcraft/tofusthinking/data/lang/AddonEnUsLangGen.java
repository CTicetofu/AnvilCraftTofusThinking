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
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea","Used at the right time, it can backfire on the attacker. \nIt can also be used to absorb certain magic or the power of time");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_none","Empty");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_sonic_boom","Sonic Boom");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_effect_sonic_boom","When full progress, right-click to activate the Sonic Boom Staff in the inventory");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_rewind","Rewind");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_effect_rewind","When full progress, right-click to original the Conduit in the inventory");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_rewind_remain","Rewind Remain");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_effect_rewind_remain","right-click to original the Conduit Staff in the inventory");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_lost_in_time","Lost In Time");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_effect_lost_in_time","Injecting it into a prepared Wither causes it to mutate");
        add("tooltip.anvilcraft_tofus_thinking.original_conduit","Another mutated power of Wither can restore some things to their original state");
        add("tooltip.anvilcraft_tofus_thinking.original_conduit_build", """
                It can be activated as long as there is water and eight frame blocks within a 3x3 range on this layer
                When there are more than 8 Stable Prismarine Bricks
                the effect will be triggered regardless of whether the surrounding living entity are in the rain or water
                Attack nearby enemy monsters when there are no fewer than 24 frame blocks\
                """);
        add("tooltip.anvilcraft_tofus_thinking.original_conduit_warn","Don't let it be influenced by another kind of time power");
        add("tooltip.anvilcraft_tofus_thinking.right_switch_in_inventory","Right-click in the inventory to toggle whether it is in %s");
        add("tooltip.anvilcraft_tofus_thinking.auto_hunting_mode","Auto Attack Mode");
        add("tooltip.anvilcraft_tofus_thinking.conduit_staff_auto","it automatically deals damage to nearby monsters when holding");
        add("tooltip.anvilcraft_tofus_thinking.conduit_staff_normal","Deal damage to nearby creatures pointed by the crosshair");
        add("tooltip.anvilcraft_tofus_thinking.conduit_staff_recovery","Restore energy slowly when in the rain or water");

        add("tooltip.anvilcraft_tofus_thinking.need_energy","Consume %s when use");
        add("tooltip.anvilcraft_tofus_thinking.not_active","Not Active");
        add("tooltip.anvilcraft_tofus_thinking.progress","Progress: %s %%");

    }
    private void entityName(){
        add(AddonEntities.CURSE_SNOWBALL.get(),"Curse Snowball");
        add(AddonEntities.STRANGE_WITHER.get(),"Strange Wither");
        add(AddonEntities.STRANGE_WITHER_SKULL.get(),"Strange Wither Skull");
    }
    private void addOther(){
        add(AddonFluids.NUTRIENT_LIQUID_TYPE.get().getDescriptionId(),"Nutrient Liquid");

        add(AddonMobEffects.CURSE.get().getDescriptionId(),"Curse");
        add(AddonMobEffects.SHRINK.get().getDescriptionId(),"Shrink");
        add(AddonMobEffects.DULL.get().getDescriptionId(),"Dull");

        add("death.attack.tofusThinking.rewind","%s has never been born");
        add("death.attack.tofusThinking.rewind_attack","%s has not been proven to exist by %s");
        add("death.attack.tofusThinking.tofusThinking.bounce_wither_skull","%s was killed by the wither skull %s had launch");
        add("death.attack.tofusThinking.counter","%s impulsively attacked %s");

        add(AddonItemGroups.ITEM_TAB_ID,"AnvilCraft: Tofu's Thinking");

        add("gui.anvilcraft_tofus_thinking.category.rewind","Rewind");
        add("gui.anvilcraft_tofus_thinking.category.rewind.need_activated","Need Activated");

        add("jei.anvilcraft_tofus_thinking.info.original_conuit","By default, the Star Of The Sea is used to absorb the blue Wither Head of a strange Wither and inject it into an Conduit to obtain it. In its original state, it is difficult to control precisely and is not suitable for direct use as a wand material.");
        add("jei.anvilcraft_tofus_thinking.info.charm_amulet","By default, it is obtained by stamping with six different charms on an anvil");
    }
}
