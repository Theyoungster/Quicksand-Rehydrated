package net.mokai.quicksandrehydrated.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.resources.ResourceLocation;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import org.apache.commons.codec.language.bm.Rule;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MyRenderTypes
{

    // TODO this should *NOT* be here
    public static final ResourceLocation COVERAGE_MASK_LOCATION = new ResourceLocation(QuicksandRehydrated.MOD_ID,"textures/entity/coverage/coverage_mask.png");
    public static final ResourceLocation DUMMY_TEXTURE_LOCATION = new ResourceLocation("na");



    // Accessor functon, ensures that you don't use the raw methods below unintentionally.
    public static RenderType coverage(ResourceLocation playerSkin, ResourceLocation coverageMask)
    {
        return CustomRenderTypes.COVERAGE.apply(playerSkin, coverageMask);
    }

    // Keep private because this stuff isn't meant to be public
    public static class CustomRenderTypes extends RenderType
    {
        // Holds the object loaded via RegisterShadersEvent
        public static ShaderInstance coverageShader;

        // Shader state for use in the render type, the supplier ensures it updates automatically with resource reloads
        private static final ShaderStateShard RENDERTYPE_COVERAGE_SHADER = new ShaderStateShard(() -> coverageShader);

        // Dummy constructor needed to make java happy
        private CustomRenderTypes(String s, VertexFormat v, VertexFormat.Mode m, int i, boolean b, boolean b2, Runnable r, Runnable r2)
        {
            super(s, v, m, i, b, b2, r, r2);
            throw new IllegalStateException("This class is not meant to be constructed!");
        }

        // The memoize caches the output value for each input, meaning the expensive registration process doesn't have to rerun
        public static BiFunction<ResourceLocation, ResourceLocation, RenderType> COVERAGE = Util.memoize(CustomRenderTypes::coverage);





        // Defines the RenderType. Make sure the name is unique by including your MODID in the name.

        private static RenderType coverage(ResourceLocation skinLocation, ResourceLocation coverLocation)
        {

            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_COVERAGE_SHADER)
                    .setTextureState(RenderStateShard.MultiTextureStateShard.builder()
                        .add(skinLocation, false, false)
                            .add(DUMMY_TEXTURE_LOCATION, false, false)
                            .add(DUMMY_TEXTURE_LOCATION, false, false)
                            .add(COVERAGE_MASK_LOCATION, false, false)
                        .add(coverLocation, false, false)
                        .build()
                    )
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true);
            return create("qsrehydrated_coverage", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$compositestate);



        }
    }
}