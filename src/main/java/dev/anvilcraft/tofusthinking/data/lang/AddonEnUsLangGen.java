package dev.anvilcraft.tofusthinking.data.lang;

import dev.anvilcraft.lib.v2.config.ConfigData;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.config.AnvilCraftTofusThinkCommonConfig;
import dev.anvilcraft.tofusthinking.config.AnvilCraftTofusThinkServerConfig;
import dev.anvilcraft.tofusthinking.init.AddonMobEffects;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
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
        addConfig();
        addOther();
    }
    private void itemName(){
        add(AddonItems.AUTO_CAN.asItem(),"Auto Can");
        add(AddonItems.CHARM_AMULET.asItem(),"Charm Amulet");
        add(AddonItems.CURSE_SNOWBALL_ITEM.asItem(),"Curse Snowball");
        add(AddonItems.AMETHYST_GOLDEN_RING.asItem(),"Amethyst Golden Ring");
        add(AddonItems.SPEED_CHARM.asItem(),"Speed Charm");
        add(AddonItems.AMETHYST_HAMMER.asItem(),"Amethyst Hammer");
        add(AddonItems.ROYAL_STEEL_HAMMER.asItem(),"Royal Steel Hammer");
        add(AddonItems.LIGHTNING_HAMMER.asItem(),"Curse Gold-Royal Steel Hammer");
        add(AddonItems.STAR_OF_THE_SEA.asItem(),"Star of the Sea");
        add(AddonItems.CONDUIT_STAFF.asItem(),"Conduit Staff");
        add(AddonItems.ORIGINAL_CONDUIT_STAFF.asItem(),"Original Conduit Staff");
        add(AddonItems.SONIC_BOOM_STAFF.asItem(),"Sonic Boom Staff");
        add(AddonItems.GEM_STAFF.asItem(),"Gem Staff");
        add(AddonItems.ELECTROMAGNETIC_CROSSBOW.asItem(),"Electromagnetic Crossbow");
    }
    private void blockName(){
        add(AddonBlocks.STABLE_PRISMARINE_BRICKS.get(),"Stable Prismarine Bricks");
        add(AddonBlocks.ORIGINAL_CONDUIT.get(),"Originalization Conduit");
        add(AddonBlocks.SMART_POWER_CONVERTER.get(),"Smart Power Converter");
        add(AddonBlocks.SMART_POWER_CONVERTER_EXTREMELY_BIG.get(),"Smart Power Converter Extremely Big");
        add(AddonBlocks.OVERLOAD_GENERATOR.get(),"Overload Generator");
        add(AddonBlocks.TOFU_ANVIL.get(),"Tofu Anvil");
    }
    private void tooltipLang(){
        add("tooltip.anvilcraft_tofus_thinking.auto_can_storage","The nutritional value of storage: %s/%s");
        add("tooltip.anvilcraft_tofus_thinking.auto_can1","Right-click the item on food to absorb its nutritional value");
        add("tooltip.anvilcraft_tofus_thinking.auto_can2","which will replenish the energy of the holder when carried");
        add("tooltip.anvilcraft_tofus_thinking.curse_snowball","Cause the target to be cursed and haunted");
        add("tooltip.anvilcraft_tofus_thinking.wither_immune","Immune to Wither");

        add("tooltip.anvilcraft_tofus_thing.amethyst_golden_ring1", "+1 Fortune Level +1 Looting Level");
        add("tooltip.anvilcraft_tofus_thing.amethyst_golden_ring2", "Piglins thinks you wear a gold thing");

        add("tooltip.anvilcraft_tofus_thinking.speed_charm","Immune to Slowness");

        add("tooltip.anvilcraft_tofus_thinking.hammer_mite_undead","Deal an additional 50% damage to undead creatures");
        add("tooltip.anvilcraft_tofus_thinking.hammer_interrupt_use","Interrupting the target's use of an item");
        add("tooltip.anvilcraft_tofus_thinking.lightning_hammer","Release lightning at the target when the attack progress is full");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea","Used at the right time, it can backfire on the attacker. \nIt can also be used to absorb certain magic or the power of time");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_none","Empty");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_sonic_boom","Sonic Boom");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_effect_sonic_boom","When full progress, right-click to activate the Sonic Boom Staff in the inventory");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_rewind","Rewind");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_effect_rewind","When full progress, right-click to original the Conduit in the inventory");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_rewind_remain","Rewind Remain");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_effect_rewind_remain","right-click to original the Conduit in the inventory again");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_lost_in_time","Lost In Time");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_effect_lost_in_time","Injecting it into a prepared Wither causes it to mutate");

        add("tooltip.anvilcraft_tofus_thinking.smart_power_converter"," Adjustable Power converter, with a maximum of %s kW");

        add("tooltip.anvilcraft_tofus_thinking.tofu_anvil_ignore_conflict",",conflicts between enchantments can also be ignored");
        add("tooltip.anvilcraft_tofus_thinking.tofu_anvil_use","Randomly select the four sides of the anvil to open during use, and if it fails, open itself");
        add("tooltip.anvilcraft_tofus_thinking.tofu_anvil_anvil","Ignore the repair cost of the item%s");
        add("tooltip.anvilcraft_tofus_thinking.tofu_anvil_fall","When the upper magnet demagnetizes, the phantom of randomly selected four sided anvil falls down");

        add("tooltip.anvilcraft_tofus_thinking.original_conduit","Another mutated power of Wither can restore some things to their original state");
        add("tooltip.anvilcraft_tofus_thinking.original_conduit_build", """
                It can be activated as long as there is water and eight frame blocks within a 3x3 range on this layer
                When there are more than 8 Stable Prismarine Bricks
                the effect will be triggered regardless of whether the surrounding living entity are in the rain or water
                Attack nearby enemy monsters when there are no fewer than 24 frame blocks\
                """);
        add("tooltip.anvilcraft_tofus_thinking.original_conduit_warn","Don't let it be influenced by another kind of time power");
        add("tooltip.anvilcraft_tofus_thinking.overload_generator", "The dangerous generator that utilizes the power of abnormal time acceleration. \nFor details, please refer to JEI");
        add("jei.anvilcraft_tofus_thinking.info.overload_generator", """
                When exposed to time acceleration (positioned above an activated Corruption Beacon), the number of overload times increases by one per second
                The power generation is 2 raised to the power of (8 * n), where n represents the number of overload times, with a maximum of 4
                Explode when overloaded more than 14 times or when the corrupted beacon below is not activated
                In other cases, removing oneself will not cause an explosion\
                """);
        add("tooltip.anvilcraft_tofus_thinking.right_switch_in_inventory","Right-click in the inventory to toggle whether it is in %s");
        add("tooltip.anvilcraft_tofus_thinking.auto_hunting_mode","Auto Attack Mode");
        add("tooltip.anvilcraft_tofus_thinking.conduit_staff_auto","it automatically deals damage to nearby monsters when holding");
        add("tooltip.anvilcraft_tofus_thinking.conduit_staff_normal","Deal damage to nearby creatures pointed by the crosshair");
        add("tooltip.anvilcraft_tofus_thinking.conduit_staff_recovery","Restore energy slowly when in the rain or water");

        add("tooltip.anvilcraft.anvilcraft_tofus_thinking.original_conduit_staff","Advanced %s");
        add("tooltip.anvilcraft.anvilcraft_tofus_thinking.original_conduit_staff_more_info",".Can use some blocks to right-click on the item in the inventory to change its energy, and hold [Ctrl] to view available items");
        add("tooltip.anvilcraft.anvilcraft_tofus_thinking.original_conduit_staff_available_head","Available Glass Items:");
        add("tooltip.anvilcraft.anvilcraft_tofus_thinking.original_conduit_staff_available_grip","Available Grip Items:");

        add("tooltip.anvilcraft_tofus_thinking.ability_none","No Effect");
        add("tooltip.anvilcraft_tofus_thinking.ability_tinned_glass","Weaken and cover the target");
        add("tooltip.anvilcraft_tofus_thinking.ability_royal_glass","Reduce energy consumption by 80%");
        add("tooltip.anvilcraft_tofus_thinking.ability_frost_glass","Refining ordinary mob with less than 200 HP into exp gems");
        add("tooltip.anvilcraft_tofus_thinking.ability_ember_glass","Summon a meteor above the target point");
        add("tooltip.anvilcraft_tofus_thinking.ability_curse_gold_block","Causes weakness, slowness, hunger, curse to the target, and clears positive effects");
        add("tooltip.anvilcraft_tofus_thinking.ability_royal_steel_block","If there are no creatures at the target point, try automatically selecting nearby monsters as the center");
        add("tooltip.anvilcraft_tofus_thinking.ability_frost_metal_block","Clear flames and freeze while in the inventory");
        add("tooltip.anvilcraft_tofus_thinking.ability_ember_metal_block","Obtain fire resistance while in the inventory, and the holder can automatically recover energy in hot dimensions or flames");

        add("tooltip.anvilcraft_tofus_thinking.gem_staff_none","No valid data available");
        add("tooltip.anvilcraft_tofus_thinking.gem_staff_amethyst","Amethysts find their path");
        add("tooltip.anvilcraft_tofus_thinking.gem_staff_shift_amethyst","The projectile will track nearby targets");
        add("tooltip.anvilcraft_tofus_thinking.gem_staff_topaz","Topazes burst brightly");
        add("tooltip.anvilcraft_tofus_thinking.gem_staff_shift_topaz","The projectile explodes when it disappears, causing half the damage to itself");
        add("tooltip.anvilcraft_tofus_thinking.gem_staff_sapphire","Sapphires swirl in pairs");
        add("tooltip.anvilcraft_tofus_thinking.gem_staff_shift_sapphire","Damage * 0.6, generate an additional projectile,and freeze target");
        add("tooltip.anvilcraft_tofus_thinking.gem_staff_emerald","Emeralds light the night");
        add("tooltip.anvilcraft_tofus_thinking.gem_staff_shift_emerald","The projectile has twice the speed,+1 Pierce, and remains when it disappears due to hitting a block");
        add("tooltip.anvilcraft_tofus_thinking.gem_staff_ruby","Rubies flash double");
        add("tooltip.anvilcraft_tofus_thinking.gem_staff_shift_ruby","Damage * 0.6, each hit produces an additional projectile");
        add("tooltip.anvilcraft_tofus_thinking.gem_staff_diamond","Diamonds shine brilliantly");
        add("tooltip.anvilcraft_tofus_thinking.gem_staff_shift_diamond","+1 Pierce and increasing the physical collision volume of the projectile");
        add("tooltip.anvilcraft_tofus_thinking.gem_staff_amber","Ambers carve a path");
        add("tooltip.anvilcraft_tofus_thinking.gem_staff_shift_amber","+2 Pierce, the projectile can rebound on a solid block, consuming 1 Pierce per rebound");
        add("tooltip.anvilcraft_tofus_thinking.gem_staff_same","Increase damage by 20% with the same material");
        add("tooltip.anvilcraft_tofus_thinking.gem_staff_change_type","Right click on amethyst, topaz, sapphire, emerald, ruby, diamond, amber, or their blocks in the inventory to change the type");

        add("tooltip.anvilcraft_tofus_thinking.crossbow_left_count","Remaining shooting times： %s");
        add("tooltip.anvilcraft_tofus_thinking.crossbow","Arrows, fireworks, metal particles, gems, or snowballs can be used as ammunition");
        add("tooltip.anvilcraft_tofus_thinking.crossbow_take_out","Right click on the inventory to retrieve ammunition or clear unused ammunition");
        add("tooltip.anvilcraft_tofus_thinking.mass","Mass： %s");
        add("tooltip.anvilcraft_tofus_thinking.damage_scale","Damage Scale： %s");
        add("tooltip.anvilcraft_tofus_thinking.crossbow_curse","Apply Weakness II(00:10) to the Target");
        add("tooltip.anvilcraft_tofus_thinking.crossbow_radiation","Apply Wither II(00:10) to the Target，Damage increases as target armor coverage decreases");
        add("tooltip.anvilcraft_tofus_thinking.crossbow_ember","Extra target ignition time of 10 seconds");
        add("tooltip.anvilcraft_tofus_thinking.crossbow_sliver","Increases damage to undead mob by 50%");
        add("tooltip.anvilcraft_tofus_thinking.crossbow_lightning","Release nine lightning bolts at the hit point");
        add("tooltip.anvilcraft_tofus_thinking.crossbow_freeze","Deal Slowness III (00:15) to mob within the Eight Grids and inflict some frost damage");
        add("tooltip.anvilcraft_tofus_thinking.crossbow_burn","Ignite the mob within the eight squares and damage their equipment, causing certain flame damage");

        add("tooltip.anvilcraft_tofus_thinking.need_energy","Consume %s when use");
        add("tooltip.anvilcraft_tofus_thinking.not_active","Not Active");
        add("tooltip.anvilcraft_tofus_thinking.progress","Progress: %s %%");
        add("tooltip.anvilcraft_tofus_thinking.hold_shift_for_more","Hold  %s  for more info");
        add("tooltip.anvilcraft_tofus_thinking.permanent", """
                Permanent: Indestructible to the world's assaults
                Wandering idly above the hollow Void
                Everlasting within the crevice of Time\
                """);

    }
    private void entityName(){
        add(AddonEntities.CURSE_SNOWBALL.get(),"Curse Snowball");
        add(AddonEntities.STRANGE_WITHER.get(),"Strange Wither");
        add(AddonEntities.STRANGE_WITHER_SKULL.get(),"Strange Wither Skull");
        add(AddonEntities.METEOR.get(),"Meteor");
        add(AddonEntities.GEM_MISSILE.get(),"Gem Missile");
        add(AddonEntities.ELECTROMAGNETIC_ARROW.get(),"Arrow");
        add(AddonEntities.ELECTROMAGNETIC_PROJECTILE.get(),"Electromagnetic Projectile");
    }
    private void addConfig(){
        ConfigData.readConfigClass(this, AnvilCraftTofusThinkCommonConfig.class);
        ConfigData.readConfigClass(this, AnvilCraftTofusThinkServerConfig.class);
    }
    private void addOther(){

        add(AddonMobEffects.CURSE.get().getDescriptionId(),"Curse");
        add(AddonMobEffects.SHRINK.get().getDescriptionId(),"Shrink");
        add(AddonMobEffects.DULL.get().getDescriptionId(),"Dull");
        add(AddonMobEffects.COVER.get().getDescriptionId(),"Cover");
        add(AddonMobEffects.TEMPERATURE_TOLERANCE.get().getDescriptionId(),"Temperature Tolerance");

        add("death.attack.tofusThinking.rewind","%s has never been born");
        add("death.attack.tofusThinking.rewind_attack","%s has not been proven to exist by %s");
        add("death.attack.tofusThinking.bounce_wither_skull","%s was killed by the wither skull %s had launch");
        add("death.attack.tofusThinking.counter","%s impulsively attacked %s");
        add("death.attack.tofusThinking.gem_missile","%s didn't get a clear look to gem by %s");

        add(AddonItemGroups.ITEM_TAB_ID,"AnvilCraft: Tofu's Thinking");

        add("gui.anvilcraft_tofus_thinking.category.rewind","Rewind");
        add("gui.anvilcraft_tofus_thinking.category.rewind.need_activated","Need Activated");

        add("jei.anvilcraft_tofus_thinking.info.original_conuit","By default, the Star Of The Sea is used to absorb the blue Wither Head of a strange Wither and inject it into an Conduit to obtain it. In its original state, it is difficult to control precisely and is not suitable for direct use as a wand material.");
        add("jei.anvilcraft_tofus_thinking.info.charm_amulet","By default, it is obtained by stamping with six different charms on an anvil");

        add("enhancedtooltips.rarity.anvilcraft_tofus_thinking_tofu","Tofu's Thinking");
    }
}
