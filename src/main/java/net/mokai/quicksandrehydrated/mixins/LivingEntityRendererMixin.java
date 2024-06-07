package net.mokai.quicksandrehydrated.mixins;

import com.mojang.blaze3d.shaders.AbstractUniform;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.mokai.quicksandrehydrated.client.render.MyRenderTypes;
import net.mokai.quicksandrehydrated.entity.playerStruggling;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;


@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

//    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
//    private void modifyGetRenderType(LivingEntity pLivingEntity, boolean pBodyVisible, boolean pTranslucent,
//            boolean pGlowing, CallbackInfoReturnable<RenderType> cir) {
//
//        if (pLivingEntity instanceof AbstractClientPlayer) {
//
//            AbstractClientPlayer abstractPlayer = (AbstractClientPlayer) pLivingEntity;
//            playerStruggling pS = (playerStruggling) abstractPlayer;
//            double coverPercent = pS.getCoveragePercent();
//            if (coverPercent > 0.0) {
//
//                LivingEntityRenderer ler = (LivingEntityRenderer) (Object) this;
//                ResourceLocation playerSkin = ler.getTextureLocation(pLivingEntity);
//                ResourceLocation coverageMask = new ResourceLocation(pS.getCoverageTexture());
//
//                AbstractUniform coverPercentUniform = MyRenderTypes.CustomRenderTypes.coverageShader.safeGetUniform("coverPercent");
//                coverPercentUniform.set((float) coverPercent);
//
//                cir.setReturnValue(MyRenderTypes.coverage(playerSkin, coverageMask));
//
//            }
//        }
//
//    }

}
