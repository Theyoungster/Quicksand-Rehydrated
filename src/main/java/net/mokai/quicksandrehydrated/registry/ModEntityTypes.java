package net.mokai.quicksandrehydrated.registry;

import net.minecraft.world.entity.Entity;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mokai.quicksandrehydrated.entity.EntityBubble;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, QuicksandRehydrated.MOD_ID);

    public static final RegistryObject<EntityType<EntityBubble>> BUBBLE =
            ENTITY_TYPES.register("bubble",
                    () -> EntityType.Builder.of(EntityBubble::new, MobCategory.MONSTER)
                            .sized(1f, 1f)
                            .build(new ResourceLocation(QuicksandRehydrated.MOD_ID, "bubble").toString()));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
