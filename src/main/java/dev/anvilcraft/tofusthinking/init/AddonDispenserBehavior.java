package dev.anvilcraft.tofusthinking.init;

import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.DispenserBlock;

public class AddonDispenserBehavior {
    public static void register() {
        DispenserBlock.registerBehavior(AddonItems.EXPLOSION_ARROW, AddonDispenserBehavior::canInfinityArrow);
        DispenserBlock.registerProjectileBehavior(AddonItems.CURSE_SNOWBALL_ITEM);
    }
    private static ItemStack canInfinityArrow(BlockSource source, ItemStack stack){
        ServerLevel level = source.level();
        if(stack.getEnchantmentLevel(level.holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.INFINITY)) <= 0){
            stack.shrink(1);
        }
        if(stack.getItem() instanceof ProjectileItem item){
            ProjectileItem.DispenseConfig config = item.createDispenseConfig();
            Direction direction = source.state().getValue(DispenserBlock.FACING);
            Position position = config.positionFunction().getDispensePosition(source, direction);
            Projectile projectile = item.asProjectile(level, position, stack, direction);
            item.shoot(projectile, direction.getStepX(), direction.getStepY(), direction.getStepZ(), config.power(), config.uncertainty());
            level.addFreshEntity(projectile);
        }
        return stack;
    }
}
