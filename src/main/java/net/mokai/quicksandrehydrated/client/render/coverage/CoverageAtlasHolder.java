package net.mokai.quicksandrehydrated.client.render.coverage;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;

public class CoverageAtlasHolder extends TextureAtlasHolder {

    public static CoverageAtlasHolder singleton;

    public CoverageAtlasHolder(TextureManager pTextureManager) {
        super(pTextureManager, new ResourceLocation(QuicksandRehydrated.MOD_ID, "textures/atlas/coverages.png"), new ResourceLocation(QuicksandRehydrated.MOD_ID,"coverages"));
        singleton = this;
    }

    public TextureAtlasSprite get(ResourceLocation pLocation) {
        return this.getSprite(pLocation);
    }

}
