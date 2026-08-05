package dev.anvilcraft.tofusthinking.item.blockItem;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SimpleBlockItem extends BlockItem {
    public SimpleBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public SimpleBlockItem(Block block, Properties properties,Predicate<DamageSource> predicate) {
        super(block, properties);
        this.immune = predicate;
    }

    private List<Component> components = new ArrayList<>();

    public static final Predicate<DamageSource> EXPLODE_IMMUNE = damageSource -> damageSource.is(DamageTypeTags.IS_EXPLOSION);
    private Predicate<DamageSource> immune = damageSource -> false;

    public SimpleBlockItem addComponent(Component... components){
        this.components = List.of(components);
        return this;
    }

    @Override
    public boolean canBeHurtBy(@NotNull ItemStack stack, @NotNull DamageSource source) {
        if(immune.test(source)){return false;}
        return super.canBeHurtBy(stack, source);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.addAll(components);
    }
}
