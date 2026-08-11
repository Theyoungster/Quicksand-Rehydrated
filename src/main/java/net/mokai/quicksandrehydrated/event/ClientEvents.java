package net.mokai.quicksandrehydrated.event;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.client.render.ModRenderTypes;
import net.mokai.quicksandrehydrated.client.render.StruggleHudOverlay;
import net.mokai.quicksandrehydrated.client.render.coverage.CoverageAtlasHolder;
import net.mokai.quicksandrehydrated.client.render.coverage.CoverageLayer;
import net.mokai.quicksandrehydrated.client.render.coverage.PlayerCoverageDefaultModel;
import net.mokai.quicksandrehydrated.client.render.coverage.PlayerCoverageSlimModel;
import net.mokai.quicksandrehydrated.entity.playerStruggling;
import net.mokai.quicksandrehydrated.networking.ModMessages;
import net.mokai.quicksandrehydrated.networking.packet.StruggleDownC2SPacket;
import net.mokai.quicksandrehydrated.networking.packet.StruggleReleaseC2SPacket;
import net.mokai.quicksandrehydrated.particle.QuicksandBubbleParticle;
import net.mokai.quicksandrehydrated.registry.ModModelLayers;
import net.mokai.quicksandrehydrated.registry.ModParticles;
import net.mokai.quicksandrehydrated.util.Keybinding;

import java.io.IOException;

public class ClientEvents {

    @Mod.EventBusSubscriber(modid = QuicksandRehydrated.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }

            Player player = event.player;
            Player localPlayer = Minecraft.getInstance().player;
            if (localPlayer == null || player != localPlayer) {
                return;
            }
            playerStruggling strugglingPlayer = (playerStruggling) player;

            if (player.level().isClientSide()) {

                boolean keyDown = Keybinding.STRUGGLE_KEY.isDown();
                boolean flagHolding = strugglingPlayer.getHoldingStruggle();

                if (keyDown && !flagHolding) {
                    // key IS down this tick, flagHolding is NOT
                    ModMessages.sendToServer(new StruggleDownC2SPacket());
                } else if (!keyDown && flagHolding) {
                    // key is NOT DOWN this tick, just released
                    ModMessages.sendToServer(new StruggleReleaseC2SPacket());
                }

                strugglingPlayer.setHoldingStruggle(keyDown);

            }

        }

    }

    @Mod.EventBusSubscriber(modid = QuicksandRehydrated.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {


        @SubscribeEvent
        public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(new CoverageAtlasHolder(Minecraft.getInstance().getTextureManager()));
        }

        @SubscribeEvent
        public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions e) {
            e.registerLayerDefinition(ModModelLayers.COVERAGE_LAYER_DEFAULT, PlayerCoverageDefaultModel::createBodyLayer);
            e.registerLayerDefinition(ModModelLayers.COVERAGE_LAYER_SLIM, PlayerCoverageSlimModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void addLayers(EntityRenderersEvent.AddLayers e) {
            System.out.println("registering coverage layers");
            PlayerRenderer renderer = (PlayerRenderer)e.getSkin("default");
            renderer.addLayer(new CoverageLayer(renderer, false));
            renderer = (PlayerRenderer)e.getSkin("slim");
            renderer.addLayer(new CoverageLayer(renderer, true));
        }

        @SubscribeEvent
        public static void shaderRegistry(RegisterShadersEvent event) throws IOException
        {
            // Adds a shader to the list, the callback runs when loading is complete.
            event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(QuicksandRehydrated.MOD_ID,"rendertype_coverage"), DefaultVertexFormat.NEW_ENTITY), (thang) -> {
                ModRenderTypes.CustomRenderTypes.coverageShader = thang;
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
        
        @SubscribeEvent
        public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
            // Register quicksand bubble particles
            event.registerSpriteSet(ModParticles.QUICKSAND_BUBBLE_PARTICLES.get(),
                    QuicksandBubbleParticle.Provider::new);
            // Washing particles removed
        }

    }
}