package dev.anvilcraft.tofusthinking.block.entity;

import dev.anvilcraft.tofusthinking.init.block.AddonBlockEntities;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.api.itemhandler.IItemHandlerHolder;
import dev.dubhe.anvilcraft.init.item.ModItems;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.GrindstoneEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jspecify.annotations.NonNull;

public class AutoGrindstoneBlockEntity extends BlockEntity implements Container, IItemHandlerHolder {
    public AutoGrindstoneBlockEntity(BlockEntityType<? extends AutoGrindstoneBlockEntity> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }
    public AutoGrindstoneBlockEntity(BlockPos pos, BlockState blockState){
        this(AddonBlockEntities.AUTO_GRINDSTONE.get(), pos, blockState);
    }

    private final AutoGrindstoneItemStackHandler handler = new AutoGrindstoneItemStackHandler();
    private final ContainerLevelAccess access = ContainerLevelAccess.NULL;

    private int totalXp = 0;

    @Override
    public @NonNull IItemHandler getItemHandler() {
        return handler;
    }

    public AutoGrindstoneItemStackHandler getHandler(){
        return handler;
    }

    public void takeGrind(){
        if(!(this.level instanceof ServerLevel serverLevel)){return;}
        tryConvert();
        if(!this.handler.getStackInSlot(2).isEmpty()){return;}
        int getXp = CommonHooks.onGrindstoneChange(this.handler.getStackInSlot(0), this.handler.getStackInSlot(1), this, -1);
        if (getXp == Integer.MIN_VALUE){
            this.setItem(0, this.computeResult(this.handler.getStackInSlot(0), this.handler.getStackInSlot(1)));
        }
        if(this.getItem(0).isEmpty()){return;}
        GrindstoneEvent.OnTakeItem e = new GrindstoneEvent.OnTakeItem(access, null, this.handler.getStackInSlot(0), this.handler.getStackInSlot(1), this.getExperienceAmount(serverLevel, getXp));
        if(!NeoForge.EVENT_BUS.post(e).isCanceled()){
            serverLevel.levelEvent(1042, this.getBlockPos(), 0);
            this.handler.setStackInSlot(0, e.getNewTopItem());
            this.handler.setStackInSlot(1, e.getNewBottomItem());
            totalXp += Math.max(e.getXp(), 0);
        }
        tryConvert();
        int allowMax = AnvilCraft.CONFIG.world.expFluidXpPerBlock * ModItems.EXP_GEM.asStack().getMaxStackSize();
        if(totalXp > allowMax){
            Vec3 pos = this.getBlockPos().getCenter().add(0, 1,0);
            ExperienceOrb orb = new ExperienceOrb(serverLevel, pos.x, pos.y, pos.z, allowMax - totalXp);
            serverLevel.addFreshEntity(orb);
            totalXp = allowMax;
        }
        setChanged();
    }

    private void tryConvert(){
        int per = AnvilCraft.CONFIG.world.expFluidXpPerBlock;
        if(per <= 0 || this.totalXp < per){return;}
        if(!this.handler.getStackInSlot(3).is(ModItems.EXP_GEM)){this.handler.setStackInSlot(3, ItemStack.EMPTY);}
        ItemStack stack = this.handler.getStackInSlot(3);
        ItemStack exp = stack.isEmpty() ? ModItems.EXP_GEM.asStack() : stack;
        int canConvert = Math.min(exp.getMaxStackSize() - stack.getCount(), this.totalXp / per);
        if(canConvert > 0){
            this.totalXp -= canConvert * per;
            this.handler.setStackInSlot(3, exp.copyWithCount(canConvert + stack.getCount()));
        }
    }

