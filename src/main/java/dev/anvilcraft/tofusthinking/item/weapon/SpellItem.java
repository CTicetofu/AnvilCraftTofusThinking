package dev.anvilcraft.tofusthinking.item.weapon;

import dev.anvilcraft.tofusthinking.attachment.magic.CooldownManager;
import dev.anvilcraft.tofusthinking.client.ClientCooldownCache;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.item.ExtendItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class SpellItem extends ExtendItem {
    public static final String GENERIC_SPELL = "GENERIC";
    public SpellItem(Properties properties) {
        super(properties);
    }

    public int getCastEnergy(ItemStack stack){return 0;}

    public int getMaxEnergy(ItemStack stack){return stack.getOrDefault(AddonComponents.MAX_ENERGY, 16000000);}

    public abstract String getSpellId();

    public String getGroupSpellId(){return GENERIC_SPELL;}

    protected boolean isGenericOnCooldown(Player player){
        return player.level().isClientSide ? ClientCooldownCache.isOnCooldown(this.getGroupSpellId()) : CooldownManager.isOnCooldown(player, this.getGroupSpellId());
    }

    protected boolean isOnCooldown(Player player){
        return isGenericOnCooldown(player) || (player.level().isClientSide ? ClientCooldownCache.isOnCooldown(this.getSpellId()) : CooldownManager.isOnCooldown(player, this.getSpellId()));
    }

    protected void addCooldown(ServerPlayer player, int cd){
        CooldownManager.addCooldown(player, this.getGroupSpellId(),10);
        CooldownManager.addCooldown(player, this.getSpellId(),cd);
    }

    protected float getBaseDamage(LivingEntity attacker, ItemStack stack){
        float baseAmount = (float) attacker.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
        List<ItemAttributeModifiers.Entry> modifiers = stack.getAttributeModifiers().modifiers();
        for (ItemAttributeModifiers.Entry entry : modifiers) {
            if (entry.attribute().equals(Attributes.ATTACK_DAMAGE)) {
                if (entry.modifier().operation().equals(AttributeModifier.Operation.ADD_VALUE)) {
                    baseAmount = (float) (baseAmount + entry.modifier().amount());
                }
            }
        }
        return baseAmount;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return ClientCooldownCache.isOnCooldown(this.getSpellId()) || ClientCooldownCache.isOnCooldown(this.getGroupSpellId());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return (int) (13 * Math.min(ClientCooldownCache.getProgress(this.getSpellId()),ClientCooldownCache.getProgress(this.getGroupSpellId())));
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return 0x00CD66;
    }
}
