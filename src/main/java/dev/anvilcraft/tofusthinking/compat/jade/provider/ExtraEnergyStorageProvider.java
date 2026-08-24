package dev.anvilcraft.tofusthinking.compat.jade.provider;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.api.energy.IExtraEnergyStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.Capabilities;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.view.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ExtraEnergyStorageProvider implements IServerExtensionProvider<CompoundTag>, IClientExtensionProvider<CompoundTag, EnergyView> {
    public static final ExtraEnergyStorageProvider INSTANCE = new ExtraEnergyStorageProvider();

    @Override
    public ResourceLocation getUid() {
        return AnvilCraftTofusThinking.of("extra_energy_storage");
    }

    @Override
    public List<ClientViewGroup<EnergyView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<CompoundTag>> groups) {
        return groups.stream().map($ -> {
            String unit = $.getExtraData().getString("Unit");
            return new ClientViewGroup<>($.views.stream()
                    .map(tag -> EnergyView.read(tag,unit))
                    .filter(Objects::nonNull)
                    .toList()
            );
        }).toList();
    }

    @Nullable
    @Override
    public List<ViewGroup<CompoundTag>> getGroups(Accessor<?> accessor) {
        if(accessor instanceof BlockAccessor blockAccessor){
            if (accessor.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, blockAccessor.getPosition(), null) instanceof IExtraEnergyStorage storage) {
                CompoundTag tag = new CompoundTag();
                tag.putLong("Cur", storage.getRealEnergyStored());
                tag.putLong("Capacity", storage.getRealMaxEnergyStored());
                ViewGroup<CompoundTag> group = new ViewGroup<>(List.of(tag));
                group.getExtraData().putString("Unit", "FE");
                return List.of(group);
            }
        }
        return null;
    }

    @Override
    public boolean shouldRequestData(Accessor<?> accessor) {
        if(accessor instanceof BlockAccessor blockAccessor){
            return accessor.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, blockAccessor.getPosition(), null) instanceof IExtraEnergyStorage;
        }
        return false;
    }
}
