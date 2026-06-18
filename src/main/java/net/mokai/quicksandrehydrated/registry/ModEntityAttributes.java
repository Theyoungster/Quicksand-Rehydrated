package net.mokai.quicksandrehydrated.registry;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.mokai.quicksandrehydrated.entity.*;

@Mod.EventBusSubscriber(modid = QuicksandRehydrated.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntityAttributes {

    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.HUNNIBEE.get(), EntityHunnibee.setAttributes());
        event.put(ModEntityTypes.TAR_GOLEM.get(), EntityTarGolem.setAttributes());
        event.put(ModEntityTypes.TAR_SLIME.get(),   EntityTarSlime.createAttributes().build());
        event.put(ModEntityTypes.MUDDY_BLOB.get(),  EntityMuddyBlob.createAttributes().build());
        event.put(ModEntityTypes.SAND_BLOB.get(),   EntitySandBlob.createAttributes().build());
        event.put(ModEntityTypes.CAVE_BLOB.get(),   EntityCaveBlob.setAttributes());
    }

}