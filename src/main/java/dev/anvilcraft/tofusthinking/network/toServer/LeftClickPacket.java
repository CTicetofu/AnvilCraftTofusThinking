package dev.anvilcraft.tofusthinking.network.toServer;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.item.LeftClickAction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

public class LeftClickPacket implements CustomPacketPayload {
    private static final LeftClickPacket INSTANCE = new LeftClickPacket();
    public static final Type<LeftClickPacket> TYPE = new Type<>(AnvilCraftTofusThinking.of("left_click"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LeftClickPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final IPayloadHandler<LeftClickPacket> HANDLER = LeftClickPacket::handler;

    public static void handler(LeftClickPacket packet, IPayloadContext context){
        context.enqueueWork(() -> {
            var player = context.player();
            ItemStack stack = player.getMainHandItem();
            if(stack.getItem() instanceof LeftClickAction action){
                action.onServerClick(stack,player);
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}

    public static void sendToServer() {
        PacketDistributor.sendToServer(INSTANCE);
    }
}
