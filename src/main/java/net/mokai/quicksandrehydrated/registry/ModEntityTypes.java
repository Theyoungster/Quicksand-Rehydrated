package net.mokai.quicksandrehydrated.registry;

import net.minecraft.world.item.Item;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mokai.quicksandrehydrated.entity.EntityBubble;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, QuicksandRehydrated.MOD_ID);
    public static final DeferredRegister<Item> SPAWN_EGGS = DeferredRegister.create(ForgeRegistries.ITEMS, QuicksandRehydrated.MOD_ID);


    public static final RegistryObject<EntityType<EntityBubble>> BUBBLE = ENTITIES.register("bubble", () -> EntityType.Builder.<EntityBubble>of(EntityBubble::new, MobCategory.MISC).sized(1f, 1f).fireImmune().build("bubble"));



    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus); SPAWN_EGGS.register(eventBus);
    }



}
