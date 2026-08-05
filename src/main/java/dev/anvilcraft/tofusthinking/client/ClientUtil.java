package dev.anvilcraft.tofusthinking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientUtil {
    public static LocalPlayer getClientPlayer(){
        return Minecraft.getInstance().player;
    }

    public static int getUseTime(){
        return getClientPlayer().getTicksUsingItem();
    }

    public static ItemStack getUseItemStack(){
        return getClientPlayer().getUseItem();
    }
}
