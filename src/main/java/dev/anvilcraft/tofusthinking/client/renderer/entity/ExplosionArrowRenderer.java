package dev.anvilcraft.tofusthinking.client.renderer.entity;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.entity.projectile.ExplosionArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jspecify.annotations.NonNull;

@OnlyIn(Dist.CLIENT)
public class ExplosionArrowRenderer extends ArrowRenderer<ExplosionArrow> {
    public static final ResourceLocation NORMAL_ARROW_LOCATION = AnvilCraftTofusThinking.of("textures/entity/projectiles/explosion_arrow.png");
    public ExplosionArrowRenderer(EntityRendererProvider.Context context) {super(context);}

    @Override
    public @NonNull ResourceLocation getTextureLocation(@NonNull ExplosionArrow explosionArrow) {
        return NORMAL_ARROW_LOCATION;
    }
}
