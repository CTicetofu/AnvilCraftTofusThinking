package dev.anvilcraft.tofusthinking.item.blockItem;

import dev.anvilcraft.tofusthinking.block.entity.MushroomCowInJarBlockEntity;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.EffectCures;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class MushroomCowInJarItem extends SimpleBlockItem {
    public MushroomCowInJarItem(Block block, Properties properties) {
        super(block, properties.component(AddonComponents.TYPE_NUMBER,(byte)0));
    }

    @Override
    public boolean isEnderMask(@NonNull ItemStack stack, @NonNull Player player, @NonNull EnderMan enderManEntity) {
        return true;
    }

    @Override
    public @org.jetbrains.annotations.Nullable EquipmentSlot getEquipmentSlot(@NonNull ItemStack stack) {
        return EquipmentSlot.HEAD;
    }

    @Override
    public boolean supportsEnchantment(@NonNull ItemStack stack, @NonNull Holder<Enchantment> enchantment) {
        return !enchantment.is(EnchantmentTags.CURSE);
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
    public void inventoryTick(@NonNull ItemStack stack, @NonNull Level level, @NonNull Entity entity, int slotId, boolean isSelected) {
        if(slotId == 39 && !level.isClientSide && entity.tickCount % 5 == 0 && entity instanceof LivingEntity living && living.getItemBySlot(EquipmentSlot.HEAD) == stack){
            living.removeEffectsCuredBy(EffectCures.MILK);
            if(living instanceof Player player){
                if(player.getFoodData().needsFood()){
                    Items.MUSHROOM_STEW.finishUsingItem(Items.MUSHROOM_STEW.getDefaultInstance(),level,player);
                }
            }
        }
    }

    @Override
    protected boolean updateCustomBlockEntityTag(@NonNull BlockPos pos, @NonNull Level level, @Nullable Player player, @NonNull ItemStack stack, @NonNull BlockState state) {
        if(stack.getOrDefault(AddonComponents.TYPE_NUMBER,(byte)0) != 0){
            if(level.getBlockEntity(pos) instanceof MushroomCowInJarBlockEntity blockEntity){
                blockEntity.changeAndUpdate(true);
            }
        }
        return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> components, @NotNull TooltipFlag tooltipFlag) {
        components.add(Component.translatable("tooltip.anvilcraft_tofus_thing.mushroom_cow_in_jar1").withStyle(ChatFormatting.GRAY));
        components.add(Component.translatable("tooltip.anvilcraft_tofus_thing.mushroom_cow_in_jar2").withStyle(ChatFormatting.GRAY));
    }
}
