package net.mokai.quicksandrehydrated.registry;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.client.render.BubbleModel;
import net.mokai.quicksandrehydrated.client.render.BubbleRenderer;
import net.mokai.quicksandrehydrated.client.render.HunnibeeModel;
import net.mokai.quicksandrehydrated.client.render.HunnibeeRenderer;

import static net.mokai.quicksandrehydrated.QuicksandRehydrated.MOD_ID;

@Mod.EventBusSubscriber (modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntitySetup {

    // CLIENT SETUP SECTION

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.BUBBLE.get(), m -> new BubbleRenderer(m, new BubbleModel(m.bakeLayer(BUBBLE))));
        event.registerEntityRenderer(ModEntityTypes.HUNNIBEE.get(), m -> new HunnibeeRenderer(m, new HunnibeeModel(m.bakeLayer(HUNNIBEE))));
    }

    // LAYERS DEFINITION SECTION //

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {

        event.registerLayerDefinition(BUBBLE, BubbleModel::create);
        event.registerLayerDefinition(ModModelLayers.HUNNIBEE_LAYER, HunnibeeModel::createBodyLayer);

    }

    // MODEL LAYERS SECTION //

    public static final ModelLayerLocation BUBBLE = register("bubble");

    public static final ModelLayerLocation HUNNIBEE = register("hunnibee_layer");






    private static ModelLayerLocation register(String name) { return register(name, "main"); }

    private static ModelLayerLocation register(String name, String thing2) { return new ModelLayerLocation(new ResourceLocation(QuicksandRehydrated.MOD_ID, name), thing2); }

}
