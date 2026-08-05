package dev.anvilcraft.tofusthinking.init;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.mobEffect.AddonMobEffect;
import dev.anvilcraft.tofusthinking.mobEffect.DullEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AddonMobEffects {
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, AnvilCraftTofusThinking.MOD_ID);

    public static final DeferredHolder<MobEffect, MobEffect> CURSE = EFFECTS.register("curse",
            () -> new AddonMobEffect(MobEffectCategory.HARMFUL,0x363636));

    public static final DeferredHolder<MobEffect, MobEffect> SHRINK = EFFECTS.register("shrink",
            () -> new AddonMobEffect(MobEffectCategory.NEUTRAL,0xFFF68F)
                    .addAttributeModifier(Attributes.SCALE,AnvilCraftTofusThinking.of("shrink"),-0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.MAX_HEALTH,AnvilCraftTofusThinking.of("shrink"),-0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );
    public static final DeferredHolder<MobEffect, MobEffect> DULL = EFFECTS.register("dull", DullEffect::new);

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}
