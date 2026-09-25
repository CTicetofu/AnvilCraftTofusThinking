package dev.anvilcraft.tofusthinking.config;

import dev.anvilcraft.lib.v2.config.BoundedDiscrete;
import dev.anvilcraft.lib.v2.config.Comment;
import dev.anvilcraft.lib.v2.config.Config;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import net.neoforged.fml.config.ModConfig;

@Config(name = AnvilCraftTofusThinking.MOD_ID,type = ModConfig.Type.SERVER)
public class AnvilCraftTofusThinkServerConfig {
    @Comment("Original Conduit which Overload will make a Explosion,When true,It will break blocks")
    public boolean originalConduitOverloadTakeDestructiveExplosion = true;

    @Comment("Overload Generator which Overload will make destruction when true")
    public boolean overloadGeneratorOverloadTakeDestruction = true;
}
