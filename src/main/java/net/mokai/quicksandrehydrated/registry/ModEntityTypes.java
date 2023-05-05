package net.mokai.quicksandrehydrated.registry;

import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, QuicksandRehydrated.MOD_ID);




    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
