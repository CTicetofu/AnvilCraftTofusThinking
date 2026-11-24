package dev.anvilcraft.tofusthinking.item.tool;

import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.item.OnEnderChestInventoryTick;
import dev.anvilcraft.tofusthinking.util.ItemUtil;
import dev.anvilcraft.tofusthinking.util.TooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

public class PotionJarItem extends Item implements OnEnderChestInventoryTick {
    public static final int MAX_PROGRESS = 32000;
    public static final int SINGLE_CONSUME = 50;
    public PotionJarItem(Properties properties) {
        super(properties.component(AddonComponents.IS_ACTIVE, false));
    }

    @Override
    public boolean overrideOtherStackedOnMe(@NonNull ItemStack stack, @NonNull ItemStack other, @NonNull Slot slot, @NonNull ClickAction action, @NonNull Player player, @NonNull SlotAccess access) {
        if(slot.allowModification(player) && action == ClickAction.SECONDARY && other.isEmpty()){
            boolean active = stack.getOrDefault(AddonComponents.IS_ACTIVE, false);
            stack.set(AddonComponents.IS_ACTIVE, !active);
            if(player.level().isClientSide){
                player.level().playLocalSound(player, SoundEvents.LEVER_CLICK,player.getSoundSource(),1,1);
            }
            return true;
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
    }

    @Override
    public void inventoryTick(@NonNull ItemStack stack, @NonNull Level level, @NonNull Entity entity, int slotId, boolean isSelected) {
        if(entity instanceof Player player){onTick(stack, level, player);}
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    public static void onTick(ItemStack stack, Level level, Player player){
        if(!level.isClientSide && player.tickCount % 40 == 0 && stack.getOrDefault(AddonComponents.IS_ACTIVE, false)){
            int current = stack.getOrDefault(AddonComponents.PROGRESS, 0);
            if(current > 0 && current < SINGLE_CONSUME){return;}
            PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            boolean add = false;
            for (MobEffectInstance instance : contents.getAllEffects()){
                if (!instance.getEffect().value().isInstantenous() && instance.duration > 20 * 30) {
                    MobEffectInstance effect = player.getEffect(instance.getEffect());
                    if(effect == null || (effect.duration <= 20 * 20 && instance.duration < effect.duration / 10)){
                        add = player.addEffect(new MobEffectInstance(instance)) || add;
                    }
                }
            }
            if(add){tryConsume(stack);}
        }
    }

    @Override
    public boolean overrideStackedOnOther(@NonNull ItemStack stack, @NonNull Slot slot, @NonNull ClickAction action, @NonNull Player player) {
        if(slot.allowModification(player) && action == ClickAction.SECONDARY && player.inventoryMenu.getCarried() == stack){
            ItemStack other = slot.getItem();
            ItemStack copy = stack.copy();
            int count = tryFill(copy, other);
            if(count > 0){
                if(!player.hasInfiniteMaterials()){
                    other.shrink(count);
                    if(other.isEmpty()){
                        slot.set(new ItemStack(Items.GLASS_BOTTLE, count));
                    } else {
                        ItemUtil.giveEnoughItem(player, new ItemStack(Items.GLASS_BOTTLE), count);
                    }
                }
                if(player.level().isClientSide){
                    player.level().playLocalSound(player, SoundEvents.BOTTLE_FILL,player.getSoundSource(),1,1);
                }
                player.inventoryMenu.setCarried(copy);
                return true;
            }
        }
        return super.overrideStackedOnOther(stack, slot, action, player);
    }

    public @NonNull InteractionResultHolder<ItemStack> use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int current = stack.getOrDefault(AddonComponents.PROGRESS, 0);
        if(player.isShiftKeyDown() && stack.getOrDefault(AddonComponents.IS_ACTIVE, false)){
            if(current >= 0 && current <= MAX_PROGRESS - 250){
                int count = 0;
                for(ItemStack itemStack : player.getInventory().items){
                    int rangeCount = tryFill(stack, itemStack);
                    count += rangeCount;
                    if(rangeCount > 0){
                        itemStack.shrink(rangeCount);
                    }
                    int progress = stack.getOrDefault(AddonComponents.PROGRESS, 0);
                    if(progress > MAX_PROGRESS - 250){break;}
                }
                if(count > 0){ItemUtil.giveEnoughItem(player,new ItemStack(Items.GLASS_BOTTLE), count);}
            }
            player.getCooldowns().addCooldown(this, 10);
            return InteractionResultHolder.success(stack);
        }
        if(current >= 0 && current < 50){return InteractionResultHolder.fail(stack);}
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    public @NonNull ItemStack finishUsingItem(@NonNull ItemStack stack, @NonNull Level level, @NonNull LivingEntity entityLiving) {
        Player player = entityLiving instanceof Player ? (Player)entityLiving : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
        }

        if (!level.isClientSide) {
            PotionContents potioncontents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            potioncontents.forEachEffect((instance) -> {
                if (instance.getEffect().value().isInstantenous()) {
                    instance.getEffect().value().applyInstantenousEffect(player, player, entityLiving, instance.getAmplifier(), 1.0F);
                } else {
                    entityLiving.addEffect(instance);
                }
            });
        }

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if(!player.hasInfiniteMaterials()){
                tryConsume(stack);
            }
        }

        entityLiving.gameEvent(GameEvent.DRINK);
        return stack;
    }

