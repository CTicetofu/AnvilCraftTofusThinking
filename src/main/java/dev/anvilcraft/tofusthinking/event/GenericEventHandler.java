package dev.anvilcraft.tofusthinking.event;

import dev.anvilcraft.tofusthinking.block.entity.MushroomCowInJarBlockEntity;
import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.dubhe.anvilcraft.api.event.LightningBoltStrikeEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;

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
}
