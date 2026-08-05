package dev.anvilcraft.tofusthinking.item.curio;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.anvilcraft.tofusthinking.util.DataClass.AttributeInstance;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;

public class CurioBaseItem extends Item implements ICurioItem {
    public CurioBaseItem(Properties properties) {
        super(properties);
    }
    protected Multimap<Holder<Attribute>, AttributeModifier> modifierMultimap = null;
    protected boolean canRepeatEquip = false;

    public boolean isEquippedBy(@Nullable LivingEntity entity) {
        return entity != null && CuriosApi.getCuriosInventory(entity).map(inv -> inv.findFirstCurio(this).isPresent()).orElse(false);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return canRepeatEquip || !this.isEquippedBy(slotContext.entity());
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        return modifierMultimap == null ? ICurioItem.super.getAttributeModifiers(slotContext, id, stack) : modifierMultimap;
    }

    public CurioBaseItem withAttribute(AttributeInstance... instances){
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
        for (AttributeInstance instance : instances){
            builder.put(instance.attribute(),instance.createModifier());
        }
        modifierMultimap = builder.build();
        return this;
    }

    public CurioBaseItem setCanRepeatEquip(boolean b){
        canRepeatEquip = b;
        return this;
    }
}
