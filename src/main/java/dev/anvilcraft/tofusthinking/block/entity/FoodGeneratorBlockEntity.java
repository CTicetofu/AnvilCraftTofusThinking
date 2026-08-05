package dev.anvilcraft.tofusthinking.block.entity;

import dev.anvilcraft.tofusthinking.init.block.AddonBlocks;
import dev.anvilcraft.tofusthinking.init.block.AddonFluids;
import dev.anvilcraft.tofusthinking.inventory.SimpleNumberConfigMenu;
import dev.anvilcraft.tofusthinking.util.ItemUtil;
import dev.dubhe.anvilcraft.api.IHasDisplayItem;
import dev.dubhe.anvilcraft.api.fluid.IFluidHandlerHolder;
import dev.dubhe.anvilcraft.api.itemhandler.FilteredItemStackHandler;
import dev.dubhe.anvilcraft.api.itemhandler.IItemHandlerHolder;
import dev.dubhe.anvilcraft.api.power.IPowerProducer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import dev.dubhe.anvilcraft.block.ChargerBlock;
import dev.dubhe.anvilcraft.block.entity.IFilterBlockEntity;
import dev.dubhe.anvilcraft.network.UpdateDisplayItemPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
    大部分copy from 充/放电器,虽然我觉得有的写法很奇怪，但这么做一定有它的道理
 */
