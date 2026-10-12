package dev.anvilcraft.tofusthinking.network.toClient;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jspecify.annotations.NonNull;

public class RespawnPacket implements CustomPacketPayload {
    public static final RespawnPacket INSTANCE = new RespawnPacket();
    public static final Type<RespawnPacket> TYPE = new Type<>(AnvilCraftTofusThinking.of("respawn"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RespawnPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final IPayloadHandler<RespawnPacket> HANDLER = RespawnPacket::handler;

    public static void handler(RespawnPacket packet, IPayloadContext context){
        context.enqueueWork(() -> {
            var player = context.player();
            player.respawn();
        });
    }
    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
