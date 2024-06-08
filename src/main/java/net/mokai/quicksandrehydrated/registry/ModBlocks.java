package net.mokai.quicksandrehydrated.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.block.*;
import net.mokai.quicksandrehydrated.block.quicksands.*;
import net.mokai.quicksandrehydrated.block.quicksands.core.FlowingQuicksandBase;


import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, QuicksandRehydrated.MOD_ID);


    //TODO: When making new substances, make sure to include them in resources/data/qsrehydrated/tags/blocks/quicksand_drownable.json

    public static final RegistryObject<Block> QUICKSAND = registerBlock("quicksand", () -> new Quicksand( BlockBehaviour.Properties.copy(Blocks.SAND).noCollission().requiresCorrectToolForDrops()
            .isViewBlocking((p_187417_, p_187418_, p_187419_) -> { return true;})
    ));
    public static final RegistryObject<Block> LIVING_SLIME = registerBlock("living_slime", () -> new LivingSlime( BlockBehaviour.Properties.copy(Blocks.SLIME_BLOCK).noCollission().requiresCorrectToolForDrops()
            .isViewBlocking((p_187417_, p_187418_, p_187419_) -> { return true;})
    ));
    public static final RegistryObject<Block> DEEP_MUD = registerBlock("deep_mud", () -> new DeepMudBlock( BlockBehaviour.Properties.copy(Blocks.MUD).noCollission().requiresCorrectToolForDrops()
            .isViewBlocking((p_187417_, p_187418_, p_187419_) -> { return true;})
    ));
    public static final RegistryObject<Block> SOFT_QUICKSAND = registerBlock("soft_quicksand", () -> new SoftQuicksand(BlockBehaviour.Properties.copy(Blocks.SAND).noCollission().requiresCorrectToolForDrops().noOcclusion()
            .isViewBlocking((p_187417_, p_187418_, p_187419_) -> { return p_187417_.getValue(FlowingQuicksandBase.LEVEL) >= 4;})));



    //public static final RegistryObject<Block> SOFT_COVER = registerBlock("loose_moss", () -> new GoundCover(BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET)));

    public static final RegistryObject<Block> MIXER = registerBlock("mixer", () -> new MixerBlock(BlockBehaviour.Properties.of().strength(6f).requiresCorrectToolForDrops().noOcclusion()));

    private static Collection<ItemStack> REGLIST;

    public static Collection<ItemStack> setupCreativeGroups() {
        REGLIST = new ArrayList<>();
        addItem(QUICKSAND);
        addItem(LIVING_SLIME);
        addItem(DEEP_MUD);
        addItem(SOFT_QUICKSAND);
        addItem(MIXER);
        return REGLIST;
    }

    public static void addItem(RegistryObject<Block> b) {
        REGLIST.add(b.get().asItem().getDefaultInstance());
    }

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
