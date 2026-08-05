package dev.anvilcraft.tofusthinking.init.entity;

import dev.anvilcraft.lib.v2.registrum.util.entry.EntityEntry;
import dev.anvilcraft.tofusthinking.entity.projectile.CurseSnowball;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.MobCategory;

import static dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking.REGISTRUM;

public class AddonEntities {
    public static void register() {
    }
    public static final EntityEntry<? extends CurseSnowball> CURSE_SNOWBALL = REGISTRUM
            .<CurseSnowball>entity("curse_snowball",CurseSnowball::new, MobCategory.MISC)
            .properties(it -> it.sized(0.25F,0.25F).clientTrackingRange(4).updateInterval(10))
            .renderer(() -> ThrownItemRenderer::new)
            .register();
}
