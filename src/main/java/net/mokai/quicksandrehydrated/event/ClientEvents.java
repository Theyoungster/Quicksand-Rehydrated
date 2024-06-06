package net.mokai.quicksandrehydrated.event;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.mokai.quicksandrehydrated.client.render.MyRenderTypes;
import net.mokai.quicksandrehydrated.client.render.StruggleHudOverlay;
import net.mokai.quicksandrehydrated.entity.playerStruggling;
import net.mokai.quicksandrehydrated.util.Keybinding;

import java.io.IOException;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = QuicksandRehydrated.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (Keybinding.STRUGGLE_KEY.consumeClick()) {
                // cast to playerStruggling interface
                ((playerStruggling) Minecraft.getInstance().player).attemptStruggle();
            }
        }

    }

    @Mod.EventBusSubscriber(modid = QuicksandRehydrated.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {

        @SubscribeEvent
        public void onRenderPlayer(RenderPlayerEvent.Pre event) {

            //MyRenderTypes.CustomRenderTypes.coverageShader.safeGetUniform("")

        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            //event.registerEntityRenderer(ModEntityTypes.BUBBLE.get(), BubbleRenderer::new);
        }

        @SubscribeEvent
        public static void shaderRegistry(RegisterShadersEvent event) throws IOException
        {
            // Adds a shader to the list, the callback runs when loading is complete.
            event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(QuicksandRehydrated.MOD_ID,"rendertype_coverage"), DefaultVertexFormat.NEW_ENTITY), (thang) -> {
                MyRenderTypes.CustomRenderTypes.coverageShader = thang;
            });
        }

        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("struggle", StruggleHudOverlay.HUD_STRUGGLE);
        }

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(Keybinding.STRUGGLE_KEY);
        }

    }
}