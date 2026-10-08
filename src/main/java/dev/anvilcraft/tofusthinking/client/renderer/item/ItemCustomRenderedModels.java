package dev.anvilcraft.tofusthinking.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@EventBusSubscriber(modid = AnvilCraftTofusThinking.MOD_ID, value = Dist.CLIENT)
public class ItemCustomRenderedModels {
    public static final ResourceLocation ELECTROMAGNETIC_CROSSBOW = AnvilCraftTofusThinking.of("electromagnetic_crossbow");
    @SubscribeEvent
    public static void onModelBake(ModelEvent.ModifyBakingResult event) {
        Map<ModelResourceLocation, BakedModel> modelRegistry = event.getModels();
        swapModels(modelRegistry, ModelResourceLocation.inventory(ELECTROMAGNETIC_CROSSBOW));
    }
    public static void swapModels(Map<ModelResourceLocation, BakedModel> modelRegistry, ModelResourceLocation modelLocation) {
        BakedModel model = modelRegistry.get(modelLocation);
        if (model == null) return;
        CustomRenderedModelWrapper wrapper = new CustomRenderedModelWrapper(model);
        modelRegistry.put(modelLocation, wrapper);
    }

    public static class CustomRenderedModelWrapper extends BakedModelWrapper<BakedModel> {
        public CustomRenderedModelWrapper(BakedModel originalModel) {
            super(originalModel);
        }

        @Override
        public boolean isCustomRenderer() {
            return true;
        }
        // 再次谢谢你，西米不比！
        // Method copied from Create Mod
        @Override
        public @NotNull BakedModel applyTransform(
                @NotNull ItemDisplayContext cameraItemDisplayContext,
                @NotNull PoseStack mat,
                boolean leftHand
        ) {
            // Super call returns originalModel, but we want to return this, else BEWLR
            // won't be used.
            super.applyTransform(cameraItemDisplayContext, mat, leftHand);
            return this;
        }
    }
}
