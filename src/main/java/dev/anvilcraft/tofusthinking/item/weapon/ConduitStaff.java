package dev.anvilcraft.tofusthinking.item.weapon;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.init.entity.AddonDamageTypes;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.util.ItemUtil;
import dev.anvilcraft.tofusthinking.util.RayDetectionUtil;
import dev.anvilcraft.tofusthinking.util.TooltipUtil;
import dev.anvilcraft.tofusthinking.util.UnclassifiedUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConduitStaff extends Item {
    public static final int MAX_ENERGY = 16000000;
    public static ResourceLocation KNOCKBACK_ID = AnvilCraftTofusThinking.of("knockback");
    public ConduitStaff(Properties properties) {
        super(properties.component(AddonComponents.MAX_ENERGY,MAX_ENERGY).component(AddonComponents.AUTO_HUNT,false).attributes(createAttributes()));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 72000;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if(itemstack.getOrDefault(AddonComponents.AUTO_HUNT,false)){return InteractionResultHolder.fail(itemstack);}
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    public static boolean isAutoHunting(ItemStack stack){
        return stack.getOrDefault(AddonComponents.AUTO_HUNT,false);
    }

    public static int getNeedEnergy(boolean isAuto){
        return isAuto ? 20000 : 10000;
    }

    @Override
    public boolean overrideOtherStackedOnMe(@NotNull ItemStack stack, @NotNull ItemStack other, @NotNull Slot slot, @NotNull ClickAction action, @NotNull Player player, @NotNull SlotAccess access) {
        if(other.isEmpty() && action == ClickAction.SECONDARY){
            boolean auto = !isAutoHunting(stack);
            stack.set(AddonComponents.AUTO_HUNT,auto);
            if(player.level().isClientSide){
                SoundEvent soundEvent = auto ? SoundEvents.CONDUIT_ACTIVATE : SoundEvents.CONDUIT_DEACTIVATE;
                player.level().playLocalSound(player, soundEvent,player.getSoundSource(),1,1);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int remainingUseDuration) {
        int useTick = getUseDuration(stack,livingEntity) - remainingUseDuration + 1;
        if(useTick % 20 == 0 && !level.isClientSide && ItemUtil.consumeEnergy(livingEntity,stack,getNeedEnergy(false))){
            if(isAutoHunting(stack)){return;}
            HitResult hitResult = RayDetectionUtil.create(level).endByLook(livingEntity,10).raycast();
            Entity entity = null;
            if(hitResult instanceof EntityHitResult entityHitResult){
                entity = entityHitResult.getEntity();
            }
            dealAttack(level,livingEntity,stack,entity,hitResult.getLocation());
        }
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if(!level.isClientSide && entity.tickCount % 40 == 0 && entity instanceof Player player){
            if(player.isInWaterRainOrBubble()){
                ItemUtil.addEnergy(stack,20000);
                player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER,400,0,false,false,true));
            }
            if(!isAutoHunting(stack)){return;}
            if(ItemUtil.hasEnoughEnergy(stack,getNeedEnergy(true))){return;}
            if(isSelected || player.getOffhandItem() == stack){
                List<Monster> monsters = level.getEntitiesOfClass(Monster.class,new AABB(player.blockPosition()).inflate(12), monster -> !monster.isInvulnerable() && monster.isAlive());
                if(!monsters.isEmpty() && ItemUtil.consumeEnergy(player,stack,getNeedEnergy(true))){
                    Monster monster = monsters.getFirst();
                    dealAttack(level,player,stack,monster,monster.position().add(0,0.2,0));
                }
            }
        }
    }

    private static void dealAttack(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, Entity entity, Vec3 pos){
        if(entity instanceof LivingEntity living){
            living.addEffect(new MobEffectInstance(MobEffects.GLOWING,20,0,false,false,true));
        }
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos.subtract(1.5, 1, 1.5), pos.add(1.5, 1, 1.5)))) {
            if(target == livingEntity || livingEntity.isAlliedTo(target)){continue;}
            if(target instanceof Player && target != entity){continue;}
            target.hurt(AddonDamageTypes.source(DamageTypes.MAGIC,level,null,livingEntity),6);
            target.invulnerableTime = 0;
        }
        UnclassifiedUtil.spawnCenterParticles(level,ParticleTypes.NAUTILUS,pos.x,pos.y,pos.z,2,0.5F,4F,0.4F,30,false);

        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.CONDUIT_ATTACK_TARGET, livingEntity.getSoundSource(), 1.6F, 1);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 7, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -3, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_KNOCKBACK,
                        new AttributeModifier(KNOCKBACK_ID,1, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }
    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(TooltipUtil.getItemEnergyTooltip(stack, Screen.hasShiftDown()));

        boolean auto = isAutoHunting(stack);
        tooltipComponents.add(TooltipUtil.getItemNeedEnergy(getNeedEnergy(auto), Screen.hasShiftDown()));
        tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.right_switch_in_inventory",Component.translatable("tooltip.anvilcraft_tofus_thinking.auto_hunting_mode").withStyle(auto ? ChatFormatting.AQUA : ChatFormatting.STRIKETHROUGH)).withStyle(ChatFormatting.GRAY));
        if(auto){
            tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.conduit_staff_auto").withStyle(ChatFormatting.GRAY));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.conduit_staff_normal").withStyle(ChatFormatting.GRAY));
        }
        tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.conduit_staff_recovery").withStyle(ChatFormatting.GRAY));
    }
}
