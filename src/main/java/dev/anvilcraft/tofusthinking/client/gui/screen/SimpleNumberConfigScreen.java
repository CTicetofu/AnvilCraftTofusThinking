package dev.anvilcraft.tofusthinking.client.gui.screen;

import dev.anvilcraft.tofusthinking.inventory.SimpleNumberConfigMenu;
import dev.anvilcraft.tofusthinking.network.toServer.SimpleNumberUpdatePacket;
import dev.dubhe.anvilcraft.client.gui.component.TexturedButton;
import dev.dubhe.anvilcraft.constant.SharedTextures;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class SimpleNumberConfigScreen extends AbstractContainerScreen<SimpleNumberConfigMenu> {
    public static final ResourceLocation BACKGROUND = SharedTextures.bg("misc", "slider_like");
    public static final ResourceLocation BUTTON_MAX = SharedTextures.textureGui("misc/slider_like/button_max");
    public static final ResourceLocation BUTTON_ADD = SharedTextures.textureGui("misc/slider_like/button_add");
    public static final ResourceLocation BUTTON_MINUS = SharedTextures.textureGui("misc/slider_like/button_minus");
    public static final ResourceLocation BUTTON_MIN = SharedTextures.textureGui("misc/slider_like/button_min");

    private EditBox value;
    private int number = 0;
    private int maxNumber = 4096;
    private int minNumber = 0;
    private StringWidget minNumWidget;
    private StringWidget maxNumWidget;

    public SimpleNumberConfigScreen(SimpleNumberConfigMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 77;
    }



    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = 2;
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;
        this.value = new EditBox(this.font, offsetX + 50, offsetY + 47, 76, 8, Component.literal("value"));
        this.value.setCanLoseFocus(true);
        this.value.setTextColor(-1);
        this.value.setTextColorUneditable(-1);
        this.value.setBordered(false);
        this.value.setMaxLength(50);
        this.value.setResponder(this::onValueInput);
        this.value.setValue("");
        TexturedButton max = new TexturedButton(
                152 + offsetX,
                43 + offsetY,
                16,
                16,
                BUTTON_MAX,
                16,
                16,
                32,
                (btn) -> this.setNumberWithValue(this.maxNumber));
        TexturedButton add = new TexturedButton(
                134 + offsetX,
                43 + offsetY,
                16,
                16,
                BUTTON_ADD,
                16,
                16,
                32,
                (btn) -> this.changeNumber(true),
                Component.literal("4")
        );
        TexturedButton min = new TexturedButton(
                8 + offsetX,
                43 + offsetY,
                16,
                16,
                BUTTON_MIN,
                16,
                16,
                32,
                (btn) -> this.setNumberWithValue(this.minNumber));
        TexturedButton minus = new TexturedButton(
                26 + offsetX,
                43 + offsetY,
                16,
                16,
                BUTTON_MINUS,
                16,
                16,
                32,
                (btn) -> this.changeNumber(false));
        minNumWidget = new StringWidget(2 + offsetX,28 + offsetY,64,16,Component.empty(),this.font);
        maxNumWidget = new StringWidget(110 + offsetX,28 + offsetY,64,16,Component.empty(),this.font);
        StringWidget leftArrow = new StringWidget(42 + offsetX,28 + offsetY,64,16,Component.literal("<--").withStyle(ChatFormatting.GRAY),this.font);
        StringWidget rightArrow = new StringWidget(68 + offsetX,28 + offsetY,64,16,Component.literal("-->").withStyle(ChatFormatting.GRAY),this.font);
        this.addRenderableWidget(max);
        this.addRenderableWidget(add);
        this.addRenderableWidget(min);
        this.addRenderableWidget(minus);
        this.addRenderableWidget(minNumWidget);
        this.addRenderableWidget(maxNumWidget);
        this.addRenderableWidget(leftArrow);
        this.addRenderableWidget(rightArrow);
        this.addRenderableWidget(this.value);
        this.setInitialFocus(this.value);
        setValueNumber(this.number);
        resetNumWidget();
    }

    private void onValueInput(String value) {
        if (value.isEmpty() || value.equals("-")) {
            return;
        }
        try {
            int num = Integer.parseInt(value);
            setNumber(num);
        } catch (NumberFormatException e) {
            setValueNumber(this.number);
        }
    }
    private void setNumber(int num){
        this.number = Mth.clamp(num,this.minNumber,this.maxNumber);
        PacketDistributor.sendToServer(new SimpleNumberUpdatePacket(num));
    }
    public void setNumberWithValue(int num){
        setNumber(num);
        setValueNumber(this.number);
    }
    public void setRange(int left,int right){
        if(left < right){
            this.minNumber = left;
            this.maxNumber = right;
        } else {
            this.minNumber = right;
            this.maxNumber = left;
        }
        resetNumWidget();
    }
    private void resetNumWidget(){
        this.minNumWidget.setMessage(Component.literal(String.valueOf(this.minNumber)).withStyle(ChatFormatting.GRAY));
        this.maxNumWidget.setMessage(Component.literal(String.valueOf(this.maxNumber)).withStyle(ChatFormatting.GRAY));
    }
    private void setValueNumber(int num){
        this.value.setValue(String.valueOf(num));
    }
    private void changeNumber(boolean add){
        int operation = add ? 1 : -1;
        int absNumber = Math.abs(number);
        if(Screen.hasShiftDown() && absNumber > 1){
            int positive = number > 0 ? 1 : -1;
            int level = (int) (Math.log(absNumber)/Math.log(2));
            if(positive * operation > 0){
                absNumber = 2 << level;
            } else {
                absNumber = absNumber == 1 << level ? 1 << level - 1 : 1 << level;
            }
            number = positive * absNumber;
        } else {
            number += operation;
        }
        setNumberWithValue(number);
    }
    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        this.init(minecraft, width, height);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
    }
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(BACKGROUND, offsetX, offsetY, 0, 0, this.imageWidth, this.imageHeight, 256, 128);
    }
}
