package dev.anvilcraft.tofusthinking.attachment.magic;

import dev.anvilcraft.tofusthinking.api.magicSpell.CooldownInstance;
import dev.anvilcraft.tofusthinking.init.AddonAttachments;
import dev.anvilcraft.tofusthinking.network.toClient.SyncAllCooldownsPacket;
import dev.anvilcraft.tofusthinking.network.toClient.SyncCooldownPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

public class CooldownManager {

    public static void tick(ServerPlayer player) {
        Map<String, CooldownInstance> current = player.getData(AddonAttachments.COOLDOWNS);
        if (current.isEmpty()) return;
        current.values().removeIf(cd -> !cd.tick());
        player.setData(AddonAttachments.COOLDOWNS, current);
    }

    public static boolean isOnCooldown(Player player, String skillId) {
        return player.getData(AddonAttachments.COOLDOWNS).containsKey(skillId);
    }

    public static CooldownInstance getCooldown(Player player, String skillId) {
        return player.getData(AddonAttachments.COOLDOWNS).get(skillId);
    }

    public static float getCooldownProgress(Player player, String skillId) {
        CooldownInstance cd = getCooldown(player, skillId);
        return cd == null ? 1.0F : cd.getProgress();
    }

    public static void addCooldown(ServerPlayer player, String skillId, int durationTicks){
        Map<String, CooldownInstance> cooldowns = player.getData(AddonAttachments.COOLDOWNS);
        Map<String, CooldownInstance> updated = new HashMap<>(cooldowns);
        updated.put(skillId, new CooldownInstance(durationTicks, durationTicks));
        player.setData(AddonAttachments.COOLDOWNS, updated);

        PacketDistributor.sendToPlayer(player, new SyncCooldownPacket(skillId, durationTicks, durationTicks));
    }

    public static boolean tryCast(ServerPlayer player, String skillId, int durationTicks) {
        if (isOnCooldown(player, skillId)) return false;
        addCooldown(player, skillId, durationTicks);
        return true;
    }

    public static void syncAll(ServerPlayer player) {
        Map<String, CooldownInstance> cooldowns = player.getData(AddonAttachments.COOLDOWNS);
        if (cooldowns.isEmpty()) return;
        PacketDistributor.sendToPlayer(player, new SyncAllCooldownsPacket(new HashMap<>(cooldowns)));
    }
}
