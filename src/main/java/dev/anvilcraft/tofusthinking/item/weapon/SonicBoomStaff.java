package dev.anvilcraft.tofusthinking.item.weapon;

import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.item.IToolProgress;
import dev.anvilcraft.tofusthinking.util.ItemUtil;
import dev.anvilcraft.tofusthinking.util.RayDetectionUtil;
import dev.anvilcraft.tofusthinking.util.TooltipUtil;
import dev.anvilcraft.tofusthinking.util.UnclassifiedUtil;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SonicBoomStaff extends Item implements IToolProgress {
    public static final int MAX_ENERGY = 16000000;

    public SonicBoomStaff(Properties properties) {
        super(properties.component(AddonComponents.MAX_ENERGY,MAX_ENERGY).attributes(createAttributes()).component(AddonComponents.IS_ACTIVE,false));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 2400;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if(!itemstack.getOrDefault(AddonComponents.IS_ACTIVE,false) || !ItemUtil.hasEnoughEnergy(itemstack,this.getSingleNeedEnergy())){return InteractionResultHolder.fail(itemstack);}
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int remainingUseDuration) {
        if(getUseDuration(stack,livingEntity) == remainingUseDuration){
            livingEntity.level().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.WARDEN_SONIC_CHARGE, livingEntity.getSoundSource(), 1, 1);
        }
    }

    @Override
    public float getToolProgress(LivingEntity entity, ItemStack stack) {
        if(entity.getUseItem() == stack){
            float f = getCastTimeDiscount(stack,entity);
            int useTick = entity.getTicksUsingItem();
            if(useTick <= 10 * f){return useTick * 0.1F * f;}
            return Math.min(1F,(useTick - 10 * f)/(40 * f));
        }
        return 0;
    }

    @Override
    public int getHudColor(Player player, ItemStack stack) {
        if(player.getTicksUsingItem() <= 10){return 0xFFFFFF00;}
        return IToolProgress.super.getHudColor(player, stack);
    }

    public int getSingleNeedEnergy(){
        return 40000;
    }

    public static float getCastTimeDiscount(ItemStack stack, LivingEntity shooter) {
        float f = EnchantmentHelper.modifyCrossbowChargingTime(stack, shooter, 2F);
        return Math.clamp(f * 0.5F,0.25F,1F);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity, int timeCharged) {
        int useTick = getUseDuration(stack,livingEntity) - timeCharged;
        if(!level.isClientSide){
            if(!stack.getOrDefault(AddonComponents.IS_ACTIVE,false)){return;}
            float discount = getCastTimeDiscount(stack,livingEntity);
            if(livingEntity instanceof Player player){
                int cooldown = useTick <= 10 ? 10 : (int)(30 * discount);
                player.getCooldowns().addCooldown(this, cooldown);
            }
            if(useTick <= 10 * discount){return;}
            float progress = this.getToolProgress(livingEntity,stack);
            int consumption = (int) (progress * this.getSingleNeedEnergy());
            if(ItemUtil.consumeEnergy(livingEntity,stack,consumption) && level instanceof ServerLevel serverLevel){
                performSonic(stack, serverLevel,livingEntity,progress);
            }
        }
    }

    private void performSonic(@NotNull ItemStack stack, @NotNull ServerLevel level, @NotNull LivingEntity livingEntity, float progress){
        float damage = 20 * progress * progress;
        float range = 2 + 14 * progress;
        int count = EnchantmentHelper.processProjectileCount(level,stack,livingEntity,1);
        float spread = EnchantmentHelper.processProjectileSpread(level,stack,livingEntity,0) * Mth.DEG_TO_RAD;
        float startYaw = -(count -1) * spread / 2;
        for (int c = 0; c < count; c++) {
            float yaw = startYaw + c * spread;
            Vec3 start = livingEntity.getEyePosition();
            Vec3 forward = livingEntity.getLookAngle().yRot(yaw);
            List<EntityHitResult> hitResults = RayDetectionUtil.create(level).excludeEntity(livingEntity).start(start.subtract(forward)).end(start.add(forward.scale(range))).ignoreBlocks(true).extendInflation(0.8F).raycastAllEntities();
            hitResults.forEach(entityHitResult -> {
                Entity target = entityHitResult.getEntity();
                if(target instanceof LivingEntity || target instanceof PartEntity<?>){
                    target.hurt(livingEntity.damageSources().sonicBoom(livingEntity),damage);
                }
            });
            for (int i = 1; i <= range; i++) {
                Vec3 position = start.add(forward.scale(i));
                UnclassifiedUtil.spawnParticles(level, ParticleTypes.SONIC_BOOM, position.x, position.y, position.z, 1, 0.0, 0.0, 0.0, 0.0, false);
            }
        }

        livingEntity.level().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.WARDEN_SONIC_BOOM, livingEntity.getSoundSource(), 1, 1);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        return true;
    }

    @Override
    public void postHurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        target.addEffect(new MobEffectInstance(MobEffects.DARKNESS,200,0));
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 8, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -3, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }
    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(TooltipUtil.getItemEnergyTooltip(stack, Screen.hasShiftDown()));
        tooltipComponents.add(TooltipUtil.getItemNeedEnergy(getSingleNeedEnergy(), Screen.hasShiftDown()));
    }
}
