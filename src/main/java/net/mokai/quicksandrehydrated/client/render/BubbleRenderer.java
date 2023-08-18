package net.mokai.quicksandrehydrated.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.mokai.quicksandrehydrated.entity.EntityBubble;

public class BubbleRenderer extends EntityRenderer<EntityBubble> {

    private static final ResourceLocation textureLoc = new ResourceLocation("qsrehydrated:textures/block/quicksand.png");
    private static EntityModel model;
    private final BlockRenderDispatcher dispatcher;
    private static BlockState fluidDummyblockState;

    public BubbleRenderer(EntityRendererProvider.Context pContext, EntityModel m) {
        super(pContext);
        model = m;
        this.shadowStrength = 0;
        this.shadowRadius = 0.5F;
        this.dispatcher = pContext.getBlockRenderDispatcher();
    }
/*
    @Override
    public void render(EntityBubble bubble, float yaw, float partialTicks, PoseStack stack, MultiBufferSource buffers, int light) {
        bubble.initForRender();
        stack.pushPose();
        float size = (float)bubble.getSize();


        stack.translate(0, .5, 0);
        stack.scale(size, size, size);
        stack.translate(0f, -.125, 0);
        stack.mulPose(Axis.YP.rotationDegrees(bubble.rotAngle * (180F / (float) Math.PI)));

        VertexConsumer builder = buffers.getBuffer(model.renderType(bubble.getTexture()));

        model.renderToBuffer(stack, builder, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.075F);

        stack.popPose();
    }

*/

    public void render(EntityBubble pEntity, float pEntityYaw, float pPartialTicks, PoseStack stack, MultiBufferSource pBuffer, int pPackedLight) {
        float size = (float) pEntity.getSize();
        BlockState blockstate = pEntity.getBlockState();
        //BlockState blockstate = Blocks.ACACIA_LOG.defaultBlockState();
        //System.out.println(blockstate);
        if (blockstate.getRenderShape() == RenderShape.MODEL) {
            Level level = pEntity.getLevel();
            if (blockstate != level.getBlockState(pEntity.blockPosition()) && blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                stack.pushPose();
                BlockPos blockpos = new BlockPos(pEntity.getX(), pEntity.getBoundingBox().maxY, pEntity.getZ());
                stack.translate(0, .5, 0);
                stack.scale(size, size, size);
                stack.translate(0f, -.5, 0);
                stack.translate(-0.5D, 0.0D, -0.5D);
                var model = this.dispatcher.getBlockModel(blockstate);
                for (var renderType : model.getRenderTypes(blockstate, RandomSource.create(blockstate.getSeed(pEntity.getOnPos())), net.minecraftforge.client.model.data.ModelData.EMPTY)) {
                    this.dispatcher.getModelRenderer().tesselateBlock(level, model, blockstate, blockpos, stack, pBuffer.getBuffer(renderType), false, RandomSource.create(), blockstate.getSeed(pEntity.getOnPos()), OverlayTexture.NO_OVERLAY, net.minecraftforge.client.model.data.ModelData.EMPTY, renderType);
                }
                stack.popPose();
                super.render(pEntity, pEntityYaw, pPartialTicks, stack, pBuffer, pPackedLight);
            }
        }
    }

    @Override
    public ResourceLocation getTextureLocation(EntityBubble entity) {
        return textureLoc;
    }

}