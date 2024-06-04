package net.mokai.quicksandrehydrated.registry;

import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.entity.EntityHunnibee;
import net.mokai.quicksandrehydrated.particle.QuicksandBubbleParticle;

@Mod.EventBusSubscriber(modid = QuicksandRehydrated.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event)
    {
        event.register(ModParticles.QUICKSAND_BUBBLE_PARTICLES.get(), QuicksandBubbleParticle.Provider::new);
    }
}