package dev.anvilcraft.tofusthinking.init;

import com.mojang.serialization.Codec;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.api.magicSpell.CooldownInstance;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AddonAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, AnvilCraftTofusThinking.MOD_ID);

    public static final Supplier<AttachmentType<Map<String, CooldownInstance>>> COOLDOWNS =
            ATTACHMENTS.register("cooldowns", () ->
                    AttachmentType.builder((Supplier<Map<String, CooldownInstance>>) HashMap::new)
                            .serialize(Codec.unboundedMap(Codec.STRING, CooldownInstance.CODEC).fieldOf("cooldowns").codec())
                            .copyOnDeath()
                            .build());

    public static void register(IEventBus eventBus) {
        ATTACHMENTS.register(eventBus);
    }
}
