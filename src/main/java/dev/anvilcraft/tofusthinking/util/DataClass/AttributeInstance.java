package dev.anvilcraft.tofusthinking.util.DataClass;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record AttributeInstance(Holder<Attribute> attribute, double value, AttributeModifier.Operation operation) {
    public AttributeModifier createModifier() {
        String name = attribute.unwrapKey().map(attribute -> attribute.location().toString()).orElse("unknown");
        var attributeName = ResourceLocation.parse(name).getPath();
        return new AttributeModifier(AnvilCraftTofusThinking.of(String.format("%s_modifier", attributeName)), value, operation);
    }
}
