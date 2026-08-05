package dev.anvilcraft.tofusthinking;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = AnvilCraftTofusThinking.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = AnvilCraftTofusThinking.MOD_ID, value = Dist.CLIENT)
public class AnvilCraftTofusThinkingClient {
    public AnvilCraftTofusThinkingClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
    }

    @SubscribeEvent
    public static void onModelBake(ModelEvent.RegisterAdditional event){
        event.register(ModelResourceLocation.standalone(AnvilCraftTofusThinking.of("item/conduit_staff_model")));
    }
}
