package net.mokai.quicksandrehydrated.client.render.coverage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.ResourceLocation;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.registry.ModModelLayers;

public class CoverageLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    PlayerCoverageDefaultModel coverageModel;
    private final DynamicTexture texture;
    TextureManager textureManager;
    ResourceLocation resourcelocation;

    public CoverageLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> pRenderer, boolean pSlim) {
        super(pRenderer);

        EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
        if (!pSlim) {
            this.coverageModel = new PlayerCoverageDefaultModel(modelSet.bakeLayer(ModModelLayers.COVERAGE_LAYER_DEFAULT));
        } else {
            this.coverageModel = new PlayerCoverageSlimModel(modelSet.bakeLayer(ModModelLayers.COVERAGE_LAYER_SLIM));
        }

        this.texture = new DynamicTexture(64, 64, true);
        this.textureManager = Minecraft.getInstance().textureManager;
        this.resourcelocation = Minecraft.getInstance().textureManager.register("coverage", this.texture);

        System.out.println("creating a coverage layer!");

    }

    private void updateTexture() {

        try {

            TextureAtlasSprite texTest = CoverageAtlasHolder.singleton.get(new ResourceLocation(QuicksandRehydrated.MOD_ID, "quicksand_coverage"));
            System.out.println(texTest);

            for (int i = 0; i < 64; ++i) {
                for (int k = 0; k < 64; ++k) {
                    this.texture.getPixels().setPixelRGBA(i, k, texTest.getPixelRGBA(0, i, k));
                }
            }

            this.texture.upload();

        }
        catch (Exception ex) {
            System.out.println("Error");

//            Random rand = new Random();
//            int col = FastColor.ABGR32.color(255, rand.nextInt(0, 255), rand.nextInt(0, 255), rand.nextInt(0, 255));
//
//            for (int i = 0; i < 64; ++i) {
//                for (int k = 0; k < 64; ++k) {
//                    this.texture.getPixels().setPixelRGBA(i, k, col);
//                }
//            }
//
//            this.texture.upload();

        }



    }

    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, AbstractClientPlayer pAbstractPlayer, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {

        this.updateTexture();

        PlayerCoverageDefaultModel model = this.coverageModel;

        this.getParentModel().copyPropertiesTo(model);
        model.prepareMobModel(pAbstractPlayer, pLimbSwing, pLimbSwingAmount, pPartialTick);
        model.setupAnim(pAbstractPlayer, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);

        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(this.resourcelocation));
        model.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, LivingEntityRenderer.getOverlayCoords(pAbstractPlayer, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);

    }

//    @OnlyIn(Dist.CLIENT)
//    class CoverageTexture implements AutoCloseable {
//
//        // mostly copied from MapInstance
//
//        // needs data var for comparison with player coverage data
//
//        private final DynamicTexture texture;
//        private final RenderType renderType;
//        //private boolean requiresUpload = true;
//
//        CoverageTexture(String pUsername) {
//            this.texture = new DynamicTexture(64, 64, true);
//
//            ResourceLocation resourcelocation = Minecraft.getInstance().textureManager.register("coverage/" + pUsername, this.texture);
//            this.renderType = RenderType.text(resourcelocation);
//        }
//
////        public void forceUpload() {
////            this.requiresUpload = true;
////        }
//
//
//        private void updateTexture() {
//
//            Random rand = new Random();
//            int col = FastColor.ABGR32.color(rand.nextInt(0, 255), rand.nextInt(0, 255), rand.nextInt(0, 255), 255);
//
//            for (int i = 0; i < 64; ++i) {
//                for (int k = 0; k < 64; ++k) {
//                    this.texture.getPixels().setPixelRGBA(i, k, col);
//                }
//            }
//
//            this.texture.upload();
//
//        }
//
//        public void close() {
//            this.texture.close();
//        }
//
//    }

}
