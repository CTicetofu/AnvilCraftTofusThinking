package dev.anvilcraft.tofusthinking.init.block;

import dev.anvilcraft.lib.v2.registrum.util.entry.BlockEntry;
import dev.anvilcraft.tofusthinking.block.AdvancedConduitFrame;
import dev.anvilcraft.tofusthinking.block.FoodGeneratorBlock;
import dev.anvilcraft.tofusthinking.block.OriginalConduitBlock;
import dev.anvilcraft.tofusthinking.block.SmartPowerConverterBlock;
import dev.anvilcraft.tofusthinking.item.blockItem.OriginalConduitItem;
import dev.anvilcraft.tofusthinking.item.blockItem.SimpleBlockItem;
import dev.dubhe.anvilcraft.init.item.ModItemTags;
import dev.dubhe.anvilcraft.util.DataGenUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking.REGISTRUM;
import static dev.dubhe.anvilcraft.api.power.IPowerComponent.OVERLOAD;

public class AddonBlocks {
    public static void register() {
    }
    public static final BlockEntry<AdvancedConduitFrame> STABLE_PRISMARINE_BRICKS = REGISTRUM
            .block("stable_prismarine_bricks", AdvancedConduitFrame::new)
            .initialProperties(() -> Blocks.PRISMARINE_BRICKS)
            .properties(p -> p.explosionResistance(1200))
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.WITHER_IMMUNE)
            .blockstate(DataGenUtil::noExtraModelOrState)
            .item((block, properties) ->
                    new SimpleBlockItem(block,properties.fireResistant(),SimpleBlockItem.EXPLODE_IMMUNE).addComponent(Component.translatable("tooltip.anvilcraft_tofus_thinking.wither_immune").withStyle(ChatFormatting.AQUA))
            )
            .build()
            .register();

    public static final BlockEntry<OriginalConduitBlock> ORIGINAL_CONDUIT = REGISTRUM
            .block("original_conduit", OriginalConduitBlock::new)
            .initialProperties(() -> Blocks.CONDUIT)
            .properties(p -> p.explosionResistance(48000))
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.WITHER_IMMUNE)
            .blockstate(DataGenUtil::noExtraModelOrState)
            .item(OriginalConduitItem::new)
            .model(DataGenUtil::noExtraModelOrState)
            .build()
            .register();

    public static final BlockEntry<FoodGeneratorBlock> NUTRIENT_EXTRACTOR = REGISTRUM
            .block("nutrient_extractor",FoodGeneratorBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.noOcclusion().isValidSpawn(Blocks::never))
            .blockstate(DataGenUtil::noExtraModelOrState)
            .simpleItem()
            .blockstate(DataGenUtil::noExtraModelOrState)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .register();

    public static final BlockEntry<SmartPowerConverterBlock> SMART_POWER_CONVERTER = REGISTRUM
            .block("smart_power_converter",SmartPowerConverterBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.isValidSpawn(Blocks::never).lightLevel(state -> {
                if (state.getValue(OVERLOAD) || state.getValue(BlockStateProperties.POWERED)) {
                    return 6;
                } else {
                    return 15;
                }
            }))
            .blockstate(DataGenUtil::noExtraModelOrState)
            .item()
            .model((ctx, provider) -> provider.blockItem(ctx))
            .tag(ModItemTags.POWER_CONVERTER)
            .build()
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .register();


}
