package net.mokai.quicksandrehydrated.registry;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.client.model.armor.SnorkelModel;
import net.mokai.quicksandrehydrated.client.render.*;
import net.mokai.quicksandrehydrated.client.render.mob.*;
import net.mokai.quicksandrehydrated.entity.*;

import static net.mokai.quicksandrehydrated.QuicksandRehydrated.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntitySetup {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.BUBBLE.get(),
                m -> new BubbleRenderer(m, new BubbleModel(m.bakeLayer(BUBBLE))));
        event.registerEntityRenderer(ModEntityTypes.HUNNIBEE.get(),
                m -> new HunnibeeRenderer(m, new HunnibeeModel(m.bakeLayer(HUNNIBEE_LL))));
        event.registerEntityRenderer(ModEntityTypes.TAR_GOLEM.get(),
                m -> new TarGolemRenderer(m, new TarGolemModel(m.bakeLayer(TAR_GOLEM_LL))));

        event.registerEntityRenderer(ModEntityTypes.TAR_SLIME.get(), TarSlimeRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.MUDDY_BLOB.get(), MuddyBlobRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.SAND_BLOB.get(),  SandBlobRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.CAVE_BLOB.get(),
                m -> new CaveBlobRenderer(m, new CaveBlobModel<>(m.bakeLayer(CAVE_BLOB_SOLID_LL))));

        System.out.println("Renderers set up");
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BUBBLE, BubbleModel::create);
        event.registerLayerDefinition(ModModelLayers.HUNNIBEE_LAYER, HunnibeeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.TAR_GOLEM_LAYER, TarGolemModel::createBodyLayer);

        event.registerLayerDefinition(ModModelLayers.CAVE_BLOB_CLEAR_LAYER, CaveBlobModel::createOuterBodyLayer);
        event.registerLayerDefinition(ModModelLayers.CAVE_BLOB_SOLID_LAYER, CaveBlobModel::createInnerBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SNORKEL, () -> SnorkelModel.createArmorLayer(new CubeDeformation(0)));
    }

    public static final ModelLayerLocation BUBBLE       = register("bubble");
    public static final ModelLayerLocation HUNNIBEE_LL  = register("hunnibee_layer");
    public static final ModelLayerLocation TAR_GOLEM_LL = register("tar_golem_layer");
    public static final ModelLayerLocation CAVE_BLOB_SOLID_LL = register("cave_blob_solid_layer"); // inner
    public static final ModelLayerLocation CAVE_BLOB_CLEAR_LL = register("cave_blob_clear_layer"); // outer

    private static ModelLayerLocation register(String name) { return register(name, "main"); }
    private static ModelLayerLocation register(String name, String layer) {
        return new ModelLayerLocation(new ResourceLocation(QuicksandRehydrated.MOD_ID, name), layer);
    }
}
