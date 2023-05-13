package net.mokai.quicksandrehydrated.registry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.client.render.BubbleModel;
import net.mokai.quicksandrehydrated.client.render.BubbleRenderer;
import net.mokai.quicksandrehydrated.entity.EntityBubble;

import static net.mokai.quicksandrehydrated.QuicksandRehydrated.MOD_ID;

@Mod.EventBusSubscriber (modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntitySetup {

    // CLIENT SETUP SECTION

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.BUBBLE.get(), m -> new BubbleRenderer(m, new BubbleModel(m.bakeLayer(BUBBLE))));

    }

    // LAYERS DEFINITION SECTION //

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {

        event.registerLayerDefinition(BUBBLE, BubbleModel::create);

    }

    // MODEL LAYERS SECTION //

    public static final ModelLayerLocation BUBBLE = register("bubble");






    private static ModelLayerLocation register(String p_171294_) { return register(p_171294_, "main"); }
    private static ModelLayerLocation register(String p_171301_, String p_171302_) { return new ModelLayerLocation(new ResourceLocation(QuicksandRehydrated.MOD_ID, p_171301_), p_171302_); }

}
