package net.mokai.quicksandrehydrated.registry;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;

@Mod.EventBusSubscriber(modid = QuicksandRehydrated.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerMobAttributes(EntityAttributeCreationEvent event) {
        //event.put(ModEntityTypes.HUNNIBEE.get(), EntityHunnibee.createMobAttributes().build());
    }
}