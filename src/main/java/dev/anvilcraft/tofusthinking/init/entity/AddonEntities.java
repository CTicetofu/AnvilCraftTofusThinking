package dev.anvilcraft.tofusthinking.init.entity;

import dev.anvilcraft.lib.v2.registrum.util.entry.EntityEntry;
import dev.anvilcraft.tofusthinking.client.renderer.entity.ElectromagneticProjectileRenderer;
import dev.anvilcraft.tofusthinking.client.renderer.entity.GemMissileRenderer;
import dev.anvilcraft.tofusthinking.client.renderer.entity.MeteorRenderer;
import dev.anvilcraft.tofusthinking.client.renderer.entity.StrangeWitherSkullRenderer;
import dev.anvilcraft.tofusthinking.entity.FallingImitativeBlockEntity;
import dev.anvilcraft.tofusthinking.entity.livingEntity.StrangeWither;
import dev.anvilcraft.tofusthinking.entity.projectile.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Blocks;

import static dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking.REGISTRUM;

public class AddonEntities {
    public static void register() {
    }
    public static final EntityEntry<? extends CurseSnowball> CURSE_SNOWBALL = REGISTRUM
            .<CurseSnowball>entity("curse_snowball",CurseSnowball::new, MobCategory.MISC)
            .properties(it -> it.sized(0.25F,0.25F).clientTrackingRange(8).updateInterval(10))
            .renderer(() -> ThrownItemRenderer::new)
            .register();

    public static final EntityEntry<? extends StrangeWither> STRANGE_WITHER = REGISTRUM
            .<StrangeWither>entity("strange_wither",StrangeWither::new, MobCategory.MISC)
            .properties(it -> it.sized(0.3125F,0.3125F).fireImmune().immuneTo(Blocks.WITHER_ROSE).clientTrackingRange(10))
            .renderer(() -> WitherBossRenderer::new)
            .register();

    public static final EntityEntry<? extends StrangeWitherSkull> STRANGE_WITHER_SKULL = REGISTRUM
            .<StrangeWitherSkull>entity("strange_wither_skull",StrangeWitherSkull::new, MobCategory.MISC)
            .properties(it -> it.sized(0.3125F,0.3125F).clientTrackingRange(10).updateInterval(10))
            .renderer(() -> StrangeWitherSkullRenderer::new)
            .register();

    public static final EntityEntry<? extends Meteor> METEOR = REGISTRUM
            .<Meteor>entity("meteor",Meteor::new, MobCategory.MISC)
            .properties(it -> it.sized(0.6F,0.6F).clientTrackingRange(10).updateInterval(5))
            .renderer(() -> MeteorRenderer::new)
            .register();

    public static final EntityEntry<? extends GemMissile> GEM_MISSILE = REGISTRUM
            .<GemMissile>entity("gem_missile",GemMissile::new, MobCategory.MISC)
            .properties(it -> it.sized(0.3F,0.3F).clientTrackingRange(32).updateInterval(5))
            .renderer(() -> GemMissileRenderer::new)
            .register();

    public static final EntityEntry<? extends ElectromagneticArrow> ELECTROMAGNETIC_ARROW = REGISTRUM
            .<ElectromagneticArrow>entity("electromagnetic_arrow",ElectromagneticArrow::new, MobCategory.MISC)
            .properties(it -> it.sized(0.5F,0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20))
            .renderer(() -> TippableArrowRenderer::new)
            .register();

    public static final EntityEntry<? extends ElectromagneticProjectile> ELECTROMAGNETIC_PROJECTILE = REGISTRUM
            .<ElectromagneticProjectile>entity("electromagnetic_projectile",ElectromagneticProjectile::new, MobCategory.MISC)
            .properties(it -> it.sized(0.5F,0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20))
            .renderer(() -> ElectromagneticProjectileRenderer::new)
            .register();

    public static final EntityEntry<? extends FallingImitativeBlockEntity> FALLING_SPECTRAL_BLOCK = REGISTRUM
            .<FallingImitativeBlockEntity>entity("falling_imitative_block", FallingImitativeBlockEntity::new, MobCategory.MISC)
            .properties(builder -> builder.sized(0.98f, 0.98f))
            .renderer(() -> FallingBlockRenderer::new)
            .register();
}
