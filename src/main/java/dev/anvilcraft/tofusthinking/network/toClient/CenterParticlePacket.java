package dev.anvilcraft.tofusthinking.network.toClient;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

public class CenterParticlePacket implements CustomPacketPayload {
    public static final Type<CenterParticlePacket> TYPE = new Type<>(AnvilCraftTofusThinking.of("center_particle"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CenterParticlePacket> STREAM_CODEC = StreamCodec.ofMember(CenterParticlePacket::encode, CenterParticlePacket::new);
    public static final IPayloadHandler<CenterParticlePacket> HANDLER = CenterParticlePacket::handle;

    private final float x;
    private final float y;
    private final float z;
    private final float distance;
    private final float distanceOffset;
    private final float speed;
    private final float speedOffset;
    private final int count;
    private final ParticleOptions particle;

    public CenterParticlePacket(float x, float y, float z, float distance, float distanceOffset, float speed, float speedOffset, int count, ParticleOptions particle) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.distance = distance;
        this.distanceOffset = distanceOffset;
        this.speed = speed;
        this.speedOffset = speedOffset;
        this.count = count;
        this.particle = particle;
    }

    public CenterParticlePacket(RegistryFriendlyByteBuf buf){
        this.x = buf.readFloat();
        this.y = buf.readFloat();
        this.z = buf.readFloat();
        this.distance = buf.readFloat();
        this.distanceOffset = buf.readFloat();
        this.speed = buf.readFloat();
        this.speedOffset = buf.readFloat();
        this.count = buf.readVarInt();
        this.particle = ParticleTypes.STREAM_CODEC.decode(buf);
    }

    private void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeFloat(this.x);
        buffer.writeFloat(this.y);
        buffer.writeFloat(this.z);
        buffer.writeFloat(this.distance);
        buffer.writeFloat(this.distanceOffset);
        buffer.writeFloat(this.speed);
        buffer.writeFloat(this.speedOffset);
        buffer.writeVarInt(this.count);
        ParticleTypes.STREAM_CODEC.encode(buffer, this.particle);
    }

    public static void handle(CenterParticlePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if(level == null){return;}
            RandomSource randomsource = level.random;
            Vec3 pos = new Vec3(packet.x, packet.y, packet.z);
            for (int i = 0; i < packet.count; i++) {
                Vec3 offsetPos = EntityUtil.calculateViewVector(randomsource.nextInt(360),randomsource.nextInt(-90,90));
                Vec3 spawnPos = pos.add(offsetPos.scale(packet.distance + packet.distanceOffset * randomsource.nextFloat()));
                Vec3 motion = offsetPos.scale(-1).scale(packet.speed + packet.speedOffset * randomsource.nextFloat());
                level.addParticle(packet.particle,spawnPos.x,spawnPos.y,spawnPos.z,motion.x,motion.y,motion.z);
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}
}
