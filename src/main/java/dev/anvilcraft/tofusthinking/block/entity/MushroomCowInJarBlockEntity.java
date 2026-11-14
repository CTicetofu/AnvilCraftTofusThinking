package dev.anvilcraft.tofusthinking.block.entity;

import dev.anvilcraft.tofusthinking.block.MushroomCowInJarBlock;
import dev.anvilcraft.tofusthinking.init.block.AddonBlockEntities;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.dubhe.anvilcraft.api.itemhandler.IItemHandlerHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SuspiciousEffectHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.Optional;

public class MushroomCowInJarBlockEntity extends BlockEntity implements IItemHandlerHolder {
    public static final InfinityMilk INFINITY_MILK = new InfinityMilk();
    private MushroomCow mushroomCow = null;
    private boolean isBrown = false;
    private static final String TAG_STEW_EFFECTS = "stew_effects";
    private final MushroomCowItemStackHandler handler = new MushroomCowItemStackHandler();
    @Nullable
    private SuspiciousStewEffects stewEffects;
    public MushroomCowInJarBlockEntity(BlockEntityType<? extends MushroomCowInJarBlockEntity> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public MushroomCowInJarBlockEntity(BlockPos pos, BlockState blockState){
        this(AddonBlockEntities.MUSHROOM_COW_IN_JAR.get(),pos,blockState);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public MushroomCow getOrCreateXeKr(Level level) {
        if(this.mushroomCow == null){
            this.mushroomCow = new MushroomCow(EntityType.MOOSHROOM,level);
            mushroomCow.setVariant(isBrown ? MushroomCow.MushroomType.BROWN : MushroomCow.MushroomType.RED);
        }
        return mushroomCow;
    }

    public void thunderHit(){
        if(this.level == null || this.level.isClientSide){return;}
        changeAndUpdate(!isBrown);
        BlockPos pos = this.getBlockPos();
        this.level.playSound(null,pos.getX(), pos.getY(), pos.getZ(), SoundEvents.MOOSHROOM_CONVERT, SoundSource.BLOCKS, 2.0F, 1.0F);
    }

    public Optional<ItemStack> convertInput(boolean silent, ItemStack input){
        if(input.isEmpty()){return Optional.empty();}
        boolean converted = false;
        ItemStack output = ItemStack.EMPTY;
        SoundEvent event = SoundEvents.MOOSHROOM_MILK;
        if(input.is(Items.BUCKET)){
            converted = true;
            output = new ItemStack(Items.MILK_BUCKET);
        } else if(input.is(Items.BOWL)){
            converted = true;
            if(this.isBrown && this.stewEffects != null){
                event = SoundEvents.MOOSHROOM_MILK_SUSPICIOUSLY;
                output = new ItemStack(Items.SUSPICIOUS_STEW);
                output.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, this.stewEffects);
                this.stewEffects = null;
            } else {
                output = new ItemStack(Items.MUSHROOM_STEW);
            }
        } else if(this.isBrown && input.is(ItemTags.SMALL_FLOWERS) && this.stewEffects == null){
            SuspiciousEffectHolder suspiciouseffectholder = SuspiciousEffectHolder.tryGet(input.getItem());
            if(suspiciouseffectholder != null){
                event = SoundEvents.MOOSHROOM_EAT;
                this.stewEffects = suspiciouseffectholder.getSuspiciousEffects();
                converted = true;
            }
        }
        if(converted && !silent && this.level != null && !this.level.isClientSide){
            BlockPos pos = this.getBlockPos();
            this.level.playSound(null,pos.getX(), pos.getY(), pos.getZ(), event, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return converted ? Optional.of(output) : Optional.empty();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,MushroomCowInJarBlockEntity blockEntity){
        if(state.hasProperty(MushroomCowInJarBlock.POWERED) && state.getValue(MushroomCowInJarBlock.POWERED)){
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.relative(direction);
                IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK,neighborPos,direction.getOpposite());
                if (fluidHandler != null) {
                    fluidHandler.fill(new FluidStack(NeoForgeMod.MILK,64000),IFluidHandler.FluidAction.EXECUTE);
                }
            }
            ItemStackHandler stackHandler = blockEntity.handler;
            ItemStack flower = stackHandler.getStackInSlot(1);
            if(blockEntity.isBrown && blockEntity.stewEffects == null && !flower.isEmpty()){
                if(blockEntity.convertInput(false,flower).isPresent()){
                    flower.shrink(1);
                } else {
                    Vec3 spawnPos = pos.getCenter().add(0,1,0);
                    ItemEntity itemEntity = new ItemEntity(level,spawnPos.x,spawnPos.y,spawnPos.z,flower);
                    level.addFreshEntity(itemEntity);
                    stackHandler.setStackInSlot(1,ItemStack.EMPTY);
                }
                blockEntity.setChanged();
            }
            ItemStack other = stackHandler.getStackInSlot(0);
            if(other.isEmpty()){return;}
            ItemStack left = stackHandler.getStackInSlot(2);
            int leftCount = Math.clamp(left.getMaxStackSize(), 16, stackHandler.getSlotLimit(2)) - left.getCount();
            if(leftCount <= 0){return;}
            Optional<ItemStack> optional = blockEntity.convertInput(true,other);
            if(optional.isPresent()){
                ItemStack convert = optional.get();
                if(left.isEmpty() || ItemStack.isSameItemSameComponents(convert,left)){
                    other.shrink(1);
                    stackHandler.setStackInSlot(2,convert.copyWithCount(convert.getCount() + left.getCount()));
                    blockEntity.setChanged();
                }
            }
        }
    }

    public void changeAndUpdate(boolean isBrown){
        this.isBrown = isBrown;
        sendUpdate();
        setChanged();
    }

    private void sendUpdate() {
        if (this.level == null) return;
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putBoolean("is_brown",this.isBrown);
        return compoundtag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("is_brown",this.isBrown);
        if (this.stewEffects != null) {
            SuspiciousStewEffects.CODEC.encodeStart(NbtOps.INSTANCE, this.stewEffects).ifSuccess((effects) -> tag.put(TAG_STEW_EFFECTS, effects));
        }
        tag.put("Depository", this.handler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        this.isBrown = tag.getBoolean("is_brown");
        if(this.mushroomCow != null){this.mushroomCow.setVariant(isBrown ? MushroomCow.MushroomType.BROWN : MushroomCow.MushroomType.RED);}
        if (tag.contains(TAG_STEW_EFFECTS, 9)) {
            SuspiciousStewEffects.CODEC.parse(NbtOps.INSTANCE, tag.get(TAG_STEW_EFFECTS)).ifSuccess((effects) -> this.stewEffects = effects);
        }
        this.handler.deserializeNBT(registries, tag.getCompound("Depository"));
    }

    @Override
    protected void applyImplicitComponents(@NonNull DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        byte type = componentInput.getOrDefault(AddonComponents.TYPE_NUMBER,(byte)0);
        if(type != 0){this.changeAndUpdate(true);}
        SuspiciousStewEffects effects = componentInput.get(DataComponents.SUSPICIOUS_STEW_EFFECTS);
        if(effects != null){this.stewEffects = effects;}
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.@NonNull Builder components) {
        super.collectImplicitComponents(components);
        components.set(AddonComponents.TYPE_NUMBER,isBrown ? (byte)1 : (byte)0);
        if(this.stewEffects != null){components.set(DataComponents.SUSPICIOUS_STEW_EFFECTS,stewEffects);}
    }

    @Override
    public @NonNull IItemHandler getItemHandler() {
        return handler;
    }
    public class MushroomCowItemStackHandler extends ItemStackHandler{
        public MushroomCowItemStackHandler(){super(3);}

        @Override
        public @NonNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(slot != 2){return ItemStack.EMPTY;}
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        public boolean isItemValid(int slot, @NonNull ItemStack stack) {
            if(slot == 0){return stack.is(Items.BUCKET) || stack.is(Items.BOWL);}
            if(slot == 1){return stack.is(ItemTags.SMALL_FLOWERS);}
            return false;
        }

        @Override
        protected int getStackLimit(int slot, @NonNull ItemStack stack) {
            if(slot == 2){return Math.clamp(stack.getMaxStackSize(), 16, this.getSlotLimit(slot));}
            return super.getStackLimit(slot, stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (level != null && !level.isClientSide) {
                setChanged();
            }
        }
    }

    public static class InfinityMilk implements IFluidHandler {
        private static final FluidStack MILK = new FluidStack(NeoForgeMod.MILK.get(),Integer.MAX_VALUE);

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public @NonNull FluidStack getFluidInTank(int i) {
            return MILK.copy();
        }

        @Override
        public int getTankCapacity(int i) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isFluidValid(int i, @NonNull FluidStack fluidStack) {
            return fluidStack.is(NeoForgeMod.MILK.get());
        }

        @Override
        public int fill(FluidStack resource, @NonNull FluidAction action) {
           if (!resource.isEmpty() && this.isFluidValid(0,resource)) {
               return resource.getAmount();
           }
           return 0;
        }

        @Override
        public @NonNull FluidStack drain(FluidStack resource, IFluidHandler.@NonNull FluidAction action) {
            return !resource.isEmpty() && FluidStack.isSameFluidSameComponents(resource, MILK) ? this.drain(resource.getAmount(), action) : FluidStack.EMPTY;
        }

        @Override
        public @NonNull FluidStack drain(int maxDrain, IFluidHandler.@NonNull FluidAction action) {
            return MILK.copyWithAmount(maxDrain);
        }
   }

    public static class InfinityMilkItem extends InfinityMilk implements IFluidHandlerItem{
        private final ItemStack stack;

        public InfinityMilkItem(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public @NonNull ItemStack getContainer() {
            return stack;
        }
    }
}
