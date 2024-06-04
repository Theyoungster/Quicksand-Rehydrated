package net.mokai.quicksandrehydrated.registry;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.client.render.*;
import net.mokai.quicksandrehydrated.entity.*;

import static net.mokai.quicksandrehydrated.QuicksandRehydrated.MOD_ID;
import static net.mokai.quicksandrehydrated.registry.ModEntityTypes.TAR_GOLEM;

@Mod.EventBusSubscriber (modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntitySetup {

    // CLIENT SETUP SECTION

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.BUBBLE.get(), m -> new BubbleRenderer(m, new BubbleModel(m.bakeLayer(BUBBLE))));
        event.registerEntityRenderer(ModEntityTypes.HUNNIBEE.get(), m -> new HunnibeeRenderer(m, new HunnibeeModel(m.bakeLayer(HUNNIBEE_LL))));
        event.registerEntityRenderer(ModEntityTypes.TAR_GOLEM.get(), m -> new TarGolemRenderer(m, new TarGolemModel(m.bakeLayer(TAR_GOLEM_LL))));
    }

    // LAYERS DEFINITION SECTION //

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {

        event.registerLayerDefinition(BUBBLE, BubbleModel::create);
        event.registerLayerDefinition(ModModelLayers.HUNNIBEE_LAYER, HunnibeeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.TAR_GOLEM_LAYER, TarGolemModel::createBodyLayer);

    }

    // SET ENTITY ATTRIBUTES ///

    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.HUNNIBEE.get(), EntityHunnibee.setAttributes());
        event.put(TAR_GOLEM.get(), EntityTarGolem.setAttributes());
    }

    // MODEL LAYERS SECTION //

    public static final ModelLayerLocation BUBBLE = register("bubble");
    public static final ModelLayerLocation HUNNIBEE_LL = register("hunnibee_layer");
    public static final ModelLayerLocation TAR_GOLEM_LL = register("tar_golem_layer");





    private static ModelLayerLocation register(String name) { return register(name, "main"); }

    private static ModelLayerLocation register(String name, String thing2) { return new ModelLayerLocation(new ResourceLocation(QuicksandRehydrated.MOD_ID, name), thing2); }

}
