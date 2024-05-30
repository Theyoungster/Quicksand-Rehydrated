package net.mokai.quicksandrehydrated.registry;

import net.minecraft.client.particle.DustParticle;
import net.minecraft.client.particle.DustParticleBase;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.particle.TestDustParticle;
import org.joml.Vector3f;

public class ModParticles {

    public static final Vector3f MUD_COLOR = Vec3.fromRGB24(3815485).toVector3f();

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, QuicksandRehydrated.MOD_ID);

    public static final RegistryObject<SimpleParticleType> QUICKSAND_BUBBLE_PARTICLES =
            PARTICLE_TYPES.register("quicksand_bubble_particles", () -> new SimpleParticleType(true));


    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }

}
