package net.mokai.quicksandrehydrated.registry;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;

public class ModModelLayers {
    public static final ModelLayerLocation HUNNIBEE_LAYER = new ModelLayerLocation(
            new ResourceLocation(QuicksandRehydrated.MOD_ID, "hunnibee_layer"), "main");
    public static final ModelLayerLocation TAR_GOLEM_LAYER = new ModelLayerLocation(
            new ResourceLocation(QuicksandRehydrated.MOD_ID, "tar_golem_layer"), "main");

    public static final ModelLayerLocation STICKY_SLIME_LAYER = new ModelLayerLocation(
            new ResourceLocation(QuicksandRehydrated.MOD_ID, "sticky_slime_layer"), "main");
    public static final ModelLayerLocation BUBBLE_LAYER = new ModelLayerLocation(
            new ResourceLocation(QuicksandRehydrated.MOD_ID, "bubble_layer"), "main");
}
