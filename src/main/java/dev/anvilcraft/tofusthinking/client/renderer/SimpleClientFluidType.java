package dev.anvilcraft.tofusthinking.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;

public class SimpleClientFluidType implements IClientFluidTypeExtensions {
    private final ResourceLocation texture;
    private final int color;

    public SimpleClientFluidType(ResourceLocation texture, int color) {
        this.texture = texture;
        this.color = color;
    }

    @Override
    public @NotNull ResourceLocation getStillTexture() {
        return texture;
    }

    @Override
    public @NotNull ResourceLocation getFlowingTexture() {
        return texture;
    }

    @Override
    public int getTintColor() {
        return color;
    }
}
