package net.mokai.quicksandrehydrated.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.mokai.quicksandrehydrated.entity.EntityBubble;

public class BubbleRenderer extends EntityRenderer<EntityBubble> {

    private static final ResourceLocation textureLoc = new ResourceLocation("qsrehydrated:textures/block/quicksand.png");
    private static EntityModel model;

    public BubbleRenderer(EntityRendererProvider.Context pContext, EntityModel m) {
        super(pContext);
        model = m;
        this.shadowStrength = 0;
    }

    @Override
    public void render(EntityBubble bubble, float yaw, float partialTicks, PoseStack stack, MultiBufferSource buffers, int light) {
        stack.pushPose();
        float size = (float)bubble.getSize();
        System.out.println(size);
        stack.translate(0, .5, 0);
        stack.scale(size, size, size);
        stack.translate(0f, -.125, 0);

        //VertexConsumer builder = buffers.getBuffer(model.renderType(textureLoc));
        VertexConsumer builder = buffers.getBuffer(model.renderType(bubble.getTexture()));

        model.renderToBuffer(stack, builder, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.075F);

        stack.popPose();
    }



    @Override
    public ResourceLocation getTextureLocation(EntityBubble entity) {
        return textureLoc;
    }

}