package dev.anvilcraft.tofusthinking.item.weapon;

import com.mojang.datafixers.util.Pair;
import dev.anvilcraft.tofusthinking.attachment.magic.CooldownManager;
import dev.anvilcraft.tofusthinking.client.ClientCooldownCache;
import dev.anvilcraft.tofusthinking.entity.projectile.GemMissile;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.init.item.AddonItemTags;
import dev.anvilcraft.tofusthinking.item.LeftClickAction;
import dev.anvilcraft.tofusthinking.network.toServer.LeftClickPacket;
import dev.anvilcraft.tofusthinking.util.ItemUtil;
import dev.anvilcraft.tofusthinking.util.TooltipUtil;
import dev.dubhe.anvilcraft.init.item.ModComponents;
import dev.dubhe.anvilcraft.init.item.ModItemTags;
import dev.dubhe.anvilcraft.item.property.component.StoredItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GemStaff extends Item implements LeftClickAction {
    public static final int MAX_ENERGY = 1600000;
    public static final String SPELL_ID = "GemMissile";
    public GemStaff(Properties properties){
        super(properties);
    }

    public GemStaff() {
        this(new Properties().stacksTo(1).component(AddonComponents.MAX_ENERGY,MAX_ENERGY).component(AddonComponents.HEAD_TYPE,"amethyst").component(AddonComponents.GRIP_TYPE,"amethyst"));
    }
    public static final String[] TYPE_LIST = {"amethyst","topaz","sapphire","emerald","ruby","diamond","amber"};
    public static final Map<String, Pair<Float,Integer>> BASE_VALUE_MAP;
    static {
        BASE_VALUE_MAP = Map.of("amethyst", Pair.of(5F, 0xCFE066FF), "topaz", Pair.of(6F, 0xCFFFFACD), "sapphire", Pair.of(6.5F, 0xCF00BFFF), "emerald", Pair.of(7F, 0xFF7CFC00), "ruby", Pair.of(7.5F, 0xCFFF6A6A), "diamond", Pair.of(8F, 0xCFFFFFFF), "amber", Pair.of(9F, 0xCFFFA54F));
    }
    public static final List<TagKey<Item>> GEM_LIST_TAG = List.of(
            Tags.Items.GEMS_AMETHYST,
            ModItemTags.GEMS_TOPAZ,
            ModItemTags.GEMS_SAPPHIRE,
            Tags.Items.GEMS_EMERALD,
            ModItemTags.GEMS_RUBY,
            Tags.Items.GEMS_DIAMOND,
            ModItemTags.GEMS_AMBER
    );
    public static final List<TagKey<Item>> GEM_BLOCK_LIST_TAG = List.of(
            AddonItemTags.STORAGE_BLOCKS_AMETHYST,
            ModItemTags.STORAGE_BLOCKS_TOPAZ,
            ModItemTags.STORAGE_BLOCKS_SAPPHIRE,
            Tags.Items.STORAGE_BLOCKS_EMERALD,
            ModItemTags.STORAGE_BLOCKS_RUBY,
            Tags.Items.STORAGE_BLOCKS_DIAMOND,
            ModItemTags.STORAGE_BLOCKS_AMBER
    );
    @Override
    public void verifyComponentsAfterLoad(@NotNull ItemStack stack) {
        if (!stack.has(ModComponents.DISPLAY_ITEM)) {
            stack.set(ModComponents.DISPLAY_ITEM, new StoredItem(Items.AMETHYST_SHARD.getDefaultInstance()));
        }
        if (!stack.has(AddonComponents.DISPLAY_ANOTHER_ITEM)) {
            stack.set(AddonComponents.DISPLAY_ANOTHER_ITEM, new StoredItem(Items.AMETHYST_BLOCK.getDefaultInstance()));
        }
        super.verifyComponentsAfterLoad(stack);
    }

    @Override
    public boolean canAttackBlock(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player) {
        return false;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public boolean supportsEnchantment(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
        return super.supportsEnchantment(stack, enchantment) || enchantment.getKey() == Enchantments.LOOTING;
    }

    @Override
    public int getEnchantmentValue(@NotNull ItemStack stack) {
        return 30;
    }

    @Override
    public boolean onDroppedByPlayer(@NotNull ItemStack item, @NotNull Player player) {
        return super.onDroppedByPlayer(item, player);
    }

    public void shootGemMissile(ItemStack stack, ServerPlayer player, Level level){
        if(CooldownManager.isOnCooldown(player,SPELL_ID) ||!ItemUtil.consumeEnergy(player,stack,2000)){return;}
        CooldownManager.addCooldown(player,SPELL_ID,20);
        GemMissile missile = new GemMissile(level,player);
        missile.setPos(player.getEyePosition());
        missile.setDeltaMovement(player.getLookAngle().scale(1F));
        String head = stack.getOrDefault(AddonComponents.HEAD_TYPE,"none");
        String grip = stack.getOrDefault(AddonComponents.GRIP_TYPE,"none");
        missile.damage = getDamageValue(head);
        missile.setColor(getColorValue(head));
        makeTypeEffect(head,missile);
        if(head.equals(grip)){
            missile.damage *= 1.2F;
        } else {
            makeTypeEffect(grip,missile);
        }
        missile.doSplit();
        level.addFreshEntity(missile);
        level.playSound(null,player.getX(),player.getY(),player.getZ(), SoundEvents.AMETHYST_BLOCK_RESONATE,player.getSoundSource(),1.8F,1.4F);
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        float damage = getDamageValue(stack.getOrDefault(AddonComponents.HEAD_TYPE,"none")) - 1;
        builder.add(Attributes.ATTACK_DAMAGE,new AttributeModifier(BASE_ATTACK_DAMAGE_ID,damage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        builder.add(Attributes.ATTACK_SPEED,new AttributeModifier(BASE_ATTACK_SPEED_ID,-3, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        return builder.build();
    }

    private void makeTypeEffect(String type, GemMissile missile){
        switch (type){
            case "amethyst" -> missile.canSeek = true;
            case "topaz" -> missile.canExplode = true;
            case "sapphire" -> {missile.canFreeze = true;missile.damage *= 0.6F;missile.setSplitCount(missile.getSplitCount() + 1);}
            case "emerald" -> {
                missile.canRetent = true;
                missile.setDeltaMovement(missile.getDeltaMovement().scale(2F));
                missile.setPierceLevel(missile.getPierceLevel() + 1);
            }
            case "ruby" -> {missile.canSummon = true;missile.damage *= 0.6F;}
            case "diamond" -> {
                missile.canBright = true;
                missile.setScale(50);
                missile.setPierceLevel(missile.getPierceLevel() + 1);
            }
            case "amber" -> {missile.canBonus = true;missile.damage *= 0.8F;missile.setPierceLevel(missile.getPierceLevel() + 2);}
        }
    }

    public boolean overrideOtherStackedOnMe(@NotNull ItemStack stack, @NotNull ItemStack other, @NotNull Slot slot, @NotNull ClickAction action, @NotNull Player player, @NotNull SlotAccess access) {
        if(action == ClickAction.SECONDARY && slot.allowModification(player)){
            int headIndex = getHeadIndex(other);
            boolean match = true;
            if(headIndex >= 0){
                stack.set(ModComponents.DISPLAY_ITEM,new StoredItem(other.copyWithCount(1)));
                stack.set(AddonComponents.HEAD_TYPE,TYPE_LIST[headIndex]);
            } else {
                int gripIndex = getGripIndex(other);
                if(gripIndex >= 0){
                    stack.set(AddonComponents.DISPLAY_ANOTHER_ITEM,new StoredItem(other.copyWithCount(1)));
                    stack.set(AddonComponents.GRIP_TYPE,TYPE_LIST[gripIndex]);
                } else {
                    match = false;
                }
            }
            if(match){
                player.level().playLocalSound(player, SoundEvents.AMETHYST_BLOCK_PLACE,player.getSoundSource(),1,1);
            }
            return match;
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltips, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltips, flag);
        boolean shift = Screen.hasShiftDown();
        tooltips.add(TooltipUtil.getItemEnergyTooltip(stack, shift));
        tooltips.add(TooltipUtil.getItemNeedEnergy(2000, shift));
        String head = stack.getOrDefault(AddonComponents.HEAD_TYPE,"none");
        String grip = stack.getOrDefault(AddonComponents.GRIP_TYPE,"none");
        int headColor = getColorValue(head);
        int gripColor = getColorValue(grip);
        tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.gem_staff_" + head).withColor(headColor));
        if(shift){
            tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.gem_staff_shift_" + head).withColor(headColor));
        }
        if(!head.equals(grip)){
            tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.gem_staff_" + grip).withColor(gripColor));
            if(shift){
                tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.gem_staff_shift_" + grip).withColor(gripColor));
            }
        } else {
            if(shift){
                tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.gem_staff_same").withColor(headColor));
            }
        }
        if(!shift){
            tooltips.add(TooltipUtil.HOLD_SHIFT_FOR_MORE.copy());
        } else {
            tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.gem_staff_change_type").withStyle(ChatFormatting.GRAY));
        }
    }

    public static float getDamageValue(String s){
       return BASE_VALUE_MAP.getOrDefault(s,Pair.of(5F,0xFFFFFFFF)).getFirst();
    }

    public static int getColorValue(String s){
        return BASE_VALUE_MAP.getOrDefault(s,Pair.of(5F,0xFFFFFFFF)).getSecond();
    }

    public static int getHeadIndex(ItemStack stack){
        for (int i = 0; i < GEM_LIST_TAG.size(); i++) {
            if(stack.is(GEM_LIST_TAG.get(i))){
                return i;
            }
        }
        return -1;
    }

    public static int getGripIndex(ItemStack stack){
        for (int i = 0; i < GEM_BLOCK_LIST_TAG.size(); i++) {
            if(stack.is(GEM_BLOCK_LIST_TAG.get(i))){
                return i;
            }
        }
        return -1;
    }

    public static ItemStack getHeadItem(@NotNull ItemStack stack) {
        return Optional.ofNullable(stack.get(ModComponents.DISPLAY_ITEM)).map(StoredItem::stored).orElse(Items.AMETHYST_SHARD.getDefaultInstance());
    }

    public static ItemStack getGripItem(@NotNull ItemStack stack) {
        return Optional.ofNullable(stack.get(AddonComponents.DISPLAY_ANOTHER_ITEM)).map(StoredItem::stored).orElse(Items.AMETHYST_BLOCK.getDefaultInstance());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return ClientCooldownCache.isOnCooldown(SPELL_ID);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return (int) (13 * ClientCooldownCache.getProgress(SPELL_ID));
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return 0x00CD66;
    }

    @Override
    public void onClientClick(ItemStack stack, Player player) {
        if(ClientCooldownCache.isOnCooldown(SPELL_ID) || (!player.isCreative() && !ItemUtil.hasEnoughEnergy(stack,2000))){return;}
        LeftClickPacket.sendToServer();
        player.swing(InteractionHand.MAIN_HAND);
    }

    @Override
    public void onServerClick(ItemStack stack, Player player) {
        if(stack.getItem() != this){return;}
        shootGemMissile(stack, (ServerPlayer) player,player.level());
    }
}
