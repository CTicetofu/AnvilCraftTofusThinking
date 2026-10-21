package dev.anvilcraft.tofusthinking.anvil;

import dev.anvilcraft.tofusthinking.block.OriginalConduitBlock;
import dev.anvilcraft.tofusthinking.block.entity.OriginalConduitBlockEntity;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.init.entity.AddonDamageTypes;
import dev.anvilcraft.tofusthinking.network.toClient.RespawnPacket;
import dev.anvilcraft.tofusthinking.util.EntityUtil;
import dev.dubhe.anvilcraft.api.anvil.IAnvilBehavior;
import dev.dubhe.anvilcraft.api.event.AnvilEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Map;
import java.util.Optional;

//处理各种******的情况,尝试杜绝刷物品
public class RewindLivingEntityBehavior implements IAnvilBehavior {
    @Override
    public boolean handle(@NotNull Level level, @NotNull BlockPos hitBlockPos, @NotNull BlockState hitBlockState, float fallDistance, AnvilEvent.@NotNull OnLand event) {
        if (!(level instanceof ServerLevel serverLevel)) return false;
        BlockPos pos = hitBlockPos.below();
        BlockState state = serverLevel.getBlockState(pos);
        if(!state.is(AddonBlocks.ORIGINAL_CONDUIT.get()) || !state.getValue(OriginalConduitBlock.OPEN)){return false;}
        if(serverLevel.getBlockEntity(pos) instanceof OriginalConduitBlockEntity blockEntity && blockEntity.getExecuteCooldown() <= 0){
            for (LivingEntity entity:serverLevel.getEntitiesOfClass(LivingEntity.class,new AABB(hitBlockPos))){
                executeLivingEntity(entity);
            }
            blockEntity.resetExecuteCooldown();
        }
        return true;
    }

    public static void executeLivingEntity(LivingEntity entity){
        if(entity instanceof Player player){
            if(player.isCreative() || player.isSpectator()){return;}
            dropEquipment(player);
            dropCurios(player,true);
            player.getInventory().dropAll();
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
             Map<String, ICurioStacksHandler> curios = iCuriosItemHandler.getCurios();
             for (String id : curios.keySet()) {
                 ICurioStacksHandler stacksHandler = curios.get(id);
                 IDynamicStackHandler stackHandler = stacksHandler.getStacks();
                 for (int i = 0; i < stackHandler.getSlots(); i++) {
                     stacksHandler.updateActiveState(i);
                 }
             }
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
            player.getEntityData().set(LivingEntity.DATA_HEALTH_ID,1F);
            player.invulnerableTime = 0;
        } else {
            for (int i = 0; i < 10; i++) {
                EntityUtil.clearAllEffect(player);
                player.invulnerableTime = 0;
                player.hurt(AddonDamageTypes.exRewind(player.level()), Float.MAX_VALUE);
                if(!player.isAlive()){break;}
            }
            //正常来说到不了这，这是一些测试，也许直接重生更好，但目前还有很多问题
            if(player.isAlive()){
                DamageSource source = AddonDamageTypes.exRewind(player.level());
                CommonHooks.onLivingDeath(player, source);
                respawn(player,source);
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

    //构思，我干嘛写这个
    private static void respawn(Player player, DamageSource source){

        if (player.isSleeping()) {
            player.stopSleeping();
        }
        player.awardKillScore(player, 0, source);
        if (player.level() instanceof ServerLevel serverlevel && player instanceof ServerPlayer serverPlayer) {
            boolean flag = serverlevel.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES);
            if (flag) {
                Component component = source.getLocalizedDeathMessage(serverPlayer);
                serverPlayer.connection
                        .send(
                                new ClientboundPlayerCombatKillPacket(serverPlayer.getId(), component),
                                PacketSendListener.exceptionallySend(
                                        () -> {
                                            String s = component.getString(256);
                                            Component component1 = Component.translatable(
                                                    "death.attack.message_too_long", Component.literal(s).withStyle(ChatFormatting.YELLOW)
                                            );
                                            Component component2 = Component.translatable("death.attack.even_more_magic", serverPlayer.getDisplayName())
                                                    .withStyle(p_143420_ -> p_143420_.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, component1)));
                                            return new ClientboundPlayerCombatKillPacket(serverPlayer.getId(), component2);
                                        }
                                )
                        );
                Team team = serverPlayer.getTeam();
                if (team == null || team.getDeathMessageVisibility() == Team.Visibility.ALWAYS) {
                    serverPlayer.server.getPlayerList().broadcastSystemMessage(component, false);
                } else if (team.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OTHER_TEAMS) {
                    serverPlayer.server.getPlayerList().broadcastSystemToTeam(serverPlayer, component);
                } else if (team.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OWN_TEAM) {
                    serverPlayer.server.getPlayerList().broadcastSystemToAllExceptTeam(serverPlayer, component);
                }
            } else {
                serverPlayer.connection.send(new ClientboundPlayerCombatKillPacket(serverPlayer.getId(), CommonComponents.EMPTY));
            }
            player.gameEvent(GameEvent.ENTITY_DIE);
            if (!serverlevel.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
                player.getInventory().dropAll();
            }
            player.setPose(Pose.DYING);
            player.setPos(player.getX(), player.getY(), player.getZ());
            player.awardStat(Stats.DEATHS);
            player.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
            player.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
            player.clearFire();
            player.setTicksFrozen(0);
            player.setSharedFlagOnFire(false);
            player.setLastDeathLocation(Optional.of(GlobalPos.of(player.level().dimension(), player.blockPosition())));
            player.getEntityData().set(LivingEntity.DATA_HEALTH_ID,0F);

            serverPlayer.server.getPlayerList().respawn(serverPlayer,true, Entity.RemovalReason.KILLED);
            PacketDistributor.sendToPlayer(serverPlayer,RespawnPacket.INSTANCE);
        }
    }
}
