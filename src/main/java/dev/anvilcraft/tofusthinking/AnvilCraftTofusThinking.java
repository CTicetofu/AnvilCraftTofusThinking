package dev.anvilcraft.tofusthinking;

import dev.anvilcraft.lib.v2.registrum.Registrum;
import dev.anvilcraft.tofusthinking.data.TofusThinkingDatagen;
import dev.anvilcraft.tofusthinking.init.AddonMenuTypes;
import dev.anvilcraft.tofusthinking.init.AddonMobEffects;
import dev.anvilcraft.tofusthinking.init.AddonNetworks;
import dev.anvilcraft.tofusthinking.init.block.AddonBlockEntities;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.init.block.AddonFluids;
import dev.anvilcraft.tofusthinking.init.entity.AddonEntities;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.init.item.AddonItemGroups;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
@Mod(AnvilCraftTofusThinking.MOD_ID)
public class AnvilCraftTofusThinking {
    public static final String MOD_ID = "anvilcraft_tofus_thinking";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Registrum REGISTRUM = Registrum.create(MOD_ID).defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    public static ResourceLocation of(String path){
        return ResourceLocation.fromNamespaceAndPath(MOD_ID,path);
    }


    public AnvilCraftTofusThinking(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
        AddonItemGroups.register(modEventBus);
        AddonBlocks.register();
        AddonFluids.register(modEventBus);
        AddonEntities.register();
        AddonItems.register();
        AddonBlockEntities.register();
        AddonMenuTypes.register();
        AddonComponents.register(modEventBus);
        AddonMobEffects.register(modEventBus);

        TofusThinkingDatagen.init();

        modEventBus.addListener(this::registerPayloads);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        AddonNetworks.init(registrar);
    }
    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
