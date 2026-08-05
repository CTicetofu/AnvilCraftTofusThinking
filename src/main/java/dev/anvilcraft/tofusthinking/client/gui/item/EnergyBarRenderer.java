package dev.anvilcraft.tofusthinking.client.gui.item;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;
import org.jetbrains.annotations.NotNull;

public class EnergyBarRenderer implements IItemDecorator, EnergyBar {
    public static EnergyBarRenderer DEFAULT = new EnergyBarRenderer();
    @Override
    public boolean render(@NotNull GuiGraphics guiGraphics, @NotNull Font font, @NotNull ItemStack stack, int x, int y) {
        PlanePoint startPoint = this.getStartPoint();
        PlanePoint endPoint = this.getEndPoint();
        PlanePoint backgroundPoint = this.getBackgroundEnd();
        float progress = Math.clamp(getProgress(stack),0,1);
        guiGraphics.fill(RenderType.guiOverlay(), x + startPoint.x, y + startPoint.y, x + backgroundPoint.x, y + backgroundPoint.y, -16777216);
        guiGraphics.fill(RenderType.guiOverlay(), x + startPoint.x, y + startPoint.y, x + endPoint.x,y + Mth.lerpInt(progress,startPoint.y,endPoint.y), this.getColor(stack));
        return true;
    }
}
