package dev.anvilcraft.tofusthinking.network.toServer;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.inventory.SimpleNumberConfigMenu;
import dev.dubhe.anvilcraft.util.Callback;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

public class SimpleNumberUpdatePacket implements CustomPacketPayload {
    public static final Type<SimpleNumberUpdatePacket> TYPE = new Type<>(AnvilCraftTofusThinking.of("simple_number_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SimpleNumberUpdatePacket> STREAM_CODEC = StreamCodec.ofMember(SimpleNumberUpdatePacket::encode, SimpleNumberUpdatePacket::new);
    public static final IPayloadHandler<SimpleNumberUpdatePacket> HANDLER = SimpleNumberUpdatePacket::handle;
    private final int power;
    public SimpleNumberUpdatePacket(int value){
        this.power = value;
    }

    public SimpleNumberUpdatePacket(RegistryFriendlyByteBuf buf){
        this.power = buf.readVarInt();
    }


    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(this.power);
    }

    public static void handle(SimpleNumberUpdatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if(player.containerMenu instanceof SimpleNumberConfigMenu menu){
                Callback<Integer> callback = menu.getCallback();
                if(callback != null){
                    callback.onValueChange(packet.power);
                }
            }
        });
    }
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
