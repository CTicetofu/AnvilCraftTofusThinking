package dev.anvilcraft.tofusthinking.item.curio;

import dev.anvilcraft.tofusthinking.attachment.magic.CooldownManager;
import dev.anvilcraft.tofusthinking.client.ClientCooldownCache;
import dev.anvilcraft.tofusthinking.network.toServer.OpenEnderCharmPacket;
import dev.anvilcraft.tofusthinking.util.TooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.List;

public class EnderCharm extends CurioBaseItem{
    public static final String SKILL = "ender_chest";
    private static final Component TITLE = Component.translatable("container.enderchest");
    public EnderCharm(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canBeHurtBy(@NotNull ItemStack stack, @NotNull DamageSource source) {
        return source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public boolean isBookEnchantable(@NonNull ItemStack stack, @NonNull ItemStack book) {
        return false;
    }

    public static void openEnderChest(ServerPlayer player){
        CooldownManager.addCooldown(player, SKILL, 10);
        if(player.containerMenu instanceof ChestMenu menu && menu.getContainer() == player.getEnderChestInventory()){return;}
        player.openMenu(new SimpleMenuProvider((containerId, inventory, thePlayer) -> ChestMenu.threeRows(containerId, inventory, player.getEnderChestInventory()), TITLE));
    }

    public static void wantOpen(Player player){
        if(player.level().isClientSide && !ClientCooldownCache.isOnCooldown(SKILL)){
            player.level().playLocalSound(player, SoundEvents.ENDER_CHEST_OPEN,player.getSoundSource(),1,1);
            OpenEnderCharmPacket.sendToServer();
        }
    }

    @Override
    public @NonNull InteractionResultHolder<ItemStack> use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand usedHand) {
        wantOpen(player);
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    @Override
    public boolean overrideOtherStackedOnMe(@NonNull ItemStack stack, @NonNull ItemStack other, @NonNull Slot slot, @NonNull ClickAction action, @NonNull Player player, @NonNull SlotAccess access) {
        if(other.isEmpty() && action == ClickAction.SECONDARY){
            wantOpen(player);
            return true;
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
    }

    @Override
    public  ICurio.@NonNull DropRule getDropRule(SlotContext slotContext, DamageSource source, boolean recentlyHit, ItemStack stack) {
        return ICurio.DropRule.ALWAYS_KEEP;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltips, @NotNull TooltipFlag tooltipFlag) {
        boolean shift = Screen.hasShiftDown();
        if(shift){
            tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thing.ender_charm_shift").withStyle(ChatFormatting.GRAY));
            tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thing.ender_charm1").withStyle(ChatFormatting.GRAY));
            tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thing.ender_charm2", Component.keybind("key.anvilcraft_tofus_thinking.open_ender_charm")).withStyle(ChatFormatting.GRAY));
            tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thing.ender_charm3").withStyle(ChatFormatting.GRAY));
        } else {
            tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thing.ender_charm").withStyle(ChatFormatting.GRAY));
            tooltips.add(TooltipUtil.HOLD_SHIFT_FOR_MORE.copy());
        }
        super.appendHoverText(stack, context, tooltips, tooltipFlag);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @Override
    public boolean isEnderMask(SlotContext slotContext, EnderMan enderMan, ItemStack stack) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return ClientCooldownCache.isOnCooldown(SKILL);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return (int) (13 * ClientCooldownCache.getProgress(SKILL));
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return 0x00CD66;
    }
}
