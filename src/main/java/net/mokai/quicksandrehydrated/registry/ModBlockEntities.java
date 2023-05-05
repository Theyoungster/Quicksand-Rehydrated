package net.mokai.quicksandrehydrated.registry;

import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.block.entity.MixerEntity;
import net.mokai.quicksandrehydrated.registry.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, QuicksandRehydrated.MOD_ID);

    public static final RegistryObject<BlockEntityType<MixerEntity>> MIXER =
            BLOCK_ENTITIES.register("mixer", () ->
                    BlockEntityType.Builder.of(MixerEntity::new,
                            ModBlocks.MIXER.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
