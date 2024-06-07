package net.mokai.quicksandrehydrated.client.render.coverage;

import net.minecraft.client.renderer.texture.DynamicTexture;

public interface playerRendererInterface {

    DynamicTexture tex = new DynamicTexture(64, 64, false);
    default DynamicTexture getTex() {return this.tex;}

}
