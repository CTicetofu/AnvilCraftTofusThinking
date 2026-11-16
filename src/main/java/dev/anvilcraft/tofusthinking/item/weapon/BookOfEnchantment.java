package dev.anvilcraft.tofusthinking.item.weapon;

import dev.anvilcraft.tofusthinking.entity.ExtraDamageSource;
import dev.anvilcraft.tofusthinking.init.entity.AddonDamageTypes;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.util.ItemUtil;
import dev.anvilcraft.tofusthinking.util.RayDetectionUtil;
import dev.anvilcraft.tofusthinking.util.TooltipUtil;
import dev.anvilcraft.tofusthinking.util.UnclassifiedUtil;
import dev.dubhe.anvilcraft.api.item.ICapacitorChargeable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookOfEnchantment extends LeftSpellItem implements ICapacitorChargeable {
    public static final int MAX_ENERGY = 16000000;
    public static final int NEED = 10000;
    public static final String SKILL_ID = "Character";
    public BookOfEnchantment(Properties properties) {
        super(properties.component(AddonComponents.MAX_ENERGY, MAX_ENERGY).attributes(createAttributes()));
    }

    @Override
    public boolean canAttackBlock(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player) {
        return player.isShiftKeyDown();
    }

    @Override
    public String getSpellId() {
        return SKILL_ID;
    }

    @Override
    public int getCastEnergy(ItemStack stack) {
        return NEED;
    }

    @Override
    public boolean isFoil(@NonNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isEnchantable(@NonNull ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue(@NonNull ItemStack stack) {
        return 30;
    }

    @Override
    public boolean supportsEnchantment(@NonNull ItemStack stack, @NonNull Holder<Enchantment> enchantment) {
        return true;
    }

    @Override
    public boolean isDamaged(@NonNull ItemStack stack) {
        return getMaxEnergy(stack) > stack.getOrDefault(AddonComponents.STORED_ENERGY, 0);
    }

    @Override
    public boolean isRepairable(@NonNull ItemStack stack) {
        return isDamaged(stack);
    }

    @Override
    public float getXpRepairRatio(@NonNull ItemStack stack) {
        return 2F;
    }

    @Override
    public int getDamage(@NonNull ItemStack stack) {
        return (getMaxEnergy(stack) - stack.getOrDefault(AddonComponents.STORED_ENERGY, 0)) / 10000;
    }

    @Override
    public void setDamage(@NonNull ItemStack stack, int damage) {
        int current = this.getDamage(stack);
        if(current - damage > 0){stack.set(AddonComponents.STORED_ENERGY, getMaxEnergy(stack) - damage * 10000);}
    }

    public void shootCharacter(ItemStack stack, ServerPlayer player, Level level){
        if(this.isOnCooldown(player)){return;}
        this.addCooldown(player, 30);
        if(!(level instanceof ServerLevel serverLevel)){return;}
        int consume = EnchantmentHelper.processDurabilityChange(serverLevel, stack, this.getCastEnergy(stack)/1000);
        consume = Math.max(consume, 1);
        if(!ItemUtil.consumeEnergy(player, stack, consume * 1000)){return;}
        int shootCount = Mth.clamp(EnchantmentHelper.processProjectileCount(serverLevel, stack, player, 1), 1, 9);
        float spread = shootCount == 1 ? 0 : EnchantmentHelper.processProjectileSpread(serverLevel, stack, player, 0);
        int pierce = Math.max(0, EnchantmentHelper.getPiercingCount(serverLevel, stack, stack)) + 1;
        Vec3 startPoint = player.getEyePosition();
        Vec3 start = startPoint.subtract(0,0.1F,0).add(player.getLookAngle().scale(0.5F));
        float startYRot = (shootCount - 1) * 0.5F * spread;
        Set<Entity> entities = new HashSet<>(shootCount * pierce);
        for (int i = 0; i < shootCount; i++) {
            Vec3 vec3 = player.getUpVector(1.0F);
            Quaternionf quaternionf = (new Quaternionf()).setAngleAxis((startYRot - i * spread) * Mth.DEG_TO_RAD, vec3.x, vec3.y, vec3.z);
            Vec3 vec31 = player.getViewVector(1.0F);
            Vector3f vector3f = vec31.toVector3f().rotate(quaternionf);
            Vec3 forward = new Vec3(vector3f);
            Vec3 end = null;
            RayDetectionUtil rayDetection = RayDetectionUtil.create(serverLevel).startFromEntity(player).excludeEntity(player).end(startPoint.add(forward.scale(32)));
            Vec3 tryV = rayDetection.raycast().getLocation();
            List<EntityHitResult> hitResults = rayDetection.raycastAllEntities();
            int canHits = pierce;
            for (EntityHitResult hitResult : hitResults){
                Entity target = hitResult.getEntity();
                if(target.isAlliedTo(player) || player.isAlliedTo(target) || entities.contains(target)){continue;}
                entities.add(target);
                end = hitResult.getLocation();
                if(--canHits <= 0){break;}
            }
            if(end == null){end = tryV;}
            if(end.subtract(startPoint).length() > 0.5){
                Vec3 offset = end.subtract(start);
                int count = (int) (offset.length() * 2);
                Vec3 norOffset = offset.normalize().scale(0.5F);
                UnclassifiedUtil.spawnLineParticles(level, ParticleTypes.ENCHANT, start.x, start.y, start.z, norOffset.x, norOffset.y, norOffset.z, norOffset.x, norOffset.y, norOffset.z, count, false);
            }
        }
        float baseAmount = this.getBaseDamage(player, stack);
        for(Entity entity : entities){
            float amount = baseAmount;
            ExtraDamageSource source = AddonDamageTypes.character(serverLevel, null, player).withDirect();
            amount = EnchantmentHelper.modifyDamage(serverLevel, stack, entity, source, amount);
            float protection = Mth.clamp(EnchantmentHelper.modifyArmorEffectiveness(serverLevel, stack, entity, source, 1), 0.0F, 1.0F);
            if(protection < 1){
                source.withIncoming(event -> event.getContainer().addModifier(DamageContainer.Reduction.ENCHANTMENTS,(container, v) -> lowMagicAbsorb(v, protection)));
            }
            entity.hurt(source, amount);
            if(entity instanceof LivingEntity living){
                float f = (float)player.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
                f = EnchantmentHelper.modifyKnockback(serverLevel, stack, living, source, f) * 0.7F;
                int loyalty = Mth.clamp(EnchantmentHelper.getTridentReturnToOwnerAcceleration(serverLevel, stack, entity), 0, 127);
                if(loyalty > 0){
                    f = Math.max(Math.abs(f),0.5F);
                    f *= -loyalty * 0.8F;
                }
                if(f != 0){
                    Vec3 knock = entity.position().subtract(player.position()).normalize();
                    living.hurtMarked = true;
                    if(f > 0){
                        living.knockback(f, -knock.x, -knock.z);
                    } else {
                        f = Math.max(f, -3.6F);
                        living.knockback(f, knock.x, knock.z);
                        living.setDeltaMovement(knock.scale(f * 0.25F));
                    }
                }
            }
            EnchantmentHelper.doPostAttackEffects(serverLevel, entity, source);
        }
        if(!player.isSilent()){
            level.playSound(null,player.getX(),player.getY(),player.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE,player.getSoundSource(),1F,1F);
        }
    }

    @Override
    public boolean overrideStackedOnOther(@NonNull ItemStack stack, @NonNull Slot slot, @NonNull ClickAction action, @NonNull Player player) {
        if(slot.allowModification(player) && action == ClickAction.SECONDARY && player.containerMenu.getCarried() == stack){
            ItemStack other = slot.getItem();
            if(other.isEmpty() || other.is(this)){return false;}
            ItemStack copy = stack.copy();
            DataComponentType<ItemEnchantments> type = other.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS;
            ItemEnchantments enchantments = other.get(type);
            if(enchantments != null && !enchantments.isEmpty()){
                int total = 0;
                for (Holder<Enchantment> holder : enchantments.keySet()){
                    total += enchantments.getLevel(holder);
                }
                int damaged = this.getDamage(copy);
                total *= 20;
                if(total / 2 <= this.getDamage(copy)){
                    this.setDamage(copy, Math.max(0, damaged - total));
                    other.remove(type);
                    if(player.level().isClientSide){
                        player.level().playLocalSound(player, SoundEvents.ENCHANTMENT_TABLE_USE ,player.getSoundSource(),1,1);
                    }
                    player.containerMenu.setCarried(copy);
                    return true;
                }
            }
        }
        return super.overrideStackedOnOther(stack, slot, action, player);
    }

    public static float lowMagicAbsorb(float amount, float less){
        return amount > 0 ? amount * less : amount;
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 5, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -3.33, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                ).build();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltips, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltips, flag);
        boolean shift = Screen.hasShiftDown();
        tooltips.add(TooltipUtil.getItemEnergyTooltip(stack, shift));
        tooltips.add(TooltipUtil.getItemNeedEnergy(this.getCastEnergy(stack), shift));
        tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thing.book_of_enchantment").withStyle(ChatFormatting.GRAY));
        String number = shift ? TooltipUtil.getFullFE(NEED * 20) : TooltipUtil.getSimpleFE(NEED * 20);
        tooltips.add(Component.translatable("tooltip.anvilcraft_tofus_thing.book_of_enchantment_charge", Component.literal(number).withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void onServerClick(ItemStack stack, Player player) {
        if(stack.getItem() != this){return;}
        this.shootCharacter(stack, (ServerPlayer)player, player.level());
    }
}
