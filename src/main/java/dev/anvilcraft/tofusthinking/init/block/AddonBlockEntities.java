package dev.anvilcraft.tofusthinking.init.block;

import dev.anvilcraft.lib.v2.registrum.util.entry.BlockEntityEntry;
import dev.anvilcraft.tofusthinking.block.entity.OriginalConduitBlockEntity;
import dev.anvilcraft.tofusthinking.block.entity.OverloadGeneratorBlockEntity;
import dev.anvilcraft.tofusthinking.block.entity.SmartPowerConverterBlockEntity;

import static dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking.REGISTRUM;

public class AddonBlockEntities {
    public static void register() {
    }

    public static final BlockEntityEntry<OriginalConduitBlockEntity> ORIGINAL_CONDUIT = REGISTRUM.<OriginalConduitBlockEntity>blockEntity("original_conduit",OriginalConduitBlockEntity::new)
            .validBlock(AddonBlocks.ORIGINAL_CONDUIT)
            .register();

    public static final BlockEntityEntry<SmartPowerConverterBlockEntity> SMART_POWER_CONVERTER = REGISTRUM.<SmartPowerConverterBlockEntity>blockEntity("smart_power_converter",SmartPowerConverterBlockEntity::new)
            .validBlocks(AddonBlocks.SMART_POWER_CONVERTER,AddonBlocks.SMART_POWER_CONVERTER_EXTREMELY_BIG)
            .register();

    public static final BlockEntityEntry<OverloadGeneratorBlockEntity> OVERLOAD_GENERATOR = REGISTRUM.<OverloadGeneratorBlockEntity>blockEntity("overload_generator", OverloadGeneratorBlockEntity::new)
            .validBlock(AddonBlocks.OVERLOAD_GENERATOR)
            .register();
}
