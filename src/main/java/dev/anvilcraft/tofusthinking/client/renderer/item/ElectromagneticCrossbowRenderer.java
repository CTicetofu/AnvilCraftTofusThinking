package dev.anvilcraft.tofusthinking.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class ElectromagneticCrossbowRenderer extends BlockEntityWithoutLevelRenderer {

    private static final EntityRenderDispatcher entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher();
    public ElectromagneticCrossbowRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public static IClientItemExtensions ELECTROMAGNETIC_CROSSBOW_EXTENSION = new IClientItemExtensions(){
        private final BlockEntityWithoutLevelRenderer renderer = new ElectromagneticCrossbowRenderer();
        private final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        private final MultiBufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        @Override
        public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return renderer;
        }

        @Override
        public HumanoidModel.ArmPose getArmPose(@NotNull LivingEntity entityLiving, @NotNull InteractionHand hand, ItemStack itemStack) {
            if (itemStack.is(AddonItems.ELECTROMAGNETIC_CROSSBOW) && CrossbowItem.isCharged(itemStack)) {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }
            return null;
        }

        @Override
        public boolean applyForgeHandTransform(@NotNull PoseStack poseStack, @NotNull LocalPlayer player,
                                               @NotNull HumanoidArm arm, @NotNull ItemStack stack,
                                               float partialTick, float equipProcess, float swingProcess) {
            if (!stack.is(AddonItems.ELECTROMAGNETIC_CROSSBOW)) return false;

            InteractionHand thisHand = (arm == player.getMainArm())
                    ? InteractionHand.MAIN_HAND
                    : InteractionHand.OFF_HAND;
            boolean isMainHand = thisHand == InteractionHand.MAIN_HAND;

            boolean isChargingThisHand = player.isUsingItem()
                    && player.getUseItemRemainingTicks() > 0
                    && player.getUsedItemHand() == thisHand;

            boolean isCharged = CrossbowItem.isCharged(stack);
            boolean isRightArm = arm == HumanoidArm.RIGHT;
            int i = isRightArm ? 1 : -1;

            if (isChargingThisHand) {
                this.applyItemArmTransform(poseStack, arm, equipProcess);
                poseStack.translate((float)i * -0.4785682F, -0.094387F, 0.05731531F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-11.935F));
                poseStack.mulPose(Axis.YP.rotationDegrees((float)i * 65.3F));
                poseStack.mulPose(Axis.ZP.rotationDegrees((float)i * -9.785F));
                float f9 = (float)stack.getUseDuration(player) - ((float)player.getUseItemRemainingTicks() - partialTick + 1.0F);
                float f13 = f9 / (float)CrossbowItem.getChargeDuration(stack, player);
                if (f13 > 1.0F) f13 = 1.0F;
                if (f13 > 0.1F) {
                    float f16 = Mth.sin((f9 - 0.1F) * 1.3F);
                    float f3 = f13 - 0.1F;
                    float f4 = f16 * f3;
                    poseStack.translate(f4 * 0.0F, f4 * 0.004F, f4 * 0.0F);
                }
                poseStack.translate(f13 * 0.0F, f13 * 0.0F, f13 * 0.04F);
                poseStack.scale(1.0F, 1.0F, 1.0F + f13 * 0.2F);
                poseStack.mulPose(Axis.YN.rotationDegrees((float)i * 45.0F));
            } else {
                if (!isMainHand && player.isUsingItem() && player.getUsedItemHand() == thisHand) {
                    return false;
                }

                float f = -0.4F * Mth.sin(Mth.sqrt(swingProcess) * (float) Math.PI);
                float f1 = 0.2F * Mth.sin(Mth.sqrt(swingProcess) * (float) (Math.PI * 2));
                float f2 = -0.2F * Mth.sin(swingProcess * (float) Math.PI);
                poseStack.translate((float)i * f, f1, f2);
                this.applyItemArmTransform(poseStack, arm, equipProcess);
                this.applyItemArmAttackTransform(poseStack, arm, swingProcess);
                if (isCharged && swingProcess < 0.001F && isMainHand) {
                    poseStack.translate((float)i * -0.641864F, 0.0F, 0.0F);
                    poseStack.mulPose(Axis.YP.rotationDegrees((float)i * 10.0F));
                }
            }

            this.renderItem(
                    player,
                    stack,
                    isRightArm ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
                    !isRightArm,
                    poseStack,
                    buffer,
                    0
            );
            return true;
        }
        private void applyItemArmTransform(PoseStack poseStack, HumanoidArm hand, float equippedProg) {
            int i = hand == HumanoidArm.RIGHT ? 1 : -1;
            poseStack.translate((float)i * 0.56F, -0.52F + equippedProg * -0.6F, -0.72F);
        }

        private void applyItemArmAttackTransform(PoseStack poseStack, HumanoidArm hand, float swingProgress) {
            int i = hand == HumanoidArm.RIGHT ? 1 : -1;
            float f = Mth.sin(swingProgress * swingProgress * (float) Math.PI);
            poseStack.mulPose(Axis.YP.rotationDegrees((float)i * (45.0F + f * -20.0F)));
            float f1 = Mth.sin(Mth.sqrt(swingProgress) * (float) Math.PI);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float)i * f1 * -20.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(f1 * -80.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees((float)i * -45.0F));
        }

        public void renderItem(
                LivingEntity entity,
                ItemStack itemStack,
                ItemDisplayContext displayContext,
                boolean leftHand,
                PoseStack poseStack,
                MultiBufferSource buffer,
                int seed
        ) {
            if (!itemStack.isEmpty()) {
                this.itemRenderer
                        .renderStatic(
                                entity,
                                itemStack,
                                displayContext,
                                leftHand,
                                poseStack,
                                buffer,
                                entity.level(),
                                seed,
                                OverlayTexture.NO_OVERLAY,
                                entity.getId() + displayContext.ordinal()
                        );
            }
        }
    };

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int light, int overlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        poseStack.pushPose();
        BakedModel models = itemRenderer.getItemModelShaper().getItemModel(stack);
        for (var model : models.getRenderPasses(stack, true)) {
            for (var rendertype : model.getRenderTypes(stack, true)) {
                VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(buffer, rendertype, true, stack.hasFoil());
                itemRenderer.renderModelLists(model, stack, light, overlay, poseStack, vertexconsumer);
            }
        }
        ChargedProjectiles projectiles = stack.get(DataComponents.CHARGED_PROJECTILES);
        ClientLevel level = Minecraft.getInstance().level;
        if(level == null){return;}
        if(projectiles != null && !projectiles.isEmpty()){
            ItemStack inside = projectiles.getItems().getFirst();
            if(inside.getItem() instanceof ArrowItem arrowItem){
                poseStack.translate(0.5F,0.7F,0);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                Projectile projectile = arrowItem.asProjectile(Minecraft.getInstance().level,new Vec3(0,0,0),inside.copy(), Direction.DOWN);
                entityRenderer.render(projectile,0,0,0,0, DeltaTracker.ONE.getGameTimeDeltaPartialTick(false),poseStack,buffer,light);
            } else if(inside.is(Items.FIREWORK_ROCKET)){
                poseStack.translate(0.5F,0.65F,0.4F);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                poseStack.scale(0.5F,0.5F,0.5F);
                itemRenderer.renderStatic(inside,ItemDisplayContext.FIXED, light, overlay, poseStack, buffer, null, 0);
            } else {
                poseStack.translate(0.5F,0.725F,0.5F);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                poseStack.scale(0.2F,0.2F,0.2F);
                itemRenderer.renderStatic(inside,ItemDisplayContext.FIXED, light, overlay, poseStack, buffer, null, 0);
            }
        }
        poseStack.popPose();
    }

}
