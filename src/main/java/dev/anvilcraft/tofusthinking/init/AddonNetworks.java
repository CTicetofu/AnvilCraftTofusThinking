package dev.anvilcraft.tofusthinking.init;

import dev.anvilcraft.tofusthinking.network.toClient.*;
import dev.anvilcraft.tofusthinking.network.toServer.LeftClickPacket;
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
        registrar.playToClient(
                LineParticlePacket.TYPE,
                LineParticlePacket.STREAM_CODEC,
                LineParticlePacket.HANDLER
        );
        registrar.playToClient(
                SyncAllCooldownsPacket.TYPE,
                SyncAllCooldownsPacket.STREAM_CODEC,
                SyncAllCooldownsPacket.HANDLER
        );
        registrar.playToClient(
                SyncCooldownPacket.TYPE,
                SyncCooldownPacket.STREAM_CODEC,
                SyncCooldownPacket.HANDLER
        );
        registrar.playToServer(
                SimpleNumberUpdatePacket.TYPE,
                SimpleNumberUpdatePacket.STREAM_CODEC,
                SimpleNumberUpdatePacket.HANDLER
        );
        registrar.playToServer(
                LeftClickPacket.TYPE,
                LeftClickPacket.STREAM_CODEC,
                LeftClickPacket.HANDLER
        );
    }
}
