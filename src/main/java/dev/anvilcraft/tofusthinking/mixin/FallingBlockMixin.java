package dev.anvilcraft.tofusthinking.mixin;

import dev.anvilcraft.tofusthinking.api.block.Falling;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.FallingBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FallingBlock.class)
public abstract class FallingBlockMixin implements Falling {
    @Shadow
    protected abstract void falling(FallingBlockEntity entity);

    @Override
    public void anvilCraftTofusThinking$toFalling(FallingBlockEntity entity) {
        falling(entity);
    }
}