    public void setItemToHand(Player player){
        ItemStack inHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        if(!inHand.isEmpty() && inHand.getCount() >= inHand.getMaxStackSize()){ return;}
        for (int i = 3; i >= 0; i--) {
            ItemStack stack = this.handler.getStackInSlot(i);
            if(!stack.isEmpty() && (inHand.isEmpty() || ItemStack.isSameItemSameComponents(inHand, stack))){
                int outCount = stack.getCount();
                if(!inHand.isEmpty()){outCount = Math.min(outCount, inHand.getMaxStackSize() - inHand.getCount());}
                player.setItemInHand(InteractionHand.MAIN_HAND, stack.copyWithCount(outCount + inHand.getCount()));
                this.handler.setStackInSlot(i, stack.copyWithCount(stack.getCount() - outCount));
                break;
            }
        }
    }

    public int getTotalXp() {
        return totalXp;
    }

    private ItemStack computeResult(ItemStack inputItem, ItemStack additionalItem) {
        boolean flag = !inputItem.isEmpty() || !additionalItem.isEmpty();
        if (!flag) {
            return ItemStack.EMPTY;
        } else if (inputItem.getCount() <= 1 && additionalItem.getCount() <= 1) {
            boolean flag1 = !inputItem.isEmpty() && !additionalItem.isEmpty();
            if (!flag1) {
                ItemStack itemstack = !inputItem.isEmpty() ? inputItem : additionalItem;
                return !EnchantmentHelper.hasAnyEnchantments(itemstack) ? ItemStack.EMPTY : this.removeNonCursesFrom(itemstack.copy());
            } else {
                return this.mergeItems(inputItem, additionalItem);
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    private ItemStack removeNonCursesFrom(ItemStack item) {
        ItemEnchantments itemenchantments = EnchantmentHelper.updateEnchantments(
                item, mutable -> mutable.removeIf(enchantmentHolder -> !enchantmentHolder.is(EnchantmentTags.CURSE))
        );
        if (item.is(Items.ENCHANTED_BOOK) && itemenchantments.isEmpty()) {
            item = item.transmuteCopy(Items.BOOK);
        }

        int i = 0;

        for (int j = 0; j < itemenchantments.size(); j++) {
            i = AnvilMenu.calculateIncreasedRepairCost(i);
        }

        item.set(DataComponents.REPAIR_COST, i);
        return item;
    }

    private ItemStack mergeItems(ItemStack inputItem, ItemStack additionalItem) {
        if (!inputItem.is(additionalItem.getItem())) {
            return ItemStack.EMPTY;
        } else {
            int i = Math.max(inputItem.getMaxDamage(), additionalItem.getMaxDamage());
            int j = inputItem.getMaxDamage() - inputItem.getDamageValue();
            int k = additionalItem.getMaxDamage() - additionalItem.getDamageValue();
            int l = j + k + i * 5 / 100;
            int i1 = 1;
            if (!inputItem.isDamageableItem() || !inputItem.isRepairable()) {
                if (inputItem.getMaxStackSize() < 2 || !ItemStack.matches(inputItem, additionalItem)) {
                    return ItemStack.EMPTY;
                }

                i1 = 2;
            }

            ItemStack itemstack = inputItem.copyWithCount(i1);
            if (itemstack.isDamageableItem()) {
                itemstack.set(DataComponents.MAX_DAMAGE, i);
                itemstack.setDamageValue(Math.max(i - l, 0));
                if (!additionalItem.isRepairable()) itemstack.setDamageValue(inputItem.getDamageValue());
            }

            this.mergeEnchantsFrom(itemstack, additionalItem);
            return this.removeNonCursesFrom(itemstack);
        }
    }

    private void mergeEnchantsFrom(ItemStack inputItem, ItemStack additionalItem) {
        EnchantmentHelper.updateEnchantments(inputItem, mutable -> {
            ItemEnchantments itemenchantments = EnchantmentHelper.getEnchantmentsForCrafting(additionalItem);

            for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemenchantments.entrySet()) {
                Holder<Enchantment> holder = entry.getKey();
                if (!holder.is(EnchantmentTags.CURSE) || mutable.getLevel(holder) == 0) {
                    mutable.upgrade(holder, entry.getIntValue());
                }
            }
        });
    }

    private int getExperienceAmount(Level level, int getXp) {
        if (getXp > -1) return getXp;
        int l = 0;
        l += this.getExperienceFromItem(this.handler.getStackInSlot(0));
        l += this.getExperienceFromItem(this.handler.getStackInSlot(1));
        if (l > 0) {
            int i1 = (int)Math.ceil((double)l / 2.0);
            return i1 + level.random.nextInt(i1);
        } else {
            return 0;
        }
    }

    private int getExperienceFromItem(ItemStack stack) {
        int l = 0;
        ItemEnchantments itemenchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemenchantments.entrySet()) {
            Holder<Enchantment> holder = entry.getKey();
            int i1 = entry.getIntValue();
            if (!holder.is(EnchantmentTags.CURSE)) {
                l += holder.value().getMinCost(i1);
            }
        }

        return l;
    }

    @Override
    protected void saveAdditional(@NonNull CompoundTag tag, HolderLookup.@NonNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("xp", this.totalXp);
        if(!this.handler.isEmpty()){
            tag.put("Depository", this.handler.serializeNBT(registries));
        }
    }

    @Override
    protected void loadAdditional(@NonNull CompoundTag tag, HolderLookup.@NonNull Provider registries) {
        super.loadAdditional(tag, registries);
        this.totalXp = tag.getInt("xp");
        if(tag.contains("Depository")){
            this.handler.deserializeNBT(registries, tag.getCompound("Depository"));
        }
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.getItem(0).isEmpty();
    }

    @Override
    public @NonNull ItemStack getItem(int i) {
        return handler.getStackInSlot(2);
    }

    @Override
    public @NonNull ItemStack removeItem(int i, int amount) {
        ItemStack stack = this.getItem(0);
        if(!stack.isEmpty()){
            setChanged();
            return stack.split(amount);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NonNull ItemStack removeItemNoUpdate(int i) {
        ItemStack stack = this.getItem(0);
        if(!stack.isEmpty()){
            setChanged();
            return stack.split(stack.getCount());
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int i, @NonNull ItemStack itemStack) {
        handler.setStackInSlot(2, itemStack);
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return false;
    }

    @Override
    public void clearContent() {}

    public class AutoGrindstoneItemStackHandler extends ItemStackHandler{
        public AutoGrindstoneItemStackHandler(){super(4);}
        public boolean isEmpty(){return stacks.isEmpty();}

        public final IItemHandler top = new LimitItemStackHandler(true);
        public final IItemHandler side = new LimitItemStackHandler(false);

        @Override
        public @NonNull ItemStack insertItem(int slot, @NonNull ItemStack stack, boolean simulate) {
            if(slot >= 2){return stack;}
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public @NonNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(slot <= 1){return ItemStack.EMPTY;}
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (level != null && !level.isClientSide) {
                setChanged();
            }
        }

        class LimitItemStackHandler implements IItemHandler{
            private final boolean bottom;
            public LimitItemStackHandler(boolean bottom){this.bottom = bottom;}
            @Override
            public int getSlots() {
                return AutoGrindstoneItemStackHandler.this.getSlots();
            }

            @Override
            public @NonNull ItemStack getStackInSlot(int i) {
                return AutoGrindstoneItemStackHandler.this.getStackInSlot(i);
            }

            @Override
            public @NonNull ItemStack insertItem(int i, @NonNull ItemStack itemStack, boolean b) {
                int index = bottom ? 0 : 1;
                if(i != index){return itemStack;}
                return AutoGrindstoneItemStackHandler.this.insertItem(i, itemStack, b);
            }

            @Override
            public @NonNull ItemStack extractItem(int i, int i1, boolean b) {
                return AutoGrindstoneItemStackHandler.this.extractItem(i, i1, b);
            }

            @Override
            public int getSlotLimit(int i) {
                return 99;
            }

            @Override
            public boolean isItemValid(int i, @NonNull ItemStack itemStack) {
                return AutoGrindstoneItemStackHandler.this.isItemValid(i, itemStack);
            }
        }
    }
}
