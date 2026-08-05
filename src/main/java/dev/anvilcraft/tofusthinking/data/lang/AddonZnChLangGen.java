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

public class AddonZnChLangGen extends LanguageProvider {
    public AddonZnChLangGen(PackOutput output) {
        super(output, AnvilCraftTofusThinking.MOD_ID, "zh_cn");
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
        add(AddonItems.AUTO_CAN.asItem(),"自动罐头");
        add(AddonItems.CHARM_AMULET.asItem(),"护符护符");
        add(AddonItems.CURSE_SNOWBALL_ITEM.asItem(),"诅咒雪球");
        add(AddonItems.AMETHYST_HAMMER.asItem(),"紫水晶锤");
        add(AddonItems.ROYAL_STEEL_HAMMER.asItem(),"皇家钢锤");
        add(AddonItems.NUTRIENT_LIQUID_BUCKET.asItem(),"营养液桶");
        add(AddonItems.STAR_OF_THE_SEA.asItem(),"海洋之星");
        add(AddonItems.CONDUIT_STAFF.asItem(),"潮涌核心法杖");
        add(AddonItems.SONIC_BOOM_STAFF.asItem(),"音爆法杖");
    }
    private void blockName(){
        add(AddonBlocks.STABLE_PRISMARINE_BRICKS.get(),"坚固海晶石砖");
        add(AddonBlocks.ORIGINAL_CONDUIT.get(),"原初化潮涌核心");
        add(AddonBlocks.NUTRIENT_EXTRACTOR.get(),"营养萃取器");
        add(AddonBlocks.SMART_POWER_CONVERTER.get(),"智能能量转换器");
    }
    private void tooltipLang(){
        add("tooltip.anvilcraft_tofus_thinking.auto_can_storage","存储的营养价值: %1$d/%2$d");
        add("tooltip.anvilcraft_tofus_thinking.auto_can","使用该物品右击食物以吸收营养价值 \n携带时为持有者补充能量");
        add("tooltip.anvilcraft_tofus_thinking.curse_snowball","使目标被诅咒缠身");
        add("tooltip.anvilcraft_tofus_thinking.wither_immune","免疫凋灵破坏");
        add("tooltip.anvilcraft_tofus_thinking.hammer_mite_undead","对亡灵生物额外造成50%伤害");
        add("tooltip.anvilcraft_tofus_thinking.hammer_interrupt_use","打断目标使用物品的状态");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea","在合适的时机举起可以反伤攻击者");

        add("tooltip.anvilcraft_tofus_thinking.right_switch_in_inventory","在物品栏界面右键以改变是否为 %s");
        add("tooltip.anvilcraft_tofus_thinking.auto_hunting_mode","自动攻击模式");
        add("tooltip.anvilcraft_tofus_thinking.conduit_staff_auto","持有时对自动对周围怪物造成伤害");
        add("tooltip.anvilcraft_tofus_thinking.conduit_staff_normal","对准星所指位置附近生物造成伤害");
        add("tooltip.anvilcraft_tofus_thinking.conduit_staff_recovery","在雨中或者水中时缓慢恢复能量");

        add("tooltip.anvilcraft_tofus_thinking.need_energy","使用时消耗 %s");
        add("tooltip.anvilcraft_tofus_thinking.not_active","未激活");
    }
    private void entityName(){
        add(AddonEntities.CURSE_SNOWBALL.get(),"诅咒雪球");
    }

    private void addOther(){
        add(AddonFluids.NUTRIENT_LIQUID_TYPE.get().getDescriptionId(),"营养液");

        add(AddonMobEffects.CURSE.get().getDescriptionId(),"诅咒");
        add(AddonMobEffects.SHRINK.get().getDescriptionId(),"收缩");
        add(AddonMobEffects.DULL.get().getDescriptionId(),"呆滞");

        add("death.attack.tofusThinking.rewind","%s 从未诞生过");
        add("death.attack.tofusThinking.counter","%s 过于冲动的攻击了 %s");

        add(AddonItemGroups.ITEM_TAB_ID,"铁砧工艺: 豆之巧思");
    }
}
