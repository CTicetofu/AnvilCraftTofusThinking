package dev.anvilcraft.tofusthinking.network.toClient;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.client.gui.screen.SimpleNumberConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

public class SimpleNumberInitPacket implements CustomPacketPayload {
    public static final Type<SimpleNumberInitPacket> TYPE = new Type<>(AnvilCraftTofusThinking.of("simple_number_init"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SimpleNumberInitPacket> STREAM_CODEC = StreamCodec.ofMember(SimpleNumberInitPacket::encode, SimpleNumberInitPacket::new);
    public static final IPayloadHandler<SimpleNumberInitPacket> HANDLER = SimpleNumberInitPacket::handle;
    private final int power;
    private final int min;
    private final int max;
    public SimpleNumberInitPacket(int value){
        this.power = value;
        this.min = 0;
        this.max = 4096;
    }
    public SimpleNumberInitPacket(int value, int min, int max){
        this.power = value;
        this.min = min;
        this.max = max;
    }

    public SimpleNumberInitPacket(RegistryFriendlyByteBuf buf){
        int[] ints = buf.readVarIntArray();
        this.power = ints[0];
        this.min = ints[1];
        this.max = ints[2];
    }


    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeVarIntArray(new int[]{power, min, max});
    }

    public static void handle(SimpleNumberInitPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(Minecraft.getInstance().screen instanceof SimpleNumberConfigScreen screen)) return;
            screen.setRange(packet.min,packet.max);
            screen.setNumberWithValue(packet.power);
        });
    }
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
