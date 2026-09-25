package dev.anvilcraft.tofusthinking.inventory;

import dev.anvilcraft.tofusthinking.init.AddonMenuTypes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.NotNull;

public class TofuAnvilMenu extends AnvilMenu {

    public TofuAnvilMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public TofuAnvilMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access){
        super(containerId,playerInventory,access);
    }

    @Override
    public @NotNull MenuType<?> getType(){return AddonMenuTypes.TOFU_ANVIL.get();}

    @Override
    protected void onTake(@NotNull Player player, @NotNull ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            player.giveExperienceLevels(-this.cost.get());
        }

        CommonHooks.onAnvilRepair(player, stack, this.inputSlots.getItem(0), this.inputSlots.getItem(1));

        this.inputSlots.setItem(0, ItemStack.EMPTY);
        if (this.repairItemCountCost > 0) {
            ItemStack itemstack = this.inputSlots.getItem(1);
            if (!itemstack.isEmpty() && itemstack.getCount() > this.repairItemCountCost) {
                itemstack.shrink(this.repairItemCountCost);
                this.inputSlots.setItem(1, itemstack);
            } else {
                this.inputSlots.setItem(1, ItemStack.EMPTY);
            }
        } else {
            this.inputSlots.setItem(1, ItemStack.EMPTY);
        }
        this.cost.set(0);
        access.execute((level, pos) -> level.levelEvent(1030, pos, 0));
    }

    @Override
    public void createResult() {
        ItemStack itemstack = this.inputSlots.getItem(0);
        this.cost.set(1);
        int resultCost = 0;
        int nameCost = 0;
        if (!itemstack.isEmpty()) {
            if (!net.neoforged.neoforge.common.CommonHooks.onAnvilChange(this, itemstack, this.inputSlots.getItem(1), resultSlots, itemName, 0, this.player)) {
                return;
            }
        }
        if (!itemstack.isEmpty() && EnchantmentHelper.canStoreEnchantments(itemstack)) {
            ItemStack stack1 = itemstack.copy();
            ItemStack stack2 = this.inputSlots.getItem(1);
            ItemEnchantments.Mutable itemenchantments$mutable = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(stack1));
            this.repairItemCountCost = 0;
            boolean isRightHasStoredEnch = false;
            if (!stack2.isEmpty()) {
                isRightHasStoredEnch = stack2.has(DataComponents.STORED_ENCHANTMENTS);
                if (stack1.isDamageableItem() && stack1.getItem().isValidRepairItem(itemstack, stack2)) {
                    int singleRepairAmount = Math.min(stack1.getDamageValue(), stack1.getMaxDamage() / 4);
                    if (singleRepairAmount <= 0) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }

                    int costCount;
                    for (costCount = 0; singleRepairAmount > 0 && costCount < stack2.getCount(); costCount++) {
                        int currentDamageValue = stack1.getDamageValue() - singleRepairAmount;
                        stack1.setDamageValue(currentDamageValue);
                        singleRepairAmount = Math.min(stack1.getDamageValue(), stack1.getMaxDamage() / 4);
                    }

                    resultCost += 1;

                    this.repairItemCountCost = costCount;
                } else {
                    if (!isRightHasStoredEnch && (!stack1.is(stack2.getItem()) || !stack1.isDamageableItem())) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }

                    if (stack1.isDamageableItem() && !isRightHasStoredEnch) {
                        int leftDamageValue = getLeftDamageValue(itemstack, stack2, stack1);

                        if (leftDamageValue < stack1.getDamageValue()) {
                            stack1.setDamageValue(leftDamageValue);
                            resultCost += 1;
                        }
                    }

                    ItemEnchantments rightEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack2);
                    boolean cantEnchantThis = false;

                    boolean ignoreConflict = getIgnoreConflict();

                    for (Object2IntMap.Entry<Holder<Enchantment>> entry : rightEnchantments.entrySet()) {
                        Holder<Enchantment> holder = entry.getKey();
                        Enchantment enchantment = holder.value();
                        int leftEnchantmentLevel = itemenchantments$mutable.getLevel(holder);
                        int willEnchantmentLevel = getWillEnchantmentLevel(entry, leftEnchantmentLevel, enchantment);
                        if(willEnchantmentLevel == leftEnchantmentLevel){continue;}
                        boolean isItemCanEnchantThis = itemstack.supportsEnchantment(holder);
                        if (this.player.getAbilities().instabuild) {
                            isItemCanEnchantThis = true;
                        }

                        if(!ignoreConflict){
                            for (Holder<Enchantment> holder1 : itemenchantments$mutable.keySet()) {
                                if (!holder1.equals(holder) && !Enchantment.areCompatible(holder, holder1)) {
                                    isItemCanEnchantThis = false;
                                }
                            }
                        }

                        if (!isItemCanEnchantThis) {
                            cantEnchantThis = true;
                        } else {
                            itemenchantments$mutable.set(holder, willEnchantmentLevel);
                            resultCost += (willEnchantmentLevel - leftEnchantmentLevel);
                            if (itemstack.getCount() > 1) {
                                resultCost = Integer.MAX_VALUE / 2;
                            }
                        }
                    }

                    if (cantEnchantThis) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }
                }
            }

            if (this.itemName != null && !StringUtil.isBlank(this.itemName)) {
                if (!this.itemName.equals(itemstack.getHoverName().getString())) {
                    nameCost = 1;
                    resultCost += nameCost;
                    stack1.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName));
                }
            } else if (itemstack.has(DataComponents.CUSTOM_NAME)) {
                nameCost = 1;
                resultCost += nameCost;
                stack1.remove(DataComponents.CUSTOM_NAME);
            }
            if (isRightHasStoredEnch && !stack1.isBookEnchantable(stack2)) stack1 = ItemStack.EMPTY;

            int finalCost = (int) Mth.clamp(resultCost, 0L, 2147483647L);
            if(finalCost > 0){
                finalCost = finalCost >= 4 ? finalCost/4 : 1;
            }
            this.cost.set(finalCost);
            if (resultCost <= 0) {
                stack1 = ItemStack.EMPTY;
            }
            if (this.cost.get() >= 40 && !this.player.getAbilities().instabuild) {
                stack1 = ItemStack.EMPTY;
            }

            if (!stack1.isEmpty()) {
                EnchantmentHelper.setEnchantments(stack1, itemenchantments$mutable.toImmutable());
            }

            this.resultSlots.setItem(0, stack1);
            this.broadcastChanges();
        } else {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(0);
        }
    }

    private static int getWillEnchantmentLevel(Object2IntMap.Entry<Holder<Enchantment>> entry, int leftEnchantmentLevel, Enchantment enchantment) {
        int willEnchantmentLevel = entry.getIntValue();
        if(leftEnchantmentLevel == willEnchantmentLevel){
            int enchantmentMaxLevel = enchantment.getMaxLevel();
            willEnchantmentLevel = willEnchantmentLevel >= enchantmentMaxLevel ? willEnchantmentLevel : willEnchantmentLevel + 1;
        } else {
            willEnchantmentLevel = Math.max(willEnchantmentLevel, leftEnchantmentLevel);
        }
        return willEnchantmentLevel;
    }

    private static boolean getIgnoreConflict(){return true;}

    private static int getLeftDamageValue(ItemStack itemstack, ItemStack stack2, ItemStack stack1) {
        int leftDurability1 = itemstack.getMaxDamage() - itemstack.getDamageValue();
        int rightDurability = stack2.getMaxDamage() - stack2.getDamageValue();
        int addDurability = rightDurability + stack1.getMaxDamage() * 12 / 100;
        int willDurability = leftDurability1 + addDurability;
        int leftDamageValue = stack1.getMaxDamage() - willDurability;
        if (leftDamageValue < 0) {
            leftDamageValue = 0;
        }
        return leftDamageValue;
    }
}
