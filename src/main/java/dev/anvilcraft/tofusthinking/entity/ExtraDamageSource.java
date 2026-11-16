package dev.anvilcraft.tofusthinking.entity;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ExtraDamageSource extends DamageSource {
    private Consumer<LivingIncomingDamageEvent> onLivingIncoming = null;
    private Consumer<LivingDamageEvent> onLivingDamage = null;

    private DeathAction onDeath = DeathAction.NONE;

    private boolean notBlock = false;

    private boolean direct = false;

    public ExtraDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 damageSourcePosition) {
        super(type, directEntity, causingEntity, damageSourcePosition);
    }

    public ExtraDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        super(type, directEntity, causingEntity);
    }

    @Override
    public boolean isDirect() {
        return direct || super.isDirect();
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

    public Consumer<LivingIncomingDamageEvent> getOnLivingIncoming() {
        return onLivingIncoming;
    }

    public Consumer<LivingDamageEvent> getOnLivingDamage(){
        return onLivingDamage;
    }

    public DeathAction getOnDeath() {
        return onDeath;
    }

    public boolean isNotBlock() {
        return notBlock;
    }

    public ExtraDamageSource withIncoming(Consumer<LivingIncomingDamageEvent> eventConsumer){
        this.onLivingIncoming = eventConsumer;
        return this;
    }

    public ExtraDamageSource withDamage(Consumer<LivingDamageEvent> eventConsumer){
        this.onLivingDamage = eventConsumer;
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

    public ExtraDamageSource withDirect(){
        this.direct = true;
        return this;
    }

    public enum DeathAction{
        NOT_CAUSE_DEATH,
        NONE,
        JUST_DIE
    }
}
