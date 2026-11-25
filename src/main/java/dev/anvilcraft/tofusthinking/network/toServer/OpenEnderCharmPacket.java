package dev.anvilcraft.tofusthinking.network.toServer;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.attachment.magic.CooldownManager;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import dev.anvilcraft.tofusthinking.item.curio.EnderCharm;
import dev.dubhe.anvilcraft.inventory.PocketInventory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jspecify.annotations.NonNull;

import java.util.function.Predicate;

public class OpenEnderCharmPacket implements CustomPacketPayload {
    private static final OpenEnderCharmPacket INSTANCE = new OpenEnderCharmPacket();
    public static final Type<OpenEnderCharmPacket> TYPE = new Type<>(AnvilCraftTofusThinking.of("open_ender_charm"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenEnderCharmPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public static final Predicate<ItemStack> PREDICATE = stack -> stack.is(AddonItems.ENDER_CHARM);

    public static final IPayloadHandler<OpenEnderCharmPacket> HANDLER = OpenEnderCharmPacket::handler;
    public static void handler(OpenEnderCharmPacket packet, IPayloadContext context){
        context.enqueueWork(() -> {
            if(context.player() instanceof ServerPlayer player && !CooldownManager.isOnCooldown(player, EnderCharm.SKILL)){
                if(player.getInventory().contains(PREDICATE) || PocketInventory.get(player).hasAnyMatching(PREDICATE) || player.getEnderChestInventory().hasAnyMatching(PREDICATE) || AddonItems.ENDER_CHARM.get().isEquippedBy(player)){
                    EnderCharm.openEnderChest(player);
                }
            }
        });
    }
    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void sendToServer() {
        PacketDistributor.sendToServer(INSTANCE);
    }
}
