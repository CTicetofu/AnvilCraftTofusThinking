package dev.anvilcraft.tofusthinking.data.tags;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumTagsProvider;
import dev.anvilcraft.tofusthinking.init.entity.AddonEntities;
import dev.anvilcraft.tofusthinking.init.entity.AddonEntityTypeTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags;

public class AddonEntityTypeTagLoader {
    public static void init(RegistrumTagsProvider<EntityType<?>> provider) {
        provider.addTag(EntityTypeTags.WITHER_FRIENDS)
                .addOptional(AddonEntities.STRANGE_WITHER.getId());

        provider.addTag(EntityTypeTags.UNDEAD)
                .addOptional(AddonEntities.STRANGE_WITHER.getId());

        //伤害测试生物，因为它们可能是可以修改属性的，防止依赖某些属性的计算造成极大效果
        provider.addTag(AddonEntityTypeTags.DAMAGE_TEST_ENTITY)
                .addOptional(ResourceLocation.fromNamespaceAndPath("dummmmmmy","target_dummy"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("powerful_dummy","test_dummy"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("powerful_dummy","test_dummy_undead"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("powerful_dummy","test_dummy_arthropod"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("powerful_dummy","test_dummy_water"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("powerful_dummy","test_dummy_illager"));

        //不会被原初潮涌核心按原始血量攻击的目标，防止某些情况下过于超模
        provider.addTag(AddonEntityTypeTags.NOT_ORIGINAL_ATTACK_ENTITY)
                .addTag(AddonEntityTypeTags.DAMAGE_TEST_ENTITY)
                .addTag(Tags.EntityTypes.BOSSES);

        //不会被原初潮涌核心无视无敌帧攻击的目标，防止放多了直接杀死一些生物
        provider.addTag(AddonEntityTypeTags.NOT_MUTI_ATTACK_ENTITY)
                .addTag(Tags.EntityTypes.BOSSES);
    }
}
