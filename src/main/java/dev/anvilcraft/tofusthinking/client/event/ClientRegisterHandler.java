package dev.anvilcraft.tofusthinking.client.event;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.client.gui.item.EnergyBarRenderer;
import dev.anvilcraft.tofusthinking.client.hud.ToolProgressHud;
import dev.anvilcraft.tofusthinking.client.init.AddonModelLayers;
import dev.anvilcraft.tofusthinking.client.renderer.blockentity.OriginalConduitRenderer;
import dev.anvilcraft.tofusthinking.client.renderer.blockentity.OverloadGeneratorRenderer;
import dev.anvilcraft.tofusthinking.client.renderer.item.ConduitStaffRenderer;
import dev.anvilcraft.tofusthinking.client.renderer.item.OriginalConduitItemRenderer;
import dev.anvilcraft.tofusthinking.client.renderer.item.OriginalConduitStaffRenderer;
import dev.anvilcraft.tofusthinking.init.block.AddonBlockEntities;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientRegisterHandler {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(AddonModelLayers.CONDUIT_EYE, OriginalConduitRenderer::createEyeLayer);
        event.registerLayerDefinition(AddonModelLayers.CONDUIT_WIND, OriginalConduitRenderer::createWindLayer);
        event.registerLayerDefinition(AddonModelLayers.CONDUIT_SHELL, OriginalConduitRenderer::createShellLayer);
        event.registerLayerDefinition(AddonModelLayers.CONDUIT_CAGE, OriginalConduitRenderer::createCageLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(AddonBlockEntities.ORIGINAL_CONDUIT.get(), OriginalConduitRenderer::new);
        event.registerBlockEntityRenderer(AddonBlockEntities.OVERLOAD_GENERATOR.get(), OverloadGeneratorRenderer::new);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event){
        event.registerItem(OriginalConduitItemRenderer.ORIGINAL_CONDUIT_EXTENSION, AddonBlocks.ORIGINAL_CONDUIT.asItem());
        event.registerItem(ConduitStaffRenderer.CONDUIT_STAFF_EXTENSION, AddonItems.CONDUIT_STAFF.get());
        event.registerItem(OriginalConduitStaffRenderer.ORIGINAL_CONDUIT_STAFF_EXTENSION, AddonItems.ORIGINAL_CONDUIT_STAFF.get());

    }

    @SubscribeEvent
    public static void onRegisterItemDecorations(RegisterItemDecorationsEvent event){
        event.register(AddonItems.SONIC_BOOM_STAFF.get(), EnergyBarRenderer.DEFAULT);
        event.register(AddonItems.CONDUIT_STAFF.get(), EnergyBarRenderer.DEFAULT);
        event.register(AddonItems.ORIGINAL_CONDUIT_STAFF.get(), EnergyBarRenderer.DEFAULT);
    }

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event){
        event.registerAboveAll(AnvilCraftTofusThinking.of("tool_progress_hud"), ToolProgressHud::render);
    }
}
