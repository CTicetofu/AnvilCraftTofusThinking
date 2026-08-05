package dev.anvilcraft.tofusthinking.client.init;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class AddonModelLayers {
    public static final ModelLayerLocation CONDUIT_EYE =
            new ModelLayerLocation(AnvilCraftTofusThinking.of("conduit"), "eye");
    public static final ModelLayerLocation CONDUIT_WIND =
            new ModelLayerLocation(AnvilCraftTofusThinking.of("conduit"), "wind");
    public static final ModelLayerLocation CONDUIT_SHELL =
            new ModelLayerLocation(AnvilCraftTofusThinking.of("conduit"), "shell");
    public static final ModelLayerLocation CONDUIT_CAGE =
            new ModelLayerLocation(AnvilCraftTofusThinking.of("conduit"), "cage");
}
