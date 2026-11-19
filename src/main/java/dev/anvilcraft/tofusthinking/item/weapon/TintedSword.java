package dev.anvilcraft.tofusthinking.item.weapon;

import dev.dubhe.anvilcraft.item.ModTiers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class TintedSword extends SwordItem {
    public TintedSword(Properties properties) {
        super(ModTiers.AMETHYST, properties.attributes(createAttributes()));
    }

    @Override
    public boolean onLeftClickEntity(@NonNull ItemStack stack, @NonNull Player player, @NonNull Entity entity) {
        if(player.level() instanceof ServerLevel level){
            if(entity instanceof LivingEntity living && living instanceof Enemy){
                if(living.getHealth() <= living.getMaxHealth() * 0.9F && player.getRandom().nextBoolean()){return false;}
            }
            float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float scale = player.getAttackStrengthScale(0.5F);
            damage *= scale * scale;
            DamageSource source = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK), player.position());
            if(entity.hurt(source, damage)){
                stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            }
        }
        player.resetAttackStrengthTicker();
        return true;
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 7, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -3, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltips, @NotNull TooltipFlag tooltipFlag) {
        tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thing.tinted_sword1").withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.ITALIC));
        tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thing.tinted_sword2").withStyle(ChatFormatting.GRAY));
        tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thing.tinted_sword3").withStyle(ChatFormatting.GRAY));
    }
}
