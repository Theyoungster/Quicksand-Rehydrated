package net.mokai.quicksandrehydrated.registry;

import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mokai.quicksandrehydrated.entity.EntityBubble;

public class ModEvents {
    @Mod.EventBusSubscriber(modid = QuicksandRehydrated.MOD_ID)
    public static class ForgeEvents {

    }

    @Mod.EventBusSubscriber(modid = QuicksandRehydrated.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {
        @SubscribeEvent
        public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
            //event.put(ModEntityTypes.BUBBLE.get(), EntityBubble.getBubbleAttributes().build());

        }
    }
}
