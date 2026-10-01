package dev.anvilcraft.tofusthinking.network.toClient;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.client.ClientCooldownCache;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

public record SyncCooldownPacket(String skillId, int total, int remaining) implements CustomPacketPayload {
    public static final Type<SyncCooldownPacket> TYPE =
            new Type<>(AnvilCraftTofusThinking.of("sync_cooldown"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncCooldownPacket> STREAM_CODEC =
            CustomPacketPayload.codec(SyncCooldownPacket::write, SyncCooldownPacket::new);

    public static final IPayloadHandler<SyncCooldownPacket> HANDLER = SyncCooldownPacket::handle;

    public SyncCooldownPacket(RegistryFriendlyByteBuf buf) {
        this(buf.readUtf(), buf.readInt(), buf.readInt());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(skillId);
        buf.writeInt(total);
        buf.writeInt(remaining);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(SyncCooldownPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientCooldownCache.set(packet.skillId(), packet.total(), packet.remaining()));
    }
}