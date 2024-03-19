package net.mokai.quicksandrehydrated.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.mokai.quicksandrehydrated.entity.EntityHunnibee;
import net.mokai.quicksandrehydrated.registry.ModModelLayers;

public class HunnibeeRenderer extends MobRenderer<EntityHunnibee, HunnibeeModel<EntityHunnibee>> {

    public HunnibeeRenderer(EntityRendererProvider.Context pContext, EntityModel m) {
        super(pContext, new HunnibeeModel<>(pContext.bakeLayer(ModModelLayers.HUNNIBEE_LAYER)), .5f);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityHunnibee pEntity) {
        return new ResourceLocation("qsrehydrated:textures/entity/hunnibee.png");

    }

    @Override
    public void render(EntityHunnibee entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(.9f,.9f,.9f);
        if(entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }



}
