package dev.anvilcraft.tofusthinking.client.renderer.model;

import dev.anvilcraft.tofusthinking.entity.projectile.GemMissile;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class GemMissileModel extends HierarchicalModel<GemMissile> {
    private final ModelPart bone;
    private final ModelPart gemMissile;

    public GemMissileModel(ModelPart root) {
        super(RenderType::entityTranslucent);
        this.bone = root.getChild("bone");
        this.gemMissile = this.bone.getChild("gem_missile");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition partDefinition1 = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        partDefinition1.addOrReplaceChild("gem_missile", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 32);
    }
    @Override
    public @NotNull ModelPart root() {
        return bone;
    }

    @Override
    public void setupAnim(@NotNull GemMissile gemMissile, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.gemMissile.yRot = -ageInTicks * 16.0F * ((float)Math.PI / 180F);
    }
}
