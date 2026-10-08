package dev.anvilcraft.tofusthinking.item.property.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ProjectileInfo(float base, String type) {
    public static final ProjectileInfo DEFAULT = new ProjectileInfo(0.05F,"NONE");
    public static final Codec<ProjectileInfo> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("base").forGetter(ProjectileInfo::base),
                    Codec.STRING.fieldOf("type").forGetter(ProjectileInfo::type)
            ).apply(instance, ProjectileInfo::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ProjectileInfo> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, ProjectileInfo::base,
            ByteBufCodecs.STRING_UTF8, ProjectileInfo::type,
            ProjectileInfo::new
    );

}
