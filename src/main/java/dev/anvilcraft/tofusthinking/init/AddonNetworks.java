package dev.anvilcraft.tofusthinking.init;

import dev.anvilcraft.tofusthinking.network.toClient.CenterParticlePacket;
import dev.anvilcraft.tofusthinking.network.toClient.SimpleNumberInitPacket;
import dev.anvilcraft.tofusthinking.network.toServer.SimpleNumberUpdatePacket;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class AddonNetworks {
    public static void init(PayloadRegistrar registrar) {
        registrar.playToClient(
                SimpleNumberInitPacket.TYPE,
                SimpleNumberInitPacket.STREAM_CODEC,
                SimpleNumberInitPacket.HANDLER
        );
        registrar.playToClient(
                CenterParticlePacket.TYPE,
                CenterParticlePacket.STREAM_CODEC,
                CenterParticlePacket.HANDLER
        );
        registrar.playToServer(
                SimpleNumberUpdatePacket.TYPE,
                SimpleNumberUpdatePacket.STREAM_CODEC,
                SimpleNumberUpdatePacket.HANDLER
        );
    }
}
