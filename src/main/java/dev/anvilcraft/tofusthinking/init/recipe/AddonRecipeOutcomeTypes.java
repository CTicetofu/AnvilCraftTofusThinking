package dev.anvilcraft.tofusthinking.init.recipe;

import dev.anvilcraft.lib.v2.recipe.init.LibRegistries;
import dev.anvilcraft.lib.v2.recipe.outcome.IRecipeOutcome;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.data.recipe.anvil.outcome.ResurrectionAmberOutcome;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AddonRecipeOutcomeTypes {
    public static final DeferredRegister<IRecipeOutcome.Type<?>> OUTCOME_TYPE = DeferredRegister.create(
            LibRegistries.OUTCOME_TYPE_REGISTRY,
            AnvilCraftTofusThinking.MOD_ID
    );
    public static final DeferredHolder<IRecipeOutcome.Type<?>, ResurrectionAmberOutcome.Type> RESURRECTION_AMBER =
            OUTCOME_TYPE.register(
                    "resurrection_amber",
                    ResurrectionAmberOutcome.Type::new
            );
}
