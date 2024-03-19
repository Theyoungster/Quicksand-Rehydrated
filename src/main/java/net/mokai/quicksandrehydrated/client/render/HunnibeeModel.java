package net.mokai.quicksandrehydrated.client.render;// Made with Blockbench 4.9.3

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class HunnibeeModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart root;

	public HunnibeeModel(ModelPart root) {

		this.root = root.getChild("bone");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body2 = bone.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(39, 0).addBox(-4.0F, 6.0F, -1.0F, 8.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(33, 14).addBox(-4.0F, 0.0F, -1.0F, 8.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -24.0F, 0.0F));

		PartDefinition body_r1 = body2.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -22.0F, 8.25F, 10.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.5236F, 0.0F, 0.0F));

		PartDefinition head = body2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 14).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(30, 0).addBox(-3.0F, -8.25F, -7.0F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(3.0F, -8.25F, -7.0F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.5F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.25F, -6.25F, -4.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0436F));

		PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 19).addBox(0.25F, -6.25F, -4.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0436F));

		PartDefinition cube_r3 = head.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 14).addBox(0.0F, -6.0F, -4.25F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3054F));

		PartDefinition cube_r4 = head.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(25, 14).addBox(-2.0F, -6.0F, -4.25F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3054F));

		PartDefinition abdomen = body2.addOrReplaceChild("abdomen", CubeListBuilder.create().texOffs(64, 103).addBox(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(94, 88).addBox(-4.0F, -4.0F, 3.0F, 8.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(64, 64).addBox(-6.0F, -6.5F, 7.0F, 12.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(102, 64).addBox(-4.0F, -4.5F, 21.0F, 8.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 64).addBox(0.0F, -2.0F, 23.0F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(64, 88).addBox(-4.0F, 3.5F, 7.0F, 8.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.0F, 2.0F, 0.0F, 0.0F, -3.1416F));

		PartDefinition leftArm = body2.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(52, 42).addBox(-0.2071F, -0.8787F, -2.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(17, 61).addBox(-0.7071F, 8.1213F, -2.5F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 1.0F, 1.0F));

		PartDefinition rightArm = body2.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(52, 42).mirror().addBox(-2.7929F, -0.8787F, -2.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(17, 61).mirror().addBox(-3.2929F, 8.1213F, -2.5F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-4.0F, 1.0F, 1.0F));

		PartDefinition leftArm2 = body2.addOrReplaceChild("leftArm2", CubeListBuilder.create().texOffs(26, 45).addBox(-0.3473F, -1.0304F, -2.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(48, 58).addBox(-0.8473F, 7.9696F, -2.5F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.25F, 6.0F, 1.0F));

		PartDefinition rightArm2 = body2.addOrReplaceChild("rightArm2", CubeListBuilder.create().texOffs(26, 45).mirror().addBox(-2.6527F, -1.0304F, -2.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(48, 58).mirror().addBox(-3.1527F, 7.9696F, -2.5F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-4.25F, 6.0F, 1.0F));

		PartDefinition RightLeg = body2.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(53, 10).addBox(-1.5F, 9.0F, -1.5F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(13, 45).addBox(-1.0F, 0.0F, -1.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.4F, 12.0F, 0.0F));

		PartDefinition LeftLeg = body2.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(33, 24).addBox(-2.5F, 9.0F, -1.5F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 45).addBox(-2.0F, 0.0F, -1.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.4F, 12.0F, 0.0F));

		PartDefinition leftwing = body2.addOrReplaceChild("leftwing", CubeListBuilder.create().texOffs(28, 32).addBox(-1.0F, 0.0F, 0.0F, 12.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, 2.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition rightwing = body2.addOrReplaceChild("rightwing", CubeListBuilder.create().texOffs(1, 32).addBox(-11.0F, 0.0F, 0.0F, 12.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, 2.0F, 0.0F, 1.5708F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return root;
	}
}