package dev.anvilcraft.tofusthinking.event;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.anvil.RewindLivingEntityBehavior;
import dev.dubhe.anvilcraft.api.event.AnvilBehaviorRegisterEvent;
import net.minecraft.tags.BlockTags;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = AnvilCraftTofusThinking.MOD_ID)
public class AddonAnvilBehaviors {
    @SubscribeEvent
    public static void register(AnvilBehaviorRegisterEvent event) {
        event.registerBehavior(state -> state.is(BlockTags.CAULDRONS), new RewindLivingEntityBehavior());
    }
}
