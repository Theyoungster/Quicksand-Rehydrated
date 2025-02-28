package net.mokai.quicksandrehydrated.mixins;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.mokai.quicksandrehydrated.client.render.coverage.CoverageLayer;
import net.mokai.quicksandrehydrated.client.render.coverage.PlayerCoverageDefaultModel;
import net.mokai.quicksandrehydrated.client.render.coverage.PlayerCoverageSlimModel;
import net.mokai.quicksandrehydrated.client.render.coverage.playerRendererInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin implements playerRendererInterface {
    public DynamicTexture tex = new DynamicTexture(64, 64, false);
    public DynamicTexture getTex() {return this.tex;}

}

// ?
//    @Inject(method = "<init>", at = @At("TAIL"))
//    private void onConstructor(EntityRendererProvider.Context p_174557_, boolean isSlim, CallbackInfo ci) {
//        PlayerRenderer thisObject = (PlayerRenderer)(Object)this;
//        if (isSlim) {
//            thisObject.addLayer(new CoverageLayer(thisObject));
//        }
//        else {
//            thisObject.addLayer(new CoverageLayer<>(thisObject));
//        }
//    }




