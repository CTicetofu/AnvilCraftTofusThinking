package dev.anvilcraft.tofusthinking.api.magicSpell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public final class CooldownInstance {
    private final int total;
    private int remaining;

    public CooldownInstance(int total, int remaining) {
        this.total = total;
        this.remaining = remaining;
    }

    public int getTotal() { return total; }
    public int getRemaining() { return remaining; }

    public boolean tick() {
        return --remaining > 0;
    }

    public float getProgress() {
        return total == 0 ? 1.0F : 1 - (float) remaining / total;
    }

    public static final Codec<CooldownInstance> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("total").forGetter(CooldownInstance::getTotal),
            Codec.INT.fieldOf("remaining").forGetter(CooldownInstance::getRemaining)
    ).apply(i, CooldownInstance::new));
}
