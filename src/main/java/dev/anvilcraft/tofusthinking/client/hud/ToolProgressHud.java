package dev.anvilcraft.tofusthinking.client.hud;

import dev.anvilcraft.tofusthinking.item.IToolProgress;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class ToolProgressHud {
    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker){
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || minecraft.screen != null) return;
        LocalPlayer player = minecraft.player;
        if(player == null){return;}
        ItemStack stack = player.getUseItem();
        if(stack.isEmpty()){return;}
        if(stack.getItem() instanceof IToolProgress toolProgress && toolProgress.isToolProgressVisible(player,stack)){
            int x = graphics.guiWidth() / 2 - 8;
            int maxX = x + 17;
            int y = graphics.guiHeight() / 2 + 12;
            float progress = Math.clamp(toolProgress.getToolProgress(player,stack),0,1);
            graphics.fill(RenderType.guiOverlay(),x,y,maxX,y + 2,0xAFA9A9A9);
            graphics.fill(RenderType.guiOverlay(),x,y, Mth.lerpInt(progress,x,maxX),y + 2,toolProgress.getHudColor(player,stack));
        }

    }
}
