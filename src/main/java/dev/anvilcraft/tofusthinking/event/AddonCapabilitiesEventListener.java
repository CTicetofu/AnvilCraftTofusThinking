package dev.anvilcraft.tofusthinking.event;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.api.energy.FEEnergyTool;
import dev.anvilcraft.tofusthinking.block.entity.SmartPowerConverterBlockEntity;
import dev.anvilcraft.tofusthinking.block.entity.MushroomCowInJarBlockEntity;
import dev.anvilcraft.tofusthinking.block.entity.SteelBarrelBlockEntity;
import dev.anvilcraft.tofusthinking.init.block.AddonBlockEntities;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import dev.dubhe.anvilcraft.block.entity.storage.StorageBlockEntity;
import dev.dubhe.anvilcraft.saved.storage.Storages;
import net.minecraft.core.Direction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.UUID;

@EventBusSubscriber(modid = AnvilCraftTofusThinking.MOD_ID)
public class AddonCapabilitiesEventListener {
    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                AddonBlockEntities.SMART_POWER_CONVERTER.get(),
                SmartPowerConverterBlockEntity::getEnergyStorage
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                AddonBlockEntities.ORIGINAL_CONDUIT.get(),
                (blockEntity, side) -> blockEntity.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                AddonBlockEntities.MUSHROOM_COW_IN_JAR.get(),
                (blockEntity, side) -> MushroomCowInJarBlockEntity.INFINITY_MILK
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                AddonBlockEntities.MUSHROOM_COW_IN_JAR.get(),
                (blockEntity, side) -> blockEntity.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                AddonBlockEntities.AUTO_GRINDSTONE.get(),
                (blockEntity, side) -> {
                    if(side == Direction.UP){return blockEntity.getHandler().top;}
                    if(side == Direction.DOWN){return blockEntity.getHandler();}
                    return blockEntity.getHandler().side;
                }
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                AddonBlockEntities.STEEL_BARREL.get(),
                AddonCapabilitiesEventListener::storageItemHandler
        );
        event.registerItem(Capabilities.EnergyStorage.ITEM,  (stack, ctx) -> new FEEnergyTool(stack),AddonItems.SONIC_BOOM_STAFF);
        event.registerItem(Capabilities.EnergyStorage.ITEM,  (stack, ctx) -> new FEEnergyTool(stack),AddonItems.GEM_STAFF);
        event.registerItem(Capabilities.EnergyStorage.ITEM,  (stack, ctx) -> new FEEnergyTool(stack),AddonItems.CONDUIT_STAFF);
        event.registerItem(Capabilities.EnergyStorage.ITEM,  (stack, ctx) -> new FEEnergyTool(stack),AddonItems.ORIGINAL_CONDUIT_STAFF);
        event.registerItem(Capabilities.EnergyStorage.ITEM,  (stack, ctx) -> new FEEnergyTool(stack),AddonItems.ELECTROMAGNETIC_CROSSBOW);
        event.registerItem(Capabilities.EnergyStorage.ITEM,  (stack, ctx) -> new FEEnergyTool(stack),AddonItems.BOOK_OF_ENCHANTMENT);
        event.registerItem(Capabilities.FluidHandler.ITEM,((o, unused) -> new MushroomCowInJarBlockEntity.InfinityMilkItem(o)), AddonBlocks.MUSHROOM_COW_IN_JAR.asItem());
    }

    private static IItemHandler storageItemHandler(StorageBlockEntity be, @Nullable Direction side) {
        UUID id = be.getId();
        if (id == null) {
            id = UUID.randomUUID();
            be.setId(id);
        }
        IItemHandler handler = Storages.get().getOrCreate(id, be.getStorageType().clazz()).getAvailableItems();
        if (be instanceof SteelBarrelBlockEntity crate) {
            crate.refreshDispose();
        }
        return handler;
    }
}
