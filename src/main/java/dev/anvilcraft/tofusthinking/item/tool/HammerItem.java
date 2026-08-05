package dev.anvilcraft.tofusthinking.item.tool;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.init.AddonMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HammerItem extends TieredItem {
    public static ResourceLocation KNOCKBACK_ID = AnvilCraftTofusThinking.of("knockback");
    public static ResourceLocation SWEEP_ID = AnvilCraftTofusThinking.of("sweep");
    public HammerItem(Tier tier, Properties properties) {
        super(tier,properties.component(DataComponents.TOOL,createToolProperties(tier)));
    }

    @Override
    public boolean supportsEnchantment(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
        return super.supportsEnchantment(stack, enchantment) || enchantment.getKey() == Enchantments.BREACH || enchantment.getKey() == Enchantments.LOOTING;
    }

    public static Tool createToolProperties(Tier tier) {
        List<Tool.Rule> rules = new ArrayList<>();
        rules.add(Tool.Rule.deniesDrops(tier.getIncorrectBlocksForDrops()));
        rules.add(Tool.Rule.overrideSpeed(tier.getIncorrectBlocksForDrops(),Math.max(tier.getSpeed() * 1.5F,1)));
        return new Tool(List.copyOf(rules), tier.getSpeed(), 1);
    }

    @Override
    public boolean isCorrectToolForDrops(@NotNull ItemStack stack, @NotNull BlockState state) {
        return !state.is(getTier().getIncorrectBlocksForDrops());
    }

    @Override
    public boolean canDisableShield(@NotNull ItemStack stack, @NotNull ItemStack shield, @NotNull LivingEntity entity, @NotNull LivingEntity attacker) {
        return true;
    }

    @Override
    public @NotNull AABB getSweepHitBox(@NotNull ItemStack stack, @NotNull Player player, Entity target) {
        return target.getBoundingBox().inflate(1.5D, 0.5D, 1.5D);
    }

    public static ItemAttributeModifiers createAttributes(Tier tier, float attackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_KNOCKBACK,
                        new AttributeModifier(KNOCKBACK_ID,1, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    @Override
    public float getAttackDamageBonus(@NotNull Entity target, float damage, @NotNull DamageSource damageSource) {
        float addDamage = 0;
        if(target instanceof LivingEntity living && damageSource.getDirectEntity() instanceof Player player){
            addDamage += (float)(living.getArmorValue() * 0.5F + living.getAttributeValue(Attributes.ARMOR_TOUGHNESS) * 0.5F) * player.getAttackStrengthScale(0.5F);
        }
        if(target.getType().is(EntityTypeTags.UNDEAD)){
            addDamage += damage * 0.5F;
        }
        return super.getAttackDamageBonus(target, damage, damageSource) + addDamage;
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        boolean interrupt = true;
        if(attacker instanceof Player player){
            interrupt = player.getAttackStrengthScale(0.5F) > 0.7;
        }
        if(interrupt){
            target.stopUsingItem();
            if(target instanceof Player player){
                if(target.isUsingItem()){player.getCooldowns().addCooldown(player.getUseItem().getItem(),30);}
            } else {
                target.addEffect(new MobEffectInstance(AddonMobEffects.DULL.getDelegate(),20));
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.hammer_mite_undead").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.hammer_interrupt_use").withStyle(ChatFormatting.GRAY));
    }
}
