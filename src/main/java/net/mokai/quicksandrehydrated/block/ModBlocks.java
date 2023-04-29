package net.mokai.quicksandrehydrated.block;

import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.block.custom.*;
import net.mokai.quicksandrehydrated.fluid.ModFluids;
import net.mokai.quicksandrehydrated.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mokai.quicksandrehydrated.block.quicksands.*;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, QuicksandRehydrated.MOD_ID);


    public static final RegistryObject<Block> QUICKSAND = registerBlock("quicksand",
            () -> new Quicksand( BlockBehaviour.Properties.copy(Blocks.SAND)
                    .noCollission()
                    .requiresCorrectToolForDrops()));



    public static final RegistryObject<Block> SOFT_QUICKSAND = registerBlock("soft_quicksand",
            () -> new SoftQuicksand(BlockBehaviour.Properties.copy(Blocks.SAND)
                    .noCollission()
                    .requiresCorrectToolForDrops()));


    public static final RegistryObject<LiquidBlock> DRY_QUICKSAND = BLOCKS.register("dry_quicksand",
            () -> new LiquidBlock(ModFluids.DRY_QUICKSAND, BlockBehaviour.Properties.copy(Blocks.WATER)));

    public static final RegistryObject<Block> MIXER = registerBlock("mixer",
            () -> new MixerBlock(BlockBehaviour.Properties.of(Material.METAL)
                    .strength(6f).requiresCorrectToolForDrops().noOcclusion()));


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
