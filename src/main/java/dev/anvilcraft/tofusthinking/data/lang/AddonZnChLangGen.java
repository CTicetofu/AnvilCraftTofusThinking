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
        add(AddonBlocks.OVERLOAD_GENERATOR.get(),"过载发电机");
    }
    private void tooltipLang(){
        add("tooltip.anvilcraft_tofus_thinking.auto_can_storage","存储的营养价值: %1$d/%2$d");
        add("tooltip.anvilcraft_tofus_thinking.auto_can","使用该物品右击食物以吸收营养价值 \n携带时为持有者补充能量");
        add("tooltip.anvilcraft_tofus_thinking.curse_snowball","使目标被诅咒缠身");
        add("tooltip.anvilcraft_tofus_thinking.wither_immune","免疫凋灵破坏");
        add("tooltip.anvilcraft_tofus_thinking.hammer_mite_undead","对亡灵生物额外造成50%伤害");
        add("tooltip.anvilcraft_tofus_thinking.hammer_interrupt_use","打断目标使用物品的状态");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea","在合适的时机举起可以反伤攻击者 \n也可以借此吸收某些魔法或者时间的力量");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_none","空");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_sonic_boom","音爆");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_effect_sonic_boom","进度为满时在物品栏界面可右键激活音爆法杖");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_rewind","回溯");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_effect_rewind","进度为满时在物品栏界面可点击潮涌核心将其原初化");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_rewind_remain","回溯之残留");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_effect_rewind_remain","点击潮涌核心法杖将其原初化");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_lost_in_time","迷失时间");
        add("tooltip.anvilcraft_tofus_thinking.star_of_the_sea_type_effect_lost_in_time","注入到准备状态的凋灵使其变异");
        add("tooltip.anvilcraft_tofus_thinking.original_conduit","另一种异变的凋灵之力，可以将一些事物还原成本来的样子");
        add("tooltip.anvilcraft_tofus_thinking.original_conduit_build", """
                只需要本层3*3范围有水和八个框架方块即可启动
                当有大于8个坚固海晶石砖时，无论周围生物是否在雨中或者水中都会造成效果
                框架方块不小于24个时攻击附近的敌怪\
                """);
        add("tooltip.anvilcraft_tofus_thinking.original_conduit_warn","不要让它受到另一种时间之力的影响");
        add("tooltip.anvilcraft_tofus_thinking.overload_generator", "使用异常时间加速之力的危险发电机，详情查看jei");
        add("jei.anvilcraft_tofus_thinking.info.overload_generator", """
                当受到时间加速（置于激活的腐化信标上方时）每秒增加一点过载次数
                发电量为 2 ^ (8 * n) n为过载次数，且最大为4
                当过载超过14次或者过载时下方不为激活的腐化信标时爆炸
                其他情况下移除自身不会产生爆炸\
                """);
        add("tooltip.anvilcraft_tofus_thinking.right_switch_in_inventory","在物品栏界面右键以改变是否为 %s");
        add("tooltip.anvilcraft_tofus_thinking.auto_hunting_mode","自动攻击模式");
        add("tooltip.anvilcraft_tofus_thinking.conduit_staff_auto","持有时对自动对周围怪物造成伤害");
        add("tooltip.anvilcraft_tofus_thinking.conduit_staff_normal","对准星所指位置附近生物造成伤害");
        add("tooltip.anvilcraft_tofus_thinking.conduit_staff_recovery","在雨中或者水中时缓慢恢复能量");

        add("tooltip.anvilcraft_tofus_thinking.need_energy","使用时消耗 %s");
        add("tooltip.anvilcraft_tofus_thinking.not_active","未激活");
        add("tooltip.anvilcraft_tofus_thinking.progress","进度: %s %%");
    }
    private void entityName(){
        add(AddonEntities.CURSE_SNOWBALL.get(),"诅咒雪球");
        add(AddonEntities.STRANGE_WITHER.get(),"奇怪的凋灵");
        add(AddonEntities.STRANGE_WITHER_SKULL.get(),"奇怪的凋灵之首");
    }

    private void addOther(){
        add(AddonFluids.NUTRIENT_LIQUID_TYPE.get().getDescriptionId(),"营养液");

        add(AddonMobEffects.CURSE.get().getDescriptionId(),"诅咒");
        add(AddonMobEffects.SHRINK.get().getDescriptionId(),"收缩");
        add(AddonMobEffects.DULL.get().getDescriptionId(),"呆滞");

        add("death.attack.tofusThinking.rewind","%s 从未诞生过");
        add("death.attack.tofusThinking.rewind_attack","%s 未能被 %s 证明存在");
        add("death.attack.tofusThinking.tofusThinking.bounce_wither_skull","%s 被 %s 发射的弹射凋灵骷髅头击杀");
        add("death.attack.tofusThinking.counter","%s 过于冲动的攻击了 %s");

        add(AddonItemGroups.ITEM_TAB_ID,"铁砧工艺: 豆之巧思");

        add("gui.anvilcraft_tofus_thinking.category.rewind","回溯");
        add("gui.anvilcraft_tofus_thinking.category.rewind.need_activated","需要激活");

        add("jei.anvilcraft_tofus_thinking.info.original_conuit","默认情况下，使用海洋之星吸收奇怪的凋灵的蓝色凋灵之首后将其注入普通的潮涌核心获取，原始状态的它难以精细控制而不太适合直接作为法杖材料。");
        add("jei.anvilcraft_tofus_thinking.info.charm_amulet","默认情况下，使用六个不同的护符经过铁砧冲压获得");
    }
}
