package dev.anvilcraft.tofusthinking.init.recipe;

import net.neoforged.bus.api.IEventBus;

public class AddonRecipeInits {
    public static void init(IEventBus modEventBus) {
        AddonRecipeOutcomeTypes.OUTCOME_TYPE.register(modEventBus);
    }
}
