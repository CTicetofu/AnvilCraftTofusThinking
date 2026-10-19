package dev.anvilcraft.tofusthinking.recipe.reforge;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public final class ReforgePredicates {
    private static final Map<ResourceLocation, Predicate<ItemStack>> PREDICATES = new HashMap<>();

    private ReforgePredicates() {
    }

    static {
        register("can_repair", stack -> stack.isRepairable() && stack.getXpRepairRatio() > 0);
        register("damaged", ItemStack::isDamaged);
        register("damageable", ItemStack::isDamageableItem);
        register("enchantable", ItemStack::isEnchantable);
        register("enchanted", ItemStack::isEnchanted);
    }
    //便于其他模组或者别的什么东西添加
    public static void register(String name, Predicate<ItemStack> predicate) {
        PREDICATES.put(AnvilCraftTofusThinking.of(name), predicate);
    }

    private static ResourceLocation resolve(String name) {
        if (name == null || name.isBlank()) return null;
        return name.contains(":") ? ResourceLocation.tryParse(name) : AnvilCraftTofusThinking.of(name);
    }

    public static boolean test(String name, ItemStack stack) {
        ResourceLocation id = resolve(name);
        if (id == null) return true;
        Predicate<ItemStack> predicate = PREDICATES.get(id);
        return predicate != null && predicate.test(stack);
    }

    public static MutableComponent describe(String name) {
        ResourceLocation id = resolve(name);
        String path = id == null ? name : id.getPath();
        String space = id == null ? "anvilcraft_tofus_thinking" : id.getNamespace();
        return Component.translatable(String.format("jei.%s.reforge.requirement.%s",space,path));
    }
}
