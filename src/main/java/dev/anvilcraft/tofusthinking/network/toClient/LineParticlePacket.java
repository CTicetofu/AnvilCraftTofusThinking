package dev.anvilcraft.tofusthinking.network.toClient;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

public class LineParticlePacket implements CustomPacketPayload {
    public static final Type<LineParticlePacket> TYPE = new Type<>(AnvilCraftTofusThinking.of("line_particle"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LineParticlePacket> STREAM_CODEC = StreamCodec.ofMember(LineParticlePacket::encode, LineParticlePacket::new);
    public static final IPayloadHandler<LineParticlePacket> HANDLER = LineParticlePacket::handle;
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private final float x;
    private final float y;
    private final float z;
    private final float offsetX;
    private final float offsetY;
    private final float offsetZ;
    private final float motionX;
    private final float motionY;
    private final float motionZ;
    private final int count;
    private final ParticleOptions particle;

    public LineParticlePacket(float x, float y, float z, float offsetX, float offsetY, float offsetZ, float motionX, float motionY, float motionZ, int count, ParticleOptions particle) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.count = count;
        this.particle = particle;
    }
    public LineParticlePacket(RegistryFriendlyByteBuf buf){
        this.x = buf.readFloat();
        this.y = buf.readFloat();
        this.z = buf.readFloat();
        this.offsetX = buf.readFloat();
        this.offsetY = buf.readFloat();
        this.offsetZ = buf.readFloat();
        this.motionX = buf.readFloat();
        this.motionY = buf.readFloat();
        this.motionZ = buf.readFloat();
        this.count = buf.readVarInt();
        this.particle = ParticleTypes.STREAM_CODEC.decode(buf);
    }

    private void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeFloat(this.x);
        buffer.writeFloat(this.y);
        buffer.writeFloat(this.z);
        buffer.writeFloat(this.offsetX);
        buffer.writeFloat(this.offsetY);
        buffer.writeFloat(this.offsetZ);
        buffer.writeFloat(this.motionX);
        buffer.writeFloat(this.motionY);
        buffer.writeFloat(this.motionZ);
        buffer.writeVarInt(this.count);
        ParticleTypes.STREAM_CODEC.encode(buffer, this.particle);
    }

    public static void handle(LineParticlePacket packet, IPayloadContext context){
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if(level == null){return;}
            Vec3 pos = new Vec3(packet.x, packet.y, packet.z);
            Vec3 offset = new Vec3(packet.offsetX,packet.offsetY,packet.offsetZ);
            for (int i = 0; i < packet.count; i++) {
                Vec3 spawnPos = pos.add(offset.scale(i));
                level.addParticle(packet.particle,spawnPos.x,spawnPos.y,spawnPos.z,packet.motionX,packet.motionY,packet.motionZ);
            }
        });
    }
}
