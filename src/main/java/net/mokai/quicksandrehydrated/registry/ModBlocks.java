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
import net.mokai.quicksandrehydrated.block.quicksands.core.QuicksandBase;
import net.mokai.quicksandrehydrated.block.quicksands.core.QuicksandBehavior;


import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, QuicksandRehydrated.MOD_ID);

    public static final RegistryObject<Block> MIXER = registerBlock("mixer", () -> new MixerBlock(BlockBehaviour.Properties.of().strength(6f).requiresCorrectToolForDrops().noOcclusion()));


    //public static final RegistryObject<Block> SOFT_COVER = registerBlock("loose_moss", () -> new GoundCover(BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET)));





    // ----------------------------------- QUICKSAND REGISTRY :O :O :O -----------------------------

    //TODO: When making new substances, make sure to include them in resources/data/qsrehydrated/tags/blocks/quicksand_drownable.json

    private final static BlockBehaviour.Properties baseBehavior = BlockBehaviour.Properties.copy(Blocks.SAND).noCollission().requiresCorrectToolForDrops()
            .noOcclusion().isViewBlocking((A, B, C) -> true);
    private final static BlockBehaviour.Properties baseFlowingBehavior = BlockBehaviour.Properties.copy(Blocks.SAND).noCollission().requiresCorrectToolForDrops()
            .noOcclusion().isViewBlocking((A, B, C) -> {return A.getValue(FlowingQuicksandBase.LEVEL) >= 4;});



    public static final RegistryObject<Block> QUICKSAND = registerBlock("quicksand", () -> new Quicksand( baseBehavior, new QuicksandBehavior()
            .setCoverageTexture("quicksand_coverage")
    ));
    public static final RegistryObject<Block> LIVING_SLIME = registerBlock("living_slime", () -> new LivingSlime( baseBehavior, new QuicksandBehavior()));
    public static final RegistryObject<Block> DEEP_MUD = registerBlock("deep_mud", () -> new DeepMudBlock( baseBehavior, new QuicksandBehavior()));
    public static final RegistryObject<Block> SOFT_QUICKSAND = registerBlock("soft_quicksand", () -> new FlowingQuicksandBase(baseFlowingBehavior, new QuicksandBehavior()));








    // ----------------------------------- Done! -----------------------------


    private static Collection<ItemStack> CREATIVELIST;

    public static void addItem(RegistryObject<Block> b) {CREATIVELIST.add(b.get().asItem().getDefaultInstance());}

    public static Collection<ItemStack> setupCreativeGroups() {
        CREATIVELIST = new ArrayList<>();
        addItem(QUICKSAND);
        addItem(LIVING_SLIME);
        addItem(DEEP_MUD);
        addItem(SOFT_QUICKSAND);
        addItem(MIXER);
        return CREATIVELIST;
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
