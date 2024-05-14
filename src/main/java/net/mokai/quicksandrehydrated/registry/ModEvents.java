package net.mokai.quicksandrehydrated.registry;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mokai.quicksandrehydrated.entity.EntityBubble;
import net.mokai.quicksandrehydrated.particle.QuicksandBubbleParticle;

import net.mokai.quicksandrehydrated.entity.EntityHunnibee;


public class ModEvents {
    @Mod.EventBusSubscriber(modid = QuicksandRehydrated.MOD_ID)
    public static class ForgeEvents {

    }

    @Mod.EventBusSubscriber(modid = QuicksandRehydrated.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {
        @SubscribeEvent
        public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
            //event.put(ModEntityTypes.BUBBLE.get(), EntityBubble.getBubbleAttributes().build());
            event.put(ModEntityTypes.HUNNIBEE.get(), EntityHunnibee.setAttributes());

        }

        @SubscribeEvent
        public static void registerParticleFactories(final RegisterParticleProvidersEvent event)
        {
            event.register(ModParticles.QUICKSAND_BUBBLE_PARTICLES.get(), QuicksandBubbleParticle.Provider::new);
        }


    }
}
