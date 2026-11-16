package dev.anvilcraft.tofusthinking.item.weapon;

import dev.anvilcraft.tofusthinking.item.LeftClickAction;
import dev.anvilcraft.tofusthinking.network.toServer.LeftClickPacket;
import dev.anvilcraft.tofusthinking.util.ItemUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class LeftSpellItem extends SpellItem implements LeftClickAction {
    public static final String LEFT = "LEFT_CLICK";
    public LeftSpellItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getGroupSpellId() {
        return LEFT;
    }

    @Override
    public void onClientClick(ItemStack stack, Player player) {
        if(this.isOnCooldown(player) || (!player.isCreative() && !ItemUtil.hasEnoughEnergy(stack, this.getCastEnergy(stack)))){return;}
        LeftClickPacket.sendToServer();
        player.swing(InteractionHand.MAIN_HAND);
    }
}
