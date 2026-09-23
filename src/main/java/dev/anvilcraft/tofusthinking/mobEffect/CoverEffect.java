package dev.anvilcraft.tofusthinking.mobEffect;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class CoverEffect extends AddonMobEffect implements NotApplyEffect{
    public CoverEffect() {
        super(MobEffectCategory.HARMFUL, 0x696969);
        this.addAttributeModifier(Attributes.FOLLOW_RANGE, AnvilCraftTofusThinking.of("cover"),-0.5F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    public static double half(double amount, int amplifier) {
        return Math.pow(1 + amount, amplifier + 1) - 1;
    }

    @Override
    public @NotNull MobEffect addAttributeModifier(@NotNull Holder<Attribute> attribute, @NotNull ResourceLocation id, double amount, AttributeModifier.@NotNull Operation operation) {
        return super.addAttributeModifier(attribute, id, operation,lv -> half(amount, lv));
    }

    @Override
    public boolean isNotApply(MobEffectInstance instance, LivingEntity entity) {
        return entity instanceof Player;
    }
}
