package dev.anvilcraft.tofusthinking.client;

import dev.anvilcraft.tofusthinking.api.magicSpell.CooldownInstance;

import java.util.HashMap;
import java.util.Map;

public class ClientCooldownCache {
    private static final Map<String, CooldownInstance> COOLDOWNS = new HashMap<>();

    public static void replaceAll(Map<String, CooldownInstance> map) {
        COOLDOWNS.clear();
        COOLDOWNS.putAll(map);
    }
    public static void set(String skillId, int total, int remaining) {
        COOLDOWNS.put(skillId, new CooldownInstance(total, remaining));
    }

    public static void clear() {
        COOLDOWNS.clear();
    }

    public static boolean isOnCooldown(String skillId) {
        return COOLDOWNS.containsKey(skillId);
    }

    public static float getProgress(String skillId) {
        CooldownInstance cd = COOLDOWNS.get(skillId);
        return cd == null ? 1.0F : cd.getProgress();
    }

    public static void tickClient() {
        COOLDOWNS.values().removeIf(cd -> !cd.tick());
    }
}