public class FoodGeneratorBlockEntity extends BlockEntity implements IPowerProducer, IFilterBlockEntity, IItemHandlerHolder, IHasDisplayItem, IFluidHandlerHolder, MenuProvider {
    public final int MAX_FOOD_VALUE = 1000000;
    private static final String TAG_TANK = "Tank";
    private static final String TAG_DEPOSITORY = "Depository";
    public static final int MAX_POWER = 16384;
    private final FilteredItemStackHandler itemHandler = new FilteredItemStackHandler(2) {
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (slot == 0) {
                return super.insertItem(slot,stack,simulate);
            }
            return stack;
        }
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return containsValidItem(stack);
        }
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slot == 1 ? super.extractItem(1, amount, simulate) : ItemStack.EMPTY;
        }
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (level != null && !level.isClientSide) {
                setChanged();
                updateDisplayItemStack();
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private final FluidTank tank = new FluidTank(MAX_FOOD_VALUE){
        @Override
        public boolean isFluidValid(@NotNull FluidStack stack) {
            return stack.is(AddonFluids.NUTRIENT_LIQUID_TYPE.get());
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
            return FluidStack.EMPTY;
        }

        @Override
        protected void onContentsChanged() {
            setChanged();
            sendUpdate();
        }
    };
    private ItemStack displayItemStack = ItemStack.EMPTY;
    private PowerGrid grid;
    private int chargeCycle = 40;
    private int currentPower = 0;
    private int planNextPower = 0;

    public FoodGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }
    public boolean containsValidItem(ItemStack stack){
         return stack.getFoodProperties(null) != null;
    }

    public boolean eatFood(int weight){
        ItemStack stack = itemHandler.getStackInSlot(0);
        if(stack.isEmpty()){return false;}
        ItemStack exampleItem = stack.copyWithCount(1);
        FoodProperties properties = exampleItem.getFoodProperties(null);
        if(properties != null){
            if(weight < 1){weight = 1;}
            ItemStack leftStack = ItemUtil.getFoodRemainingItems(exampleItem);
            int consumeCount = weight >= 3 ? stack.getMaxStackSize() : (1 << weight - 1) * stack.getMaxStackSize() / 4;
            if(consumeCount < 1){consumeCount = 1;}
            int allowCount = Math.min(stack.getCount(),consumeCount);
            ItemStack outputStack = itemHandler.getStackInSlot(1);
            if(!leftStack.isEmpty() ){
                if(!outputStack.isEmpty() && !ItemStack.isSameItemSameComponents(outputStack, leftStack)){return false;}
                allowCount = Math.min((leftStack.getMaxStackSize() - outputStack.getCount())/Math.max(leftStack.getCount(),1),allowCount);
            }
            int singleValue = (int) ((properties.nutrition() + properties.saturation()) * Math.min(0.75 + 0.25 * weight,2));
            if(singleValue <= 0){return false;}
            allowCount = Math.min(allowCount,(MAX_FOOD_VALUE - tank.getFluidAmount()) / singleValue);
            if(allowCount <= 0){return false;}
            if(tank.isEmpty()){
                tank.setFluid(new FluidStack(AddonFluids.NUTRIENT_LIQUID,allowCount * singleValue));
            } else {
                tank.getFluid().grow(allowCount * singleValue);
            }
            stack.shrink(allowCount);
            if(!leftStack.isEmpty()){itemHandler.setStackInSlot(1,leftStack.copyWithCount(outputStack.getCount() + leftStack.getCount() * allowCount));}
            updateDisplayItemStack();
            sendUpdate();
            setChanged();
            return true;
        }
        return false;
    }

    private void updateDisplayItemStack() {
        ItemStack newDisplayStack = getDisplayItemStackForRender();
        displayItemStack = newDisplayStack.copy();
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunk(getBlockPos()).getPos(), new UpdateDisplayItemPacket(displayItemStack, getPos()));
    }
    @Override
    public @NotNull Component getDisplayName() {
        return AddonBlocks.NUTRIENT_EXTRACTOR.get().getName();
    }
    @javax.annotation.Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new SimpleNumberConfigMenu(i, this::setPlanNextPower);
    }

    @Override
    public void writeClientSideData(@NotNull AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.getBlockPos());
    }

    public void setPlanNextPower(int planNextPower) {
        planNextPower = Mth.clamp(planNextPower,0,MAX_POWER);
        this.planNextPower = planNextPower;
    }
    public int getPlanNextPower(){
        return planNextPower;
    }
    public void shutDown(){
        this.currentPower = 0;
    }
    private ItemStack getDisplayItemStackForRender() {
        return itemHandler.getStackInSlot(0);
    }
    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.put(TAG_TANK, this.tank.writeToNBT(registries, new CompoundTag()));
        return tag;
    }
    private void sendUpdate() {
        if (this.level == null) return;
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
    }
    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void updateDisplayItem(@NotNull ItemStack stack) {
        displayItemStack = stack;
    }

    public ItemStack getDisplayItemStack() {
        return this.displayItemStack;
    }

    @Override
    public @NotNull IItemHandler getItemHandler() {
        return itemHandler;
    }
    @Override
    public @NotNull IFluidHandler getFluidHandler() {
        return tank;
    }
    @Override
    public @Nullable Level getCurrentLevel() {
        return getLevel();
    }

    @Override
    public @NotNull BlockPos getPos() {
        return getBlockPos();
    }

    @Override
    public void setGrid(@Nullable PowerGrid grid) {
        this.grid = grid;
    }

    @Override
    public @Nullable PowerGrid getGrid() {
        return this.grid;
    }
    @Override
    public @NotNull FilteredItemStackHandler getFilteredItemStackHandler() {
        return this.itemHandler;
    }
    @Override
    public boolean isFilterEnabled() {
        return true;
    }

    @Override
    public boolean isSlotDisabled(int slot) {
        return false;
    }

    public ItemStack tryExtractItemFromSlot0() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (stack.isEmpty()) return ItemStack.EMPTY;
        itemHandler.setStackInSlot(0, ItemStack.EMPTY);
        setChanged();
        return stack;
    }
    @Override
    public int getOutputPower() {
        return currentPower;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state){
        flushState(level,pos);
        boolean powered = state.getValue(ChargerBlock.POWERED);
        if(powered){shutDown();return;}
        if (grid == null) return;
        if(--chargeCycle <= 0){
            if(tank.getFluidAmount() > 3 * planNextPower){
                tank.getFluid().shrink(3 * planNextPower);
                currentPower = planNextPower;
                setChanged();
                sendUpdate();
            } else {
                currentPower = 0;
            }
            chargeCycle = 40;
        }
    }

    public CompoundTag onlySaveTank(Level level){
        CompoundTag tag = new CompoundTag();
        tag.put(TAG_TANK, this.tank.writeToNBT(level.registryAccess(), new CompoundTag()));
        ResourceLocation resourcelocation = BlockEntityType.getKey(this.getType());
        if (resourcelocation == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        } else {
            tag.putString("id", resourcelocation.toString());
        }
        return tag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put(TAG_DEPOSITORY, itemHandler.serializeNBT(provider));
        tag.put(TAG_TANK, this.tank.writeToNBT(provider, new CompoundTag()));
        tag.putInt("charge_cycle",chargeCycle);
        tag.putInt("current_power",currentPower);
        tag.putInt("plan_next_power",planNextPower);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        itemHandler.deserializeNBT(provider, tag.getCompound(TAG_DEPOSITORY));
        this.tank.readFromNBT(provider, tag.getCompound(TAG_TANK));
        this.chargeCycle = tag.getInt("charge_cycle");
        this.currentPower = tag.getInt("current_power");
        this.planNextPower = tag.getInt("plan_next_power");
    }
}
