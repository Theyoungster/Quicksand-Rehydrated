package net.mokai.quicksandrehydrated.client.render;// Made with Blockbench 4.10.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.mokai.quicksandrehydrated.entity.EntityTarGolem;

@OnlyIn(Dist.CLIENT)
public class TarGolemModel<T extends EntityTarGolem> extends HierarchicalModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "tar_golem"), "main");
	private final ModelPart root;/*
	private final ModelPart core;
	private final ModelPart ribs;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart face;
	private final ModelPart horns;
	private final ModelPart shoulder_r;
	private final ModelPart arm_r;
	private final ModelPart forearm_r;
	private final ModelPart shoulder_l;
	private final ModelPart arm_l;
	private final ModelPart forearm_l;
	private final ModelPart puddle1;
	private final ModelPart puddle2;
	private final ModelPart puddle3;
	private final ModelPart puddle_3_sides;
	private final ModelPart puddle_4;*/

	public TarGolemModel(ModelPart root) {
		System.out.println("root");
		System.out.println(root.getAllParts());
		this.root = root;/*
		this.core = root.getChild("core");
		this.ribs = root.getChild("ribs");
		this.body = root.getChild("body");
		this.head = root.getChild("head");
		this.face = root.getChild("face");
		this.horns = root.getChild("horns");
		this.shoulder_r = root.getChild("shoulder_r");
		this.arm_r = root.getChild("arm_r");
		this.forearm_r = root.getChild("forearm_r");
		this.shoulder_l = root.getChild("shoulder_l");
		this.arm_l = root.getChild("arm_l");
		this.forearm_l = root.getChild("forearm_l");
		this.puddle1 = root.getChild("puddle1");
		this.puddle2 = root.getChild("puddle2");
		this.puddle3 = root.getChild("puddle3");
		this.puddle_3_sides = root.getChild("puddle_3_sides");
		this.puddle_4 = root.getChild("puddle_4");*/
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
		PartDefinition core = root.addOrReplaceChild("core", CubeListBuilder.create().texOffs(0, 128).addBox(-12.0F, -14.0F, -12.0F, 24.0F, 24.0F, 24.0F, new CubeDeformation(0.0F)).texOffs(168, 32).addBox(-10.0F, -12.0F, -9.0F, 20.0F, 8.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -35.0F, 4.0F));
		PartDefinition core_r1 = core.addOrReplaceChild("core_r1", CubeListBuilder.create().texOffs(0, 176).addBox(-8.0F, -12.0F, -7.0F, 16.0F, 28.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.0436F, 0.0F, 0.0F));
		PartDefinition ribs = core.addOrReplaceChild("ribs", CubeListBuilder.create().texOffs(13, 295).addBox(-9.0F, -3.8333F, -2.1667F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(82, 298).addBox(-9.0F, -0.8333F, -2.1667F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(54, 300).addBox(-9.0F, 3.1667F, -2.1667F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(54, 300).mirror().addBox(6.0F, 3.1667F, -2.1667F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(82, 298).mirror().addBox(5.0F, -0.8333F, -2.1667F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(13, 295).mirror().addBox(3.0F, -3.8333F, -2.1667F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 5.8333F, -5.8333F));
		PartDefinition body = core.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -22.1208F, -2.9233F));
		PartDefinition body_back_r1 = body.addOrReplaceChild("body_back_r1", CubeListBuilder.create().texOffs(214, 216).addBox(-12.0F, -14.0F, -4.0F, 24.0F, 24.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.1208F, 18.9233F, 0.0873F, 0.0F, 0.0F));
		PartDefinition body_top_r1 = body.addOrReplaceChild("body_top_r1", CubeListBuilder.create().texOffs(204, 82).addBox(-12.0F, -33.0F, 15.0F, 24.0F, 24.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.1208F, 22.9233F, 1.6581F, 0.0F, 0.0F));
		PartDefinition body_main_r1 = body.addOrReplaceChild("body_main_r1", CubeListBuilder.create().texOffs(114, 98).addBox(-15.0F, -21.0F, 8.0F, 30.0F, 24.0F, 30.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-16.0F, -22.0F, 7.0F, 32.0F, 48.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.1208F, -21.0767F, 0.0873F, 0.0F, 0.0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(196, 152).addBox(-10.0F, -13.4251F, -18.465F, 20.0F, 26.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.5459F, -1.6116F));
		PartDefinition body_front_r1 = head.addOrReplaceChild("body_front_r1", CubeListBuilder.create().texOffs(48, 232).addBox(-7.0F, 4.0F, -3.0F, 14.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -16.4251F, -14.465F, 0.0873F, 0.0F, 0.0F));
		PartDefinition face = head.addOrReplaceChild("face", CubeListBuilder.create().texOffs(104, 82).addBox(-4.0F, -7.4251F, -6.465F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(0, 80).addBox(-5.0F, -8.4251F, -7.465F, 10.0F, 16.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -14.0F));
		PartDefinition horns = face.addOrReplaceChild("horns", CubeListBuilder.create(), PartPose.offset(0.0F, -7.7585F, -1.2984F));
		PartDefinition horn_2_l_r1 = horns.addOrReplaceChild("horn_2_l_r1", CubeListBuilder.create().texOffs(57, 283).mirror().addBox(-7.0F, -7.5F, -0.5F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(95, 281).mirror().addBox(-11.0F, -8.5F, 1.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(11.0F, 1.8333F, -7.6667F, -3.1416F, 0.0F, 1.5708F));
		PartDefinition horn_1_l_r1 = horns.addOrReplaceChild("horn_1_l_r1", CubeListBuilder.create().texOffs(8, 278).mirror().addBox(0.0F, -6.5F, -3.5F, 9.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(11.0F, 1.8333F, -7.6667F, 0.0F, -1.5708F, -1.5708F));
		PartDefinition horn_3_r_r1 = horns.addOrReplaceChild("horn_3_r_r1", CubeListBuilder.create().texOffs(95, 281).addBox(5.0F, -8.5F, 1.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(57, 283).addBox(2.0F, -7.5F, -0.5F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, 1.8333F, -7.6667F, -3.1416F, 0.0F, -1.5708F));
		PartDefinition horn_1_r_r1 = horns.addOrReplaceChild("horn_1_r_r1", CubeListBuilder.create().texOffs(8, 278).addBox(-9.0F, -6.5F, -3.5F, 9.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, 1.8333F, -7.6667F, 0.0F, 1.5708F, 1.5708F));
		PartDefinition shoulder_r = body.addOrReplaceChild("shoulder_r", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition body_r_drip_r1 = shoulder_r.addOrReplaceChild("body_r_drip_r1", CubeListBuilder.create().texOffs(146, 166).addBox(-8.0F, 3.0F, 3.0F, 13.0F, 32.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-20.0F, -12.8792F, -14.0767F, 0.0869F, 0.0076F, -0.1742F));
		PartDefinition arm_r = shoulder_r.addOrReplaceChild("arm_r", CubeListBuilder.create().texOffs(64, 190).addBox(-13.0F, -2.6896F, -7.7952F, 16.0F, 26.0F, 16.0F, new CubeDeformation(0.0F)).texOffs(114, 222).addBox(-12.0F, -1.6896F, -6.7952F, 14.0F, 16.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-15.0F, -4.8792F, 1.9233F, 0.0873F, 0.0F, 0.0F));
		PartDefinition arm_r_r1 = arm_r.addOrReplaceChild("arm_r_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.3739F, -12.7219F, -7.9392F, 8.0F, 22.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, 22.3066F, 4.2615F, 0.0869F, -0.0076F, 0.0869F));
		PartDefinition forearm_r = arm_r.addOrReplaceChild("forearm_r", CubeListBuilder.create(), PartPose.offset(-8.0F, 24.0F, 1.0F));
		PartDefinition forearm_r_r1 = forearm_r.addOrReplaceChild("forearm_r_r1", CubeListBuilder.create().texOffs(0, 220).addBox(-6.0F, 8.1246F, -15.5192F, 12.0F, 28.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.9465F, 15.1286F, -0.5672F, 0.0F, 0.0F));
		PartDefinition shoulder_l = body.addOrReplaceChild("shoulder_l", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition body_l_drip_r1 = shoulder_l.addOrReplaceChild("body_l_drip_r1", CubeListBuilder.create().texOffs(146, 166).mirror().addBox(-5.0F, 3.0F, 3.0F, 13.0F, 32.0F, 24.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(20.0F, -12.8792F, -14.0767F, 0.0869F, -0.0076F, 0.1742F));
		PartDefinition arm_l = shoulder_l.addOrReplaceChild("arm_l", CubeListBuilder.create().texOffs(64, 190).mirror().addBox(-3.0F, -2.6896F, -7.7952F, 16.0F, 26.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(114, 222).mirror().addBox(-2.0F, -1.6896F, -6.7952F, 14.0F, 16.0F, 14.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(15.0F, -4.8792F, 1.9233F, 0.0873F, 0.0F, 0.0F));
		PartDefinition arm_l_r1 = arm_l.addOrReplaceChild("arm_l_r1", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-3.6261F, -12.7219F, -7.9392F, 8.0F, 22.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(6.0F, 22.3066F, 4.2615F, 0.0869F, 0.0076F, -0.0869F));
		PartDefinition forearm_l = arm_l.addOrReplaceChild("forearm_l", CubeListBuilder.create(), PartPose.offset(8.0F, 24.0F, 1.0F));
		PartDefinition forearm_l_r1 = forearm_l.addOrReplaceChild("forearm_l_r1", CubeListBuilder.create().texOffs(0, 220).mirror().addBox(-6.0F, 8.1246F, -15.5192F, 12.0F, 28.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -0.9465F, 15.1286F, -0.5672F, 0.0F, 0.0F));
		PartDefinition puddle1 = root.addOrReplaceChild("puddle1", CubeListBuilder.create(), PartPose.offset(0.0F, -15.2967F, 3.0382F));
		PartDefinition puddle_1_r1 = puddle1.addOrReplaceChild("puddle_1_r1", CubeListBuilder.create().texOffs(168, 0).addBox(-10.0F, -18.0F, -7.0F, 20.0F, 12.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 11.2967F, -5.0382F, -0.1745F, 0.0F, 0.0F));
		PartDefinition puddle2 = root.addOrReplaceChild("puddle2", CubeListBuilder.create(), PartPose.offset(0.0F, -8.5452F, 3.4168F));
		PartDefinition puddle_2_r1 = puddle2.addOrReplaceChild("puddle_2_r1", CubeListBuilder.create().texOffs(70, 152).addBox(-12.0F, -11.0F, -8.0F, 24.0F, 12.0F, 26.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.5452F, -5.4168F, -0.0873F, 0.0F, 0.0F));
		PartDefinition puddle3 = root.addOrReplaceChild("puddle3", CubeListBuilder.create().texOffs(0, 82).addBox(-16.0F, -6.0F, -12.0F, 32.0F, 6.0F, 40.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition puddle_3_sides = root.addOrReplaceChild("puddle_3_sides", CubeListBuilder.create().texOffs(25, 339).addBox(-20.0F, -3.0F, -5.0F, 8.0F, 3.0F, 22.0F, new CubeDeformation(0.0F)).texOffs(25, 339).mirror().addBox(12.0F, -3.0F, -5.0F, 8.0F, 3.0F, 22.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition puddle_4 = root.addOrReplaceChild("puddle_4", CubeListBuilder.create().texOffs(113, 288).addBox(-10.0F, -3.0F, -16.0F, 20.0F, 3.0F, 54.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 512, 512);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		EntityTarGolem targolem = (EntityTarGolem) entity;
		//this.animateIdlePose(targolem.idleAnimationState, TarGolemAnimations.COMPACT_IDLE, 10);


		// man
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