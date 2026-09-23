package dev.anvilcraft.tofusthinking.item;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ExtendItem extends Item {
    public ExtendItem(Properties properties) {
        super(properties);
    }

    protected boolean permanent = false;

    @Override
    public boolean canBeHurtBy(@NotNull ItemStack stack, @NotNull DamageSource source) {
        if(permanent){
            return source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
        }
        return super.canBeHurtBy(stack,source);

    }

    @Override
    public int getEntityLifespan(@NotNull ItemStack itemStack, @NotNull Level level) {
        return permanent ? 1000000 : super.getEntityLifespan(itemStack, level);
    }

    @Override
    public boolean onEntityItemUpdate(@NotNull ItemStack stack, @NotNull ItemEntity entity) {
        if(permanent){
            if(entity.getAge() >= 0){entity.setExtendedLifetime();}
            double overY = entity.getY() - entity.level().getMinBuildHeight();
            if(overY < 5){
                if(overY < -60){
                    entity.setPos(entity.getX(),entity.level().getMinBuildHeight(),entity.getZ());
                }
                Vec3 motion = entity.getDeltaMovement();
                entity.setDeltaMovement(motion.x,Math.max(overY < 0 ? 0.5 : 0.04,motion.y),motion.z);
            }
        }
        return super.onEntityItemUpdate(stack, entity);
    }
}
