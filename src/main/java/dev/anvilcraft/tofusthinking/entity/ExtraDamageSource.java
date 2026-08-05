package dev.anvilcraft.tofusthinking.entity;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ExtraDamageSource extends DamageSource {

    private float extraHurtAmount = 0;
    private float extraHurtRate = 1;

    private float extraDamageAmount = 0;
    private float extraDamageRate = 1;

    private DeathAction onDeath = DeathAction.NONE;

    private boolean notBlock = false;

    public ExtraDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 damageSourcePosition) {
        super(type, directEntity, causingEntity, damageSourcePosition);
    }

    public ExtraDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        super(type, directEntity, causingEntity);
    }

    public ExtraDamageSource(Holder<DamageType> type, Vec3 damageSourcePosition) {
        super(type, damageSourcePosition);
    }

    public ExtraDamageSource(Holder<DamageType> type, @Nullable Entity entity) {
        super(type, entity);
    }

    public ExtraDamageSource(Holder<DamageType> type) {
        super(type);
    }

    public float getExtraHurtAmount() {
        return extraHurtAmount;
    }

    public float getExtraHurtRate() {
        return extraHurtRate;
    }

    public float getExtraDamageAmount() {
        return extraDamageAmount;
    }

    public float getExtraDamageAmountRate() {
        return extraDamageRate;
    }

    public DeathAction getOnDeath() {
        return onDeath;
    }

    public boolean isNotBlock() {
        return notBlock;
    }

    public ExtraDamageSource withHurtInfo(float amount, float rate){
        extraHurtAmount = amount;
        extraHurtRate = rate;
        return this;
    }

    public ExtraDamageSource withDamageInfo(float amount, float rate){
        extraDamageAmount = amount;
        extraDamageRate = rate;
        return this;
    }

    public ExtraDamageSource notCauseDeath(){
        onDeath = DeathAction.NOT_CAUSE_DEATH;
        return this;
    }

    public ExtraDamageSource justDie(){
        onDeath = DeathAction.JUST_DIE;
        return this;
    }

    public ExtraDamageSource withNotBlock(boolean b){
        notBlock = b;
        return this;
    }

    public enum DeathAction{
        NOT_CAUSE_DEATH,
        NONE,
        JUST_DIE
    }
}
