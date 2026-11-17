package dev.anvilcraft.tofusthinking.compat.jade.provider;

import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.block.entity.AutoGrindstoneBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum AutoGrindstoneProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor accessor, IPluginConfig iPluginConfig) {
        if(accessor.getBlockEntity() instanceof AutoGrindstoneBlockEntity){
            CompoundTag data = accessor.getServerData();
            MutableComponent component = Component.literal(String.valueOf(data.getInt("total_xp"))).withStyle(ChatFormatting.WHITE);
            iTooltip.add(Component.translatable("tooltip.anvilcraft_tofus_thing.auto_grindstone.total_xp",component).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if(blockAccessor.getBlockEntity() instanceof AutoGrindstoneBlockEntity blockEntity){
            compoundTag.putInt("total_xp",blockEntity.getTotalXp());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return AnvilCraftTofusThinking.of("auto_grindstone");
    }
}
