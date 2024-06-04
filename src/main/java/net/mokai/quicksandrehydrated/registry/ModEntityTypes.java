package net.mokai.quicksandrehydrated.registry;

import com.google.common.collect.ImmutableSet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mokai.quicksandrehydrated.entity.EntityBubble;
import net.mokai.quicksandrehydrated.entity.EntityHunnibee;

import java.lang.reflect.InvocationTargetException;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, QuicksandRehydrated.MOD_ID);
    public static final DeferredRegister<Item> SPAWN_EGGS = DeferredRegister.create(ForgeRegistries.ITEMS, QuicksandRehydrated.MOD_ID);
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, QuicksandRehydrated.MOD_ID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, QuicksandRehydrated.MOD_ID);



    public static final RegistryObject<EntityType<EntityBubble>> BUBBLE = ENTITIES.register("bubble", () -> EntityType.Builder.<EntityBubble>of(EntityBubble::new, MobCategory.MISC).sized(1f, 1f).fireImmune().noSave().build("bubble"));
    public static final RegistryObject<EntityType<EntityHunnibee>> HUNNIBEE = ENTITIES.register("hunnibee", () -> EntityType.Builder.<EntityHunnibee>of(EntityHunnibee::new, MobCategory.CREATURE).sized(1f, 2.9f).build("hunnibee"));


    public static final RegistryObject<PoiType> MIXER_POI = POI_TYPES.register("mixer_poi", () -> new PoiType(ImmutableSet.copyOf(ModBlocks.MIXER.get().getStateDefinition().getPossibleStates()), 1,1));

    public static final RegistryObject<VillagerProfession> MUDOLOGER = VILLAGER_PROFESSIONS.register("mudologer", () -> new VillagerProfession("mudologer", x -> x.get() == MIXER_POI.get(), x -> x.get() == MIXER_POI.get(), ImmutableSet.of(), ImmutableSet.of(),
                    SoundEvents.BUCKET_FILL));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
        SPAWN_EGGS.register(eventBus);
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);
    }

    public static void registerPOIs() {
        try {
            ObfuscationReflectionHelper.findMethod(PoiType.class, "registerBlockStates", PoiType.class).invoke(null, MIXER_POI.get());
        } catch (InvocationTargetException | IllegalAccessException exception) {
            exception.printStackTrace();
        }
    }





}
