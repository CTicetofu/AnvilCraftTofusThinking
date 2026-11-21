package dev.anvilcraft.tofusthinking.init.storage;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.saved.storage.SteelBarrelStorage;
import dev.dubhe.anvilcraft.init.registry.ModRegistryKeys;
import dev.dubhe.anvilcraft.saved.storage.BaseStorage;
import dev.dubhe.anvilcraft.saved.storage.IStorageType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.NonNull;

import java.util.UUID;
import java.util.function.Function;

public class AddonStorageTypes {
    private static final DeferredRegister<IStorageType<?>> REGISTER = DeferredRegister.create(
            ModRegistryKeys.STORAGE_TYPE,
            AnvilCraftTofusThinking.MOD_ID
    );

    public static final DeferredHolder<IStorageType<?>, IStorageType<SteelBarrelStorage>> STEEL_BARREL = AddonStorageTypes.of(
            "steel_barrel",
            SteelBarrelStorage::new,
            SteelBarrelStorage.class
    );

    private static <T extends BaseStorage<?>> DeferredHolder<IStorageType<?>, IStorageType<T>> of(
            String id,
            Function<UUID, T> factory,
            Class<T> clazz
    ) {
        DeferredHolder<IStorageType<?>, IStorageType<T>> holder = REGISTER.register(id, () -> new IStorageType<>() {
            @Override
            public @NonNull T newInstance(@NonNull UUID uuid) {
                return factory.apply(uuid);
            }

            @Override
            public @NonNull Class<T> clazz() {
                return clazz;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof IStorageType<?> type && this.clazz().equals(type.clazz());
            }

            @Override
            public int hashCode() {
                return this.clazz().hashCode();
            }
        });
        IStorageType.CLASS_MAP.put(clazz, holder);
        return holder;
    }

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
