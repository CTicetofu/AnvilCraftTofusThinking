package dev.anvilcraft.tofusthinking.util;

import dev.anvilcraft.tofusthinking.network.toClient.CenterParticlePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

//我英语真的不好
public class UnclassifiedUtil {
    //copy from 原版命令，因此这是个服务端方法
    public static void spawnParticles(Level level, ParticleOptions particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed, boolean force) {
        if(level.getServer() == null){return;}
        level.getServer().getPlayerList().getPlayers().forEach(player -> ((ServerLevel) level).sendParticles(player, particle, force, x, y, z, count, deltaX, deltaY, deltaZ, speed));
    }

    public static void spawnCenterParticles(Level level, ParticleOptions particle,double x, double y, double z, float distance, float distanceOffset, float speed, float speedOffset, int count, boolean force){
        if(level.getServer() == null){return;}
        Vec3 pos = new Vec3(x,y,z);
        level.getServer().getPlayerList().getPlayers().forEach(player -> {
            if(isShouldPlayerReceive(level,player,pos,force ? 512 : 32)){
                PacketDistributor.sendToPlayer(player,new CenterParticlePacket((float) x, (float) y, (float) z, distance, distanceOffset, speed, speedOffset, count, particle));
            }
        });
    }

    public static boolean isShouldPlayerReceive(Level level, Player player, Vec3 pos, double distance){
        if (player.level() != level) {
            return false;
        } else {
            BlockPos blockpos = player.blockPosition();
            return blockpos.closerToCenterThan(pos, distance);
        }
    }
}