    @Override
    public int getUseDuration(@NonNull ItemStack stack, @NonNull LivingEntity entity) {
        return 24;
    }

    @Override
    public @NonNull UseAnim getUseAnimation(@NonNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull List<Component> tooltips, @NonNull TooltipFlag tooltipFlag) {
        PotionContents potioncontents = stack.get(DataComponents.POTION_CONTENTS);
        if (potioncontents != null) {
            Objects.requireNonNull(tooltips);
            potioncontents.addPotionTooltip(tooltips::add, 1.0F, context.tickRate());
        }
        int current = stack.getOrDefault(AddonComponents.PROGRESS, 0);
        if(current < 0){
            tooltips.add(Component.literal("∞ MB").withStyle(ChatFormatting.GRAY));
        } else {
            tooltips.add(Component.literal(String.format("%d MB / %d MB", current, MAX_PROGRESS)).withStyle(ChatFormatting.GRAY));
        }

        boolean active = stack.getOrDefault(AddonComponents.IS_ACTIVE, false);
        tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.right_switch_in_inventory",Component.translatable("tooltip.anvilcraft_tofus_thinking.auto_drink_mode").withStyle(active ? ChatFormatting.AQUA : ChatFormatting.STRIKETHROUGH)).withStyle(ChatFormatting.GRAY));

        if(Screen.hasShiftDown()){
            tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.potion_jar1").withStyle(ChatFormatting.GRAY));
            tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.potion_jar2", Component.literal(SINGLE_CONSUME + "MB").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
            tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.potion_jar3").withStyle(ChatFormatting.GRAY));
            tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.potion_jar4").withStyle(ChatFormatting.GRAY));
        } else {
            tooltips.add(TooltipUtil.HOLD_SHIFT_FOR_MORE.copy());
        }
    }

    public static int tryFill(ItemStack stack, ItemStack other){
        int current = stack.getOrDefault(AddonComponents.PROGRESS, 0);
        if(current < MAX_PROGRESS && current >= 0 && other.is(Items.POTION) && other.has(DataComponents.POTION_CONTENTS)){
            PotionContents contents = other.getOrDefault(DataComponents.POTION_CONTENTS,PotionContents.EMPTY);
            if(contents.potion().isEmpty()){return 0;}
            Holder<Potion> potion = contents.potion().get();
            PotionContents self = stack.getOrDefault(DataComponents.POTION_CONTENTS,PotionContents.EMPTY);
            if(self.potion().isEmpty() || self.is(potion)){
                stack.set(DataComponents.POTION_CONTENTS, PotionContents.EMPTY.withPotion(potion));
                int max = Math.min(other.getCount(), Mth.ceil((MAX_PROGRESS - current)/250F));
                stack.set(AddonComponents.PROGRESS, current + max * 250);
                if(stack.getOrDefault(AddonComponents.PROGRESS, 0) >= MAX_PROGRESS){
                    tryMakeInfinity(stack);
                }
                return max;
            }
        }
        return 0;
    }

    public static void tryMakeInfinity(ItemStack stack){
        int current = stack.getOrDefault(AddonComponents.PROGRESS, 0);
        if(current >= MAX_PROGRESS){
            PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS,PotionContents.EMPTY);
            contents.potion().ifPresent(potionHolder -> {
                Potion potion = potionHolder.value();
                int size = potion.getEffects().size();
                if(size == 1 && !potion.hasInstantEffects() && potion.getEffects().getFirst().duration >= 30 * 20){
                    stack.set(AddonComponents.PROGRESS,-1);
                }
            });
        }
    }

    public static void tryConsume(ItemStack stack){
        int current = stack.getOrDefault(AddonComponents.PROGRESS, 0);
        if(current >= SINGLE_CONSUME){
            stack.set(AddonComponents.PROGRESS, current - SINGLE_CONSUME);
            if(stack.getOrDefault(AddonComponents.PROGRESS, 0) <= 0){
                stack.remove(DataComponents.POTION_CONTENTS);
            }
        }
    }
}
