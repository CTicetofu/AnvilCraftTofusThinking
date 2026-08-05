package dev.anvilcraft.tofusthinking.event;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.api.energy.FEEnergyTool;
import dev.anvilcraft.tofusthinking.block.entity.SmartPowerConverterBlockEntity;
import dev.anvilcraft.tofusthinking.init.block.AddonBlockEntities;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import dev.dubhe.anvilcraft.block.entity.PowerConverterBlockEntity;
import dev.dubhe.anvilcraft.init.block.ModBlockEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.List;
@EventBusSubscriber(modid = AnvilCraftTofusThinking.MOD_ID)
public class AddonCapabilitiesEventListener {
    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        List.of(
                AddonBlockEntities.FOOD_GENERATOR.get()
        ).forEach(type -> event.registerBlockEntity(
                        Capabilities.ItemHandler.BLOCK,
                        type,
                        (be, side) -> be.getItemHandler()
                )
        );
        List.of(
                AddonBlockEntities.FOOD_GENERATOR.get()
        ).forEach(type -> event.registerBlockEntity(
                        Capabilities.FluidHandler.BLOCK,
                        type,
                        (be, side) -> be.getFluidHandler()
                ));

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                AddonBlockEntities.SMART_POWER_CONVERTER.get(),
                SmartPowerConverterBlockEntity::getEnergyStorage
        );
        event.registerItem(Capabilities.EnergyStorage.ITEM,  (stack, ctx) -> new FEEnergyTool(stack),AddonItems.SONIC_BOOM_STAFF);
        event.registerItem(Capabilities.EnergyStorage.ITEM,  (stack, ctx) -> new FEEnergyTool(stack),AddonItems.CONDUIT_STAFF);
    }
}
