package net.mokai.quicksandrehydrated.registry;

import net.minecraftforge.fluids.FluidType;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.mokai.quicksandrehydrated.fluid.quicksands.DryQuicksandHolder;

public class ModFluids {

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, QuicksandRehydrated.MOD_ID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, QuicksandRehydrated.MOD_ID);

    public static final DryQuicksandHolder DRYQUICKSAND = new DryQuicksandHolder();
    /*
    public static final RegistryObject<FlowingFluid> DRY_QUICKSAND = FLUIDS.register("dry_quicksand",
            () -> new ForgeFlowingFluid.Source(ModFluids.DRY_QUICKSAND_PROPERTIES));
    public static final RegistryObject<FlowingFluid> FLOWING_DRY_QUICKSAND = FLUIDS.register("flowing_dry_quicksand",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.DRY_QUICKSAND_PROPERTIES));


    public static final ForgeFlowingFluid.Properties DRY_QUICKSAND_PROPERTIES = new ForgeFlowingFluid.Properties(
            ModFluidTypes.DRY_QUICKSAND_TYPE, DRY_QUICKSAND, FLOWING_DRY_QUICKSAND)
            .slopeFindDistance(2).levelDecreasePerBlock(2).block(ModBlocks.DRY_QUICKSAND)
            .bucket(ModItems.DRY_QUICKSAND_BUCKET);
*/




    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }
}
