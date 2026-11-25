package dev.anvilcraft.tofusthinking.item.curio;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.dubhe.anvilcraft.init.ModAttributes;
import dev.dubhe.anvilcraft.inventory.PocketInventory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import top.theillusivec4.curios.api.SlotAttribute;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.List;

public class PocketPocket extends CurioBaseItem{
    public static final ResourceLocation POCKET_ADD = AnvilCraftTofusThinking.of("pocket_add");
    public PocketPocket(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        if(slotContext.entity() instanceof Player player){
            AttributeInstance instance = player.getAttribute(ModAttributes.POCKET_COUNT);
            if(instance != null && instance.hasModifier(POCKET_ADD) && !PocketInventory.get(player).isEmpty()){
                return false;
            }
        }
        return super.canUnequip(slotContext, stack);
    }

    @Override
    public  ICurio.@NonNull DropRule getDropRule(SlotContext slotContext, DamageSource source, boolean recentlyHit, ItemStack stack) {
        return ICurio.DropRule.ALWAYS_KEEP;
    }

    @Override
    public boolean isBookEnchantable(@NonNull ItemStack stack, ItemStack book) {
        ItemEnchantments enchants = book.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Holder<Enchantment> enchantment : enchants.keySet()) {
            if (enchantment.is(EnchantmentTags.CURSE)){return false;}
        }
        return true;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(ModAttributes.POCKET_COUNT, new AttributeModifier(POCKET_ADD, 6, AttributeModifier.Operation.ADD_VALUE));
        builder.put(SlotAttribute.getOrCreate("belt"), new AttributeModifier(POCKET_ADD, 1, AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thing.pocket_pocket").withStyle(ChatFormatting.GRAY));
    }
}
