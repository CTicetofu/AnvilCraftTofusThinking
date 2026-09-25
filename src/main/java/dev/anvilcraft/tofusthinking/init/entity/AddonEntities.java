package dev.anvilcraft.tofusthinking.init.entity;

import dev.anvilcraft.lib.v2.registrum.util.entry.EntityEntry;
import dev.anvilcraft.tofusthinking.client.renderer.entity.MeteorRenderer;
import dev.anvilcraft.tofusthinking.client.renderer.entity.StrangeWitherSkullRenderer;
import dev.anvilcraft.tofusthinking.entity.FallingImitativeBlockEntity;
import dev.anvilcraft.tofusthinking.entity.livingEntity.StrangeWither;
import dev.anvilcraft.tofusthinking.entity.projectile.CurseSnowball;
import dev.anvilcraft.tofusthinking.entity.projectile.Meteor;
import dev.anvilcraft.tofusthinking.entity.projectile.StrangeWitherSkull;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.WitherBossRenderer;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Blocks;

import static dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking.REGISTRUM;

public class AddonEntities {
    public static void register() {
    }
    public static final EntityEntry<? extends CurseSnowball> CURSE_SNOWBALL = REGISTRUM
            .<CurseSnowball>entity("curse_snowball",CurseSnowball::new, MobCategory.MISC)
            .properties(it -> it.sized(0.25F,0.25F).clientTrackingRange(4).updateInterval(10))
            .renderer(() -> ThrownItemRenderer::new)
            .register();

    public static final EntityEntry<? extends StrangeWither> STRANGE_WITHER = REGISTRUM
            .<StrangeWither>entity("strange_wither",StrangeWither::new, MobCategory.MISC)
            .properties(it -> it.sized(0.3125F,0.3125F).fireImmune().immuneTo(Blocks.WITHER_ROSE).clientTrackingRange(10))
            .renderer(() -> WitherBossRenderer::new)
            .register();

    public static final EntityEntry<? extends StrangeWitherSkull> STRANGE_WITHER_SKULL = REGISTRUM
            .<StrangeWitherSkull>entity("strange_wither_skull",StrangeWitherSkull::new, MobCategory.MISC)
            .properties(it -> it.sized(0.3125F,0.3125F).clientTrackingRange(4).updateInterval(10))
            .renderer(() -> StrangeWitherSkullRenderer::new)
            .register();

    public static final EntityEntry<? extends Meteor> METEOR = REGISTRUM
            .<Meteor>entity("meteor",Meteor::new, MobCategory.MISC)
            .properties(it -> it.sized(0.6F,0.6F).clientTrackingRange(4).updateInterval(10))
            .renderer(() -> MeteorRenderer::new)
            .register();

    public static final EntityEntry<? extends FallingImitativeBlockEntity> FALLING_SPECTRAL_BLOCK = REGISTRUM
            .<FallingImitativeBlockEntity>entity("falling_imitative_block", FallingImitativeBlockEntity::new, MobCategory.MISC)
            .properties(builder -> builder.sized(0.98f, 0.98f))
            .renderer(() -> FallingBlockRenderer::new)
            .register();
}
