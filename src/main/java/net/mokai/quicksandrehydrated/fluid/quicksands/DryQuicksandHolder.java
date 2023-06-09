package net.mokai.quicksandrehydrated.fluid.quicksands;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

import net.mokai.quicksandrehydrated.fluid.quicksands.blocks.DryQuicksandBlock;
import net.mokai.quicksandrehydrated.registry.ModBlocks;
import net.mokai.quicksandrehydrated.registry.ModFluids;
import net.mokai.quicksandrehydrated.registry.ModItems;

//Thanks to example https://github.com/MinecraftForge/MinecraftForge/blob/1.15.x/src/test/java/net/minecraftforge/debug/fluid/NewFluidTest.java
public class DryQuicksandHolder {

    private static final String id = "dry_quicksand";
    private static final ResourceLocation FLUID_STILL = new ResourceLocation("qsrehydrated:block/" + id + "_still");
    private static final ResourceLocation FLUID_FLOWING = new ResourceLocation("qsrehydrated:block/" + id + "_flowing");
    public static final int COLOR = 0x4B261F;
    public static RegistryObject<FlowingFluid> STILL = ModFluids.FLUIDS.register(id, () -> new DryQuicksandBlock.Source(makeProperties()));
    public static RegistryObject<FlowingFluid> FLOWING = ModFluids.FLUIDS.register(id + "_flowing", () -> new DryQuicksandBlock.Flowing(makeProperties()));
    public static RegistryObject<LiquidBlock> BLOCK = ModBlocks.BLOCKS.register(id + "_still", () -> new DryQuicksandBlock(STILL, Block.Properties.of(Material.WATER).strength(100.0F).noLootTable()));
    public static RegistryObject<Item> BUCKET = ModItems.ITEMS.register(id + "_bucket", () -> new BucketItem(STILL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));


    public static RegistryObject<FluidType> FLUID_FLOWING_TYPE = ModFluids.FLUID_TYPES.register(id, () -> new FluidType(FluidType.Properties.create()) {
        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions() {

                @Override
                public ResourceLocation getStillTexture() {
                    return FLUID_STILL;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return FLUID_FLOWING;
                }

                //        @Nullable
                @Override
                public ResourceLocation getOverlayTexture() {
                    return null;
                }
            });
        }
    });



    private static ForgeFlowingFluid.Properties makeProperties() {
        return new ForgeFlowingFluid.Properties(FLUID_FLOWING_TYPE, STILL, FLOWING)
                .bucket(BUCKET)
                .block(BLOCK);
    }


}
