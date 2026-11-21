package dev.anvilcraft.tofusthinking.client.init;

import com.mojang.blaze3d.platform.InputConstants;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.Lazy;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = AnvilCraftTofusThinking.MOD_ID, value = Dist.CLIENT)
public class AddonKeyMappings {
    public static final Lazy<KeyMapping> OPEN_ENDER_CHARM = register("open_ender_charm", KeyConflictContext.UNIVERSAL, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN);

    @SuppressWarnings("SameParameterValue")
    private static Lazy<KeyMapping> register(String name, KeyConflictContext context, InputConstants.Type type, int key) {
        return Lazy.of(() -> new KeyMapping("key.anvilcraft_tofus_thinking." + name, context, type, key, "key.categories.anvilcraft_tofus_thinking"));
    }

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(OPEN_ENDER_CHARM.get());
    }
}
