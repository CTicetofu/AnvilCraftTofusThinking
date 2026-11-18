package dev.anvilcraft.tofusthinking.event;

import dev.anvilcraft.tofusthinking.block.entity.MushroomCowInJarBlockEntity;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.dubhe.anvilcraft.api.event.LightningBoltStrikeEvent;
import dev.dubhe.anvilcraft.init.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.GrindstoneEvent;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

import java.util.Iterator;

@EventBusSubscriber
public class GenericEventHandler {
    @SubscribeEvent
    public static void onItemExpire(ItemExpireEvent event){
        ItemEntity entity = event.getEntity();
        if(entity.getItem().has(AddonComponents.IMMORTAL)){
            entity.setExtendedLifetime();
        }
    }
    @SubscribeEvent
    public static void onLightningStrike(LightningBoltStrikeEvent event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        for (BlockPos rangePos : BlockPos.betweenClosed(pos.offset(1,1,1),pos.offset(-1,-1,-1))){
            if(level.getBlockState(rangePos).is(AddonBlocks.MUSHROOM_COW_IN_JAR) && level.getBlockEntity(rangePos) instanceof MushroomCowInJarBlockEntity blockEntity){
                blockEntity.thunderHit();
            }
        }
    }

    @SubscribeEvent
    public static void onAnvil(AnvilUpdateEvent event){
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if(!left.isEmpty() && right.is(ModItems.MAGNET) && !left.is(ModItems.MAGNET) && !left.has(AddonComponents.MAGNETIC_ATTRACTION)){
            event.setMaterialCost(1);
            event.setCost(left.getCount());
            ItemStack output = left.copy();
            output.set(AddonComponents.MAGNETIC_ATTRACTION, Unit.INSTANCE);
            event.setOutput(output);
        }
    }

    @SubscribeEvent
    public static void onGrind(GrindstoneEvent.OnPlaceItem event){
        ItemStack top = event.getTopItem();
        if(!top.isEmpty() && top.has(AddonComponents.MAGNETIC_ATTRACTION) && event.getBottomItem().isEmpty()){
            ItemStack output = top.copy();
            output.remove(AddonComponents.MAGNETIC_ATTRACTION);
            event.setOutput(output);
            event.setXp(2);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockDrop(BlockDropsEvent event){
        if(event.getBreaker() instanceof ServerPlayer player && player.isHolding(stack -> stack.has(AddonComponents.MAGNETIC_ATTRACTION))){
            Iterator<ItemEntity> iterable = event.getDrops().iterator();
            while (iterable.hasNext()){
                ItemEntity itemEntity = iterable.next();
                ItemStack stack = itemEntity.getItem();
                if (!stack.isEmpty() && player.getInventory().add(stack)) {
                    iterable.remove();
                } else {
                    itemEntity.teleportTo(player.getX(), player.getY(), player.getZ());
                }
            }

            int exp = event.getDroppedExperience();
            if(exp > 0){
                player.takeXpDelay = 0;
                Vec3 pos = player.position().add(0,0.1,0);
                while(exp > 0) {
                    int i = ExperienceOrb.getExperienceValue(exp);
                    exp -= i;
                    ExperienceOrb orb = new ExperienceOrb(event.getLevel(), pos.x(), pos.y(), pos.z(), i);
                    orb.playerTouch(player);
                    player.takeXpDelay = 0;
                }
                event.setDroppedExperience(0);
            }
        }
    }
}
