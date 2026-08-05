package dev.anvilcraft.tofusthinking.data;

import dev.anvilcraft.lib.v2.registrum.providers.ProviderType;
import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.data.lang.AddonEnUsLangGen;
import dev.anvilcraft.tofusthinking.data.lang.AddonZnChLangGen;
import dev.anvilcraft.tofusthinking.data.tags.AddonTagsHandler;
import dev.anvilcraft.tofusthinking.init.entity.AddonDamageTypes;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

import static dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking.REGISTRUM;

@EventBusSubscriber(modid = AnvilCraftTofusThinking.MOD_ID)
public class TofusThinkingDatagen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        PackOutput packOutput = generator.getPackOutput();

        generator.addProvider(event.includeClient(),new AddonEnUsLangGen(packOutput));
        generator.addProvider(event.includeClient(),new AddonZnChLangGen(packOutput));
    }
    public static void init(){
        var genInit = REGISTRUM.getDataGenInitializer();
        genInit.add(Registries.DAMAGE_TYPE, AddonDamageTypes::bootstrap);

        REGISTRUM.addDataGenerator(ProviderType.DAMAGE_TYPE_TAGS, AddonTagsHandler::initDamageType);
    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike itemLike) {
        return RegistrumRecipeProvider.has(itemLike);
    }
}
