package dev.anvilcraft.tofusthinking.item.weapon.StaffProcess;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class StaffContext {
    final Level level;
    final LivingEntity attacker;
    HitResult hitResult;
    List<LivingEntity> targets = new ArrayList<>();

    public StaffContext(Level level, LivingEntity attacker, HitResult hitResult) {
        this.level = level;
        this.attacker = attacker;
        this.hitResult = hitResult;
    }

    public Level getLevel() {
        return level;
    }

    public LivingEntity getAttacker() {
        return attacker;
    }

    public HitResult getHitResult() {
        return hitResult;
    }

    public void setTargets(List<LivingEntity> targets) {
        this.targets = targets;
    }
}
