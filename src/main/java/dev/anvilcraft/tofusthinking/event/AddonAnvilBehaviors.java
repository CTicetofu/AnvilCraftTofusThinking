package dev.anvilcraft.tofusthinking.event;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.anvil.NutrientExtractBehavior;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.dubhe.anvilcraft.api.event.AnvilBehaviorRegisterEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = AnvilCraftTofusThinking.MOD_ID)
public class AddonAnvilBehaviors {
    @SubscribeEvent
    public static void register(AnvilBehaviorRegisterEvent event) {
        event.registerBehavior(AddonBlocks.NUTRIENT_EXTRACTOR.get(),new NutrientExtractBehavior());
    }
}
