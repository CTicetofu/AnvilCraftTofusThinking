package dev.anvilcraft.tofusthinking.config;

import dev.anvilcraft.lib.v2.config.BoundedDiscrete;
import dev.anvilcraft.lib.v2.config.Comment;
import dev.anvilcraft.lib.v2.config.Config;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import net.neoforged.fml.config.ModConfig;

@Config(name = AnvilCraftTofusThinking.MOD_ID,type = ModConfig.Type.COMMON)
public class AnvilCraftTofusThinkCommonConfig {
    @Comment("Smart Power Converter Max Input")
    @BoundedDiscrete(min = 1, max = Integer.MAX_VALUE)
    public int smartPowerConverterMaxInput = 4096;

    @Comment("Smart Power Converter Extremely Big Max Input")
    @BoundedDiscrete(min = 1, max = Integer.MAX_VALUE)
    public int smartPowerConverterExtremelyBigMaxInput = 1048567;

    @Comment("Can Tofu Anvil Ignore Enchantment Conflict")
    public boolean canTofuAnvilIgnoreEnchantmentConflict = true;
}
