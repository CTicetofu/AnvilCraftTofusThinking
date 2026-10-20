package dev.anvilcraft.tofusthinking.client.event;

import dev.anvilcraft.lib.v2.util.ListUtil;
import dev.anvilcraft.lib.v2.util.Util;
import dev.anvilcraft.tofusthinking.client.ClientCooldownCache;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import dev.anvilcraft.tofusthinking.item.LeftClickAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

@EventBusSubscriber(Dist.CLIENT)
public class ClientManageHandler {
    public static int TICK_COUNT = 0;
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event){
        if(TICK_COUNT++ > 72000){
            TICK_COUNT = 0;
        }
        ClientCooldownCache.tickClient();
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if(player == null){return;}
        if(minecraft.screen == null && minecraft.options.keyAttack.isDown()){
            ItemStack stack = player.getMainHandItem();
            if(stack.getItem() instanceof LeftClickAction action){
                action.onClientClick(stack,player);
            }
        }
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientCooldownCache.clear();
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event){
        if(event.getHand() == InteractionHand.OFF_HAND){
            LocalPlayer player = Minecraft.getInstance().player;
            if(player != null){
                ItemStack stack = player.getMainHandItem();
                if(stack.is(AddonItems.ELECTROMAGNETIC_CROSSBOW) && CrossbowItem.isCharged(stack)){event.setCanceled(true);}
            }
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event){
        ItemStack stack = event.getItemStack();
        List<Component> list = event.getToolTip();
        if(stack.has(AddonComponents.DENSE)){
            propertyTooltip("dense", list, 0xCDBE70);
        }
        if(stack.has(AddonComponents.ANTIDOTE)){
            propertyTooltip("antidote", list, 0x00FF00);
        }
        if(stack.has(AddonComponents.HEAVY_HIT)){
            propertyTooltip("heavy_hit", list, 0x7B68EE);
        }
        if(stack.has(AddonComponents.IMMORTAL)){
            propertyTooltip("immortal", list, 0xFF7F24);
        }
    }

    private static void propertyTooltip(String propertyName, List<Component> tooltip, int color) {
        int i = 0;
        for (int j = 0; j < tooltip.size(); j++) {
            if (tooltip.get(j).getContents() instanceof TranslatableContents t && t.getKey().contains("enchantment")
                    && ListUtil.safelyGet(tooltip, j + 1)
                    .flatMap(tooltipI -> Util.castSafely(tooltipI.getContents(), TranslatableContents.class))
                    .map(TranslatableContents::getKey)
                    .filter(key -> key.contains("enchantment"))
                    .isEmpty()
            ) {
                i = j;
                break;
            }
        }
        tooltip.add(
                1 + i,
                Component.translatable("tooltip.anvilcraft_tofus_thinking.%s".formatted(propertyName)).withColor(color)
        );
    }
}
