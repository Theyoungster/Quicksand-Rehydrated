package net.mokai.quicksandrehydrated.registry;

import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.block.custom.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mokai.quicksandrehydrated.block.quicksands.*;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, QuicksandRehydrated.MOD_ID);

    // Going forward, each item should include this in its MakeProperties() method instead, so as to reduce clutter here.

    public static final RegistryObject<Block> QUICKSAND = registerBlock("quicksand", () -> new Quicksand( BlockBehaviour.Properties.copy(Blocks.SAND).noCollission().requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SOFT_QUICKSAND = registerBlock("soft_quicksand", () -> new SoftQuicksand(BlockBehaviour.Properties.copy(Blocks.SAND).noCollission().requiresCorrectToolForDrops()));


    public static final RegistryObject<Block> MIXER = registerBlock("mixer", () -> new MixerBlock(BlockBehaviour.Properties.of(Material.METAL).strength(6f).requiresCorrectToolForDrops().noOcclusion()));


    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
