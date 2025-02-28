package net.mokai.quicksandrehydrated.client.render.coverage;

import com.mojang.blaze3d.platform.NativeImage;
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
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.FastColor;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.entity.coverage.CoverageEntry;
import net.mokai.quicksandrehydrated.entity.coverage.PlayerCoverage;
import net.mokai.quicksandrehydrated.entity.playerStruggling;
import net.mokai.quicksandrehydrated.registry.ModModelLayers;

import java.util.ArrayList;
import java.util.List;

import java.awt.*;

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

    }

    private void updateTexture(PlayerCoverage cov) {

        // TODO make this only update the portion changed?

        // This function iterates over *the entire* dynamic coverage texture ...
        // Ideally, it should update only the portion that has changed,
        // but I haven't implemented anything that tracks what has been changed, and what hasn't

        NativeImage img = this.texture.getPixels();

        // this sets the entire thing to transparent black ... ?
        for (int i = 0; i < 64; ++i) {
            for (int k = 0; k < 64; ++k) {
                img.setPixelRGBA(i, k, 0);
            }
        }

        int entry_count = cov.coverageEntries.size();
        if (entry_count == 0) {
            this.texture.upload();
            return;
        }

        // for each coverage entry (starting from bottom for overlap purposes?)
        for (int c = entry_count-1; c >= 0; --c) {

            CoverageEntry entry = cov.coverageEntries.get(c);

            double bot = 1.0 - (entry.end/32.0);
            double top = 1.0 - (entry.begin/32.0);

            // access this coverage's texture, and the depth mask
            // theoretically, different depth masks could be used for different coverages
            TextureAtlasSprite colorTex = CoverageAtlasHolder.singleton.get(entry.texture);
            TextureAtlasSprite alphaTex = CoverageAtlasHolder.singleton.get(new ResourceLocation(QuicksandRehydrated.MOD_ID, "coverage_mask"));

            // (the depth mask color doesn't matter - I used the alpha to determine depth)
            // (100% alpha at bottom, 0% alpha at top)

            // for every pixel coordinate ... for each coverage?
            for (int i = 0; i < 64; ++i) {
                for (int k = 0; k < 64; ++k) {

                    // access depth mask color
                    int alpha_rgba = alphaTex.getPixelRGBA(0, i, k);
                    float A = (float) FastColor.ARGB32.alpha(alpha_rgba) / 255.0F;

                    // if the alpha of this pixel is within the bounds ... use the pixel from the coverage texture here!
                    if (A < top && A >= bot) {
                        int color_rgba = colorTex.getPixelRGBA(0, i, k);
                        img.setPixelRGBA(i, k, color_rgba);
                    }

                }
            }
        }

        this.texture.upload();

    }

//    private void copyYLayer(Resource, int yLayer)

    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, AbstractClientPlayer pAbstractPlayer, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {

        playerStruggling playerqs = (playerStruggling) pAbstractPlayer;

        PlayerCoverage pC = playerqs.getCoverage();
        if (pC.requiresUpdate) {
            pC.requiresUpdate = false;
            this.updateTexture(pC);
        }

        PlayerCoverageDefaultModel model = this.coverageModel;

        this.getParentModel().copyPropertiesTo(model);
        model.prepareMobModel(pAbstractPlayer, pLimbSwing, pLimbSwingAmount, pPartialTick);
        model.setupAnim(pAbstractPlayer, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);

        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(this.resourcelocation));
        model.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, LivingEntityRenderer.getOverlayCoords(pAbstractPlayer, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);

    }

}
