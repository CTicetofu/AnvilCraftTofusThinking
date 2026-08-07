package dev.anvilcraft.tofusthinking.init.item;

import com.mojang.serialization.Codec;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public class AddonComponents {
    public static final DeferredRegister<DataComponentType<?>> DR = DeferredRegister.create(
            Registries.DATA_COMPONENT_TYPE, AnvilCraftTofusThinking.MOD_ID
    );
    public static final DataComponentType<Integer> NUTRITION_VALUE = register(
            "nutrition_lvalue",
            it -> it.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT)
    );
    public static final DataComponentType<Integer> PROGRESS = register(
            "progress",
            it -> it.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT)
    );
    public static final DataComponentType<Integer> EFFECT_TICK = register(
            "effect_tick",
            it -> it.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT)
    );
    public static final DataComponentType<Boolean> AUTO_HUNT = register(
            "auto_hunt",
            it -> it.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
    );
    public static final DataComponentType<Boolean> HAS_USED = register(
            "has_used",
            it -> it.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
    );
    public static final DataComponentType<Boolean> IS_ACTIVE = register(
            "is_active",
            it -> it.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
    );
    public static final DataComponentType<Byte> TYPE_NUMBER = register(
            "type_number",
            it -> it.persistent(Codec.BYTE).networkSynchronized(ByteBufCodecs.BYTE)
    );
    public static final DataComponentType<Integer> STORED_ENERGY = register(
            "stored_energy",
            (builder) -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT)
    );
    public static final DataComponentType<Integer> MAX_ENERGY = register(
            "max_energy",
            (builder) -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT)
    );
    private static <T> DataComponentType<T> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {
        var builder = DataComponentType.<T>builder();
        customizer.accept(builder);
        var componentType = builder.build();
        DR.register(name, () -> componentType);
        return componentType;
    }

    public static void register(IEventBus bus) {
        DR.register(bus);
    }
}
