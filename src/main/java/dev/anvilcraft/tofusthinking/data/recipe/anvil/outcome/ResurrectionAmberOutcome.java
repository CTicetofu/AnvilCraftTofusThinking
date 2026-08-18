package dev.anvilcraft.tofusthinking.data.recipe.anvil.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.anvilcraft.lib.v2.codec.StreamCodecUtil;
import dev.anvilcraft.lib.v2.recipe.cache.TagCache;
import dev.anvilcraft.lib.v2.recipe.outcome.IRecipeOutcome;
import dev.anvilcraft.lib.v2.recipe.outcome.SpawnItem;
import dev.anvilcraft.lib.v2.recipe.util.InWorldRecipeContext;
import dev.anvilcraft.tofusthinking.init.recipe.AddonRecipeOutcomeTypes;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.item.ModComponents;
import dev.dubhe.anvilcraft.item.property.component.SavedEntity;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ResurrectionAmberOutcome(Vec3 offset, ResourceLocation savedEntityPath)implements IRecipeOutcome<ResurrectionAmberOutcome> {
    @Override
    public @NotNull Type getType() {
        return AddonRecipeOutcomeTypes.RESURRECTION_AMBER.get();
    }

    @Override
    public void accept(InWorldRecipeContext context) {
        Tag tag = context.computeIfAbsent(TagCache.TAG_CACHE).getTag(this.savedEntityPath);
        if (tag == null) return;
        Optional<SavedEntity> optional = SavedEntity.CODEC.parse(context.getNbtRegistryOps(), tag).result();
        if (optional.isEmpty()) return;

        SavedEntity savedEntity = optional.get();
        ItemStack result = ModBlocks.RESIN_BLOCK.asStack();
        result.set(ModComponents.SAVED_ENTITY, savedEntity);
        SpawnItem.builder().item(result).offset(this.offset).build().accept(context);
    }

    public static class Type implements IRecipeOutcome.Type<ResurrectionAmberOutcome> {
        public static final MapCodec<ResurrectionAmberOutcome> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Vec3.CODEC.fieldOf("offset").forGetter(ResurrectionAmberOutcome::offset),
                        ResourceLocation.CODEC.fieldOf("saved_entity_path").forGetter(ResurrectionAmberOutcome::savedEntityPath)
                ).apply(instance, ResurrectionAmberOutcome::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, ResurrectionAmberOutcome> STREAM_CODEC =
                StreamCodec.composite(
                        StreamCodecUtil.VEC3,
                        ResurrectionAmberOutcome::offset,
                        ResourceLocation.STREAM_CODEC,
                        ResurrectionAmberOutcome::savedEntityPath,
                        ResurrectionAmberOutcome::new
                );

        @Override
        public @NotNull MapCodec<ResurrectionAmberOutcome> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ResurrectionAmberOutcome> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
