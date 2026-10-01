package dev.anvilcraft.tofusthinking.network.toClient;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.api.magicSpell.CooldownInstance;
import dev.anvilcraft.tofusthinking.client.ClientCooldownCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record SyncAllCooldownsPacket(Map<String, CooldownInstance> cooldowns) implements CustomPacketPayload {
    public static final Type<SyncAllCooldownsPacket> TYPE =
            new Type<>(AnvilCraftTofusThinking.of("sync_all_cooldowns"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncAllCooldownsPacket> STREAM_CODEC =
            CustomPacketPayload.codec(SyncAllCooldownsPacket::write, SyncAllCooldownsPacket::new);

    public static final IPayloadHandler<SyncAllCooldownsPacket> HANDLER = SyncAllCooldownsPacket::handle;

    public SyncAllCooldownsPacket(RegistryFriendlyByteBuf buf) {
        this(buf.readMap(FriendlyByteBuf::readUtf, b -> new CooldownInstance(b.readInt(), b.readInt())));
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeMap(cooldowns, FriendlyByteBuf::writeUtf, (b, cd) -> {
            b.writeInt(cd.getTotal());
            b.writeInt(cd.getRemaining());
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(SyncAllCooldownsPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientCooldownCache.replaceAll(packet.cooldowns()));
    }
}
