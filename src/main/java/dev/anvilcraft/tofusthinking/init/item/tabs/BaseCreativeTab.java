package dev.anvilcraft.tofusthinking.init.item.tabs;

import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.util.DataClass.EnchantmentKeyInstance;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public class BaseCreativeTab implements CreativeModeTab.DisplayItemsGenerator{
    protected CreativeModeTab.ItemDisplayParameters itemDisplayParameters;
    protected CreativeModeTab.Output output;
    @Override
    public void accept(CreativeModeTab.@NotNull ItemDisplayParameters parameters, CreativeModeTab.@NotNull Output output) {
        this.itemDisplayParameters = parameters;
        this.output = output;
        this.init();
    }

    public void init(){}

    public void accept(ItemLike item){
        this.output.accept(item);
    }

    public void accept(ItemStack stack){
        this.output.accept(stack);
    }

    public void acceptFullEnergy(ItemLike item){
        Integer max = item.asItem().components().get(AddonComponents.MAX_ENERGY);
        if(max == null || max <= 0){return;}
        ItemStack stack = item.asItem().getDefaultInstance();
        stack.set(AddonComponents.STORED_ENERGY,max);
        this.accept(stack);
    }

    public void acceptEnchant(ItemLike item,EnchantmentKeyInstance... instances){
        this.accept(enchant(item,this.itemDisplayParameters,instances));
    }

    private static ItemStack enchant(ItemLike item, CreativeModeTab.ItemDisplayParameters parameters, EnchantmentKeyInstance... instances){
        ItemStack stack = item.asItem().getDefaultInstance();
        return enchantStack(stack,parameters,instances);
    }

    private static ItemStack enchantStack(ItemStack stack, CreativeModeTab.ItemDisplayParameters parameters, EnchantmentKeyInstance... instances){
        for (EnchantmentKeyInstance instance : instances){
            var holder = parameters.holders().holder(instance.enchantment);
            holder.ifPresent(enchantmentReference -> stack.enchant(enchantmentReference, instance.level));
        }
        return stack;
    }
}
