package net.mokai.quicksandrehydrated.fluid;

import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.block.ModBlocks;
import net.mokai.quicksandrehydrated.item.ModItems;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, QuicksandRehydrated.MOD_ID);

    public static final RegistryObject<FlowingFluid> DRY_QUICKSAND = FLUIDS.register("dry_quicksand",
            () -> new ForgeFlowingFluid.Source(ModFluids.DRY_QUICKSAND_PROPERTIES));
    public static final RegistryObject<FlowingFluid> FLOWING_DRY_QUICKSAND = FLUIDS.register("flowing_dry_quicksand",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.DRY_QUICKSAND_PROPERTIES));


    public static final ForgeFlowingFluid.Properties DRY_QUICKSAND_PROPERTIES = new ForgeFlowingFluid.Properties(
            ModFluidTypes.DRY_QUICKSAND_TYPE, DRY_QUICKSAND, FLOWING_DRY_QUICKSAND)
            .slopeFindDistance(2).levelDecreasePerBlock(2).block(ModBlocks.DRY_QUICKSAND)
            .bucket(ModItems.DRY_QUICKSAND_BUCKET);



    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}
