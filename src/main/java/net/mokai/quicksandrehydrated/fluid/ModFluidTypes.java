package net.mokai.quicksandrehydrated.fluid;

import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.joml.Vector3f;

public class ModFluidTypes {
    public static final ResourceLocation DRY_QUICKSAND_STILL_RL = new ResourceLocation(QuicksandRehydrated.MOD_ID, "block/dry_quicksand_still");
    public static final ResourceLocation DRY_QUICKSAND_FLOWING_RL  = new ResourceLocation(QuicksandRehydrated.MOD_ID, "block/dry_quicksand_flowing");
    public static final ResourceLocation DRY_QUICKSAND_OVERLAY_RL = new ResourceLocation("block/sand");
//    public static final ResourceLocation DRY_QUICKSAND_OVERLAY_RL = new ResourceLocation(QuicksandRehydrated.MOD_ID, "block_sand");

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, QuicksandRehydrated.MOD_ID);

    public static final RegistryObject<FluidType> DRY_QUICKSAND_TYPE = register("dry_quicksand",
            FluidType.Properties.create().density(99999999).viscosity(0).sound(SoundAction.get("drink"),
                    SoundEvents.HONEY_DRINK));



    private static RegistryObject<FluidType> register(String name, FluidType.Properties properties) {
        return FLUID_TYPES.register(name, () -> new FluidQuicksandBase(DRY_QUICKSAND_STILL_RL, DRY_QUICKSAND_FLOWING_RL, DRY_QUICKSAND_OVERLAY_RL, properties));
    }

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }
}
