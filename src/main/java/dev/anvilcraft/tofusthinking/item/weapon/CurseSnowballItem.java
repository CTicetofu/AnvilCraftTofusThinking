package dev.anvilcraft.tofusthinking.item.weapon;

import dev.anvilcraft.tofusthinking.entity.projectile.CurseSnowball;
import dev.anvilcraft.tofusthinking.init.entity.AddonEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CurseSnowballItem extends Item implements ProjectileItem {
    public CurseSnowballItem() {
        super(new Properties().stacksTo(64));
    }
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.SNOWBALL_THROW,
                SoundSource.NEUTRAL,
                0.5F,
                0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        if (!level.isClientSide) {
            CurseSnowball snowball = new CurseSnowball(level, player);
            snowball.setItem(itemstack);
            snowball.setPos(player.getEyePosition().subtract(0,0.1,0));
            snowball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.8F, 0.5F);
            level.addFreshEntity(snowball);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        itemstack.consume(1, player);
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
    @Override
    public @NotNull Projectile asProjectile(@NotNull Level level, @NotNull Position pos, @NotNull ItemStack stack, @NotNull Direction direction) {
        CurseSnowball snowball = new CurseSnowball(AddonEntities.CURSE_SNOWBALL.get(),level);
        snowball.setPos(pos.x(),pos.y(),pos.z());
        snowball.setItem(stack);
        return snowball;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.curse_snowball").withStyle(ChatFormatting.GRAY));
    }
}
