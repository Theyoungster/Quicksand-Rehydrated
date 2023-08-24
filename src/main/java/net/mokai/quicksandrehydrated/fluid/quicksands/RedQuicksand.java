package net.mokai.quicksandrehydrated.fluid.quicksands;

import net.minecraft.world.level.material.Material;

import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.DispenseFluidContainer;
import net.minecraftforge.fluids.FluidType;
import net.mokai.quicksandrehydrated.block.QuicksandInterface;
import net.mokai.quicksandrehydrated.fluid.FluidQuicksandBase;
import org.apache.commons.lang3.Validate;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static net.mokai.quicksandrehydrated.QuicksandRehydrated.MOD_ID;


public class RedQuicksand {

    private static final String name = "red_quicksand";
    private static final ResourceLocation FLUID_STILL = new ResourceLocation("qsrehydrated:block/fluid/" + name + "_still");
    private static final ResourceLocation FLUID_FLOWING = new ResourceLocation("qsrehydrated:block/fluid/" + name + "_flowing");

    public static final ResourceLocation FLUID_OVERLAY = new ResourceLocation("minecraft:block/sand");

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MOD_ID);

    private static ForgeFlowingFluid.Properties makeProperties()
    {
        return new ForgeFlowingFluid.Properties(dry_quicksand_type, dry_quicksand, dry_quicksand_flowing)
                .bucket(dry_quicksand_bucket).block(dry_quicksand_block).levelDecreasePerBlock(5).slopeFindDistance(0);
    }

    public static RegistryObject<FluidType> dry_quicksand_type = FLUID_TYPES.register(name, () -> new FluidType(FluidType.Properties.create())
    {
        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
        {
            consumer.accept(new IClientFluidTypeExtensions()
            {
                @Override
                public ResourceLocation getStillTexture()
                {
                    return FLUID_STILL;
                }

                @Override
                public ResourceLocation getFlowingTexture()
                {
                    return FLUID_FLOWING;
                }

                @Nullable
                @Override
                public ResourceLocation getOverlayTexture()
                {
                    return FLUID_OVERLAY;
                }

            });
        }
    });

    public static RegistryObject<FlowingFluid> dry_quicksand = FLUIDS.register(name, () ->
            new ForgeFlowingFluid.Source(makeProperties())
    );
    public static RegistryObject<FlowingFluid> dry_quicksand_flowing = FLUIDS.register(name + "_flowing", () ->
            new ForgeFlowingFluid.Flowing(makeProperties())
    );

    public static RegistryObject<LiquidBlock> dry_quicksand_block = BLOCKS.register(name + "_block", () ->
            new ThisFluid(dry_quicksand, Properties.of(Material.AIR).noCollission().strength(100.0F).noLootTable())
    );
    public static RegistryObject<Item> dry_quicksand_bucket = ITEMS.register(name + "_bucket", () ->
            new BucketItem(dry_quicksand, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1))
    );


    public RedQuicksand()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::loadComplete);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        FLUID_TYPES.register(modEventBus);
        FLUIDS.register(modEventBus);
    }

    public void loadComplete(FMLLoadCompleteEvent event)
    {
        // some sanity checks
        BlockState state = Fluids.WATER.defaultFluidState().createLegacyBlock();
        BlockState state2 = Fluids.WATER.getFluidType().getBlockForFluidState(null,null,Fluids.WATER.defaultFluidState());
        Validate.isTrue(state.getBlock() == Blocks.WATER && state2 == state);
        ItemStack stack = Fluids.WATER.getFluidType().getBucket(new FluidStack(Fluids.WATER, 1));
        Validate.isTrue(stack.getItem() == Fluids.WATER.getBucket());
        event.enqueueWork(() -> DispenserBlock.registerBehavior(dry_quicksand_bucket.get(), DispenseFluidContainer.getInstance()));
    }






    private static class ThisFluid extends FluidQuicksandBase implements QuicksandInterface {

        public ThisFluid(java.util.function.Supplier<? extends FlowingFluid> supplier, Block.Properties props) {
            super(supplier, props);
        }

        public double getOffset() {
            return 0d; // This value is subtracted from depth for substances that aren't full blocks.
        }

        @Override
        public double getBubblingChance() {
            return .75d;
        }

    }



}
