package dev.anvilcraft.tofusthinking.anvil;

import dev.anvilcraft.tofusthinking.block.OriginalConduitBlock;
import dev.anvilcraft.tofusthinking.block.entity.OriginalConduitBlockEntity;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.init.entity.AddonDamageTypes;
import dev.anvilcraft.tofusthinking.util.EntityUtil;
import dev.dubhe.anvilcraft.api.anvil.IAnvilBehavior;
import dev.dubhe.anvilcraft.api.event.AnvilEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

//处理各种******的情况,尝试杜绝刷物品
public class RewindLivingEntityBehavior implements IAnvilBehavior {
    @Override
    public boolean handle(@NotNull Level level, @NotNull BlockPos hitBlockPos, @NotNull BlockState hitBlockState, float fallDistance, AnvilEvent.@NotNull OnLand event) {
        if (!(level instanceof ServerLevel serverLevel)) return false;
        BlockPos pos = hitBlockPos.below();
        BlockState state = serverLevel.getBlockState(pos);
        if(!state.is(AddonBlocks.ORIGINAL_CONDUIT.get()) || !state.getValue(OriginalConduitBlock.OPEN)){return false;}
        if(serverLevel.getBlockEntity(pos) instanceof OriginalConduitBlockEntity blockEntity && blockEntity.getExecuteCooldown() <= 1000){
            for (LivingEntity entity:serverLevel.getEntitiesOfClass(LivingEntity.class,new AABB(hitBlockPos))){
                executeLivingEntity(entity);
            }
            blockEntity.resetExecuteCooldown();
        }
        return true;
    }

    public static void executeLivingEntity(LivingEntity entity){
        if(entity instanceof Player player){
            if(player.isCreative()){return;}
            dropEquipment(player);
            dropCurios(player,true);
            slayPlayer(player);
        } else if(entity.getType().is(Tags.EntityTypes.BOSSES)){
            hurtPowerfulEntity(entity);
        } else {
            if(entity instanceof Mob mob){
                dropConditionalEquipment(mob);
            } else {
                dropEquipment(entity);
            }
            dropCurios(entity,false);
            eraseLivingEntity(entity);
        }
    }

    private static void dropEquipment(LivingEntity entity){
        for(EquipmentSlot slot : EquipmentSlot.values()){
            ItemStack stack = entity.getItemBySlot(slot);
            if(stack.isEmpty()){continue;}
            ItemStack copy = stack.copy();
            entity.setItemSlot(slot,ItemStack.EMPTY);
            if(!entity.getItemBySlot(slot).isEmpty()){entity.getItemBySlot(slot).setCount(0);}
            if(entity.getItemBySlot(slot).isEmpty()){
                thrownItem(entity,copy);
            }
        }
    }

    private static void dropConditionalEquipment(Mob entity){
        for(EquipmentSlot slot : EquipmentSlot.values()){
            ItemStack stack = entity.getItemBySlot(slot);
            if(entity.getEquipmentDropChance(slot) <= 0 || EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)){continue;}
            if(stack.isEmpty()){continue;}
            ItemStack copy = stack.copy();
            entity.setItemSlot(slot,ItemStack.EMPTY);
            if(!entity.getItemBySlot(slot).isEmpty()){entity.getItemBySlot(slot).setCount(0);}
            if(entity.getItemBySlot(slot).isEmpty()){
                thrownItem(entity,copy);
            }
        }
    }

    private static void dropCurios(LivingEntity entity,boolean keep){
         CuriosApi.getCuriosInventory(entity).ifPresent(iCuriosItemHandler -> {
             IItemHandlerModifiable handler = iCuriosItemHandler.getEquippedCurios();
             for (int i = 0; i < handler.getSlots(); i++) {
                 ItemStack stack = handler.getStackInSlot(i);
                 if(stack.isEmpty()){continue;}
                 if(!keep && EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)){continue;}
                 ItemStack copy = stack.copy();
                 handler.setStackInSlot(i,ItemStack.EMPTY);
                 if(!handler.getStackInSlot(i).isEmpty()){handler.getStackInSlot(i).setCount(0);}
                 if(handler.getStackInSlot(i).isEmpty()){
                     thrownItem(entity,copy);
                 }
             }
         });
    }

    private static void slayPlayer(Player player){
        boolean mercy = player.level().getLevelData().isHardcore() && player.getHealth()/player.getMaxHealth() >= 0.4 && player.getHealth() > 2;
        if(mercy){
            EntityUtil.clearAllEffect(player);
            player.setHealth(1);
            if(player.getHealth() > 1.1F){
                player.invulnerableTime = 0;
                player.hurt(AddonDamageTypes.exRewind(player.level()),player.getHealth() - 0.5F);
            }
        } else {
            for (int i = 0; i < 10; i++) {
                EntityUtil.clearAllEffect(player);
                player.invulnerableTime = 0;
                player.hurt(AddonDamageTypes.exRewind(player.level()), Float.MAX_VALUE);
                if(!player.isAlive()){break;}
            }
        }
    }

    //什么也得不到，不要试图用这个刷怪
    private static void eraseLivingEntity(LivingEntity entity){
        EntityUtil.clearAllEffect(entity);
        entity.stopRiding();
        entity.stopUsingItem();
        entity.getPassengers().forEach(Entity::stopRiding);
        entity.levelCallback.onRemove(Entity.RemovalReason.DISCARDED);
    }

    //不想给BOSS战利品，但直接扬了可能影响部分BOSS的复活机制
    private static void hurtPowerfulEntity(LivingEntity entity){
        Vec3 vec3 = entity.position();
        entity.setLastHurtByPlayer(null);
        int minY = entity.level().dimensionType().minY();
        entity.setPos(vec3.x,minY - 640, vec3.z);
        for (int i = 0; i < 10; i++) {
            EntityUtil.clearAllEffect(entity);
            entity.invulnerableTime = 0;
            entity.kill();
            if(!entity.isAlive()){break;}
        }
        if(entity.isAlive()){entity.setPos(vec3.add(EntityUtil.calculateViewVector(0,entity.getRandom().nextInt(360)).scale(5)));}
    }

    private static void thrownItem(LivingEntity entity,ItemStack stack){
        Vec3 offset = EntityUtil.calculateViewVector(0,entity.getRandom().nextInt(360));
        Vec3 spawnLocation = entity.position().add(0,0.5,0).add(offset);
        ItemEntity itemEntity = new ItemEntity(entity.level(),spawnLocation.x,spawnLocation.y,spawnLocation.z,stack);
        itemEntity.setDeltaMovement(offset.scale(0.5F));
        entity.level().addFreshEntity(itemEntity);
    }
}
