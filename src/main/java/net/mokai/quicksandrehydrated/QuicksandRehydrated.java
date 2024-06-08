package net.mokai.quicksandrehydrated;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.mokai.quicksandrehydrated.loot.ModLootModifiers;
import net.mokai.quicksandrehydrated.networking.ModMessages;
import net.mokai.quicksandrehydrated.registry.*;
import net.mokai.quicksandrehydrated.screen.MixerScreen;
import net.mokai.quicksandrehydrated.screen.ModMenuTypes;

import static net.mokai.quicksandrehydrated.util.ModTags.Blocks.QUICKSAND_DROWNABLE;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(QuicksandRehydrated.MOD_ID)
public class QuicksandRehydrated {

    public static final String MOD_ID = "qsrehydrated";

    public QuicksandRehydrated() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModFluids.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModParticles.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        ModSounds.register(modEventBus);
        ModCreativeModeTab.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        //modEventBus.addListener(this::addCreative);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {

        event.enqueueWork(ModMessages::register);
        //event.enqueueWork(ModEntityTypes::registerPOIs);

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

            //ItemBlockRenderTypes.setRenderLayer(ModFluids.DRY_QUICKSAND.get(), RenderType.solid());
            MenuScreens.register(ModMenuTypes.MIXER_MENU.get(), MixerScreen::new);

        }
    }


    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class GameModEvents {
        @SubscribeEvent
        public static void onLivingBreatheEvent(LivingBreatheEvent event) {

            Entity entity = event.getEntity();
            Vec3 eyePos = entity.getEyePosition();
            BlockPos eyeBlockPos = new BlockPos((int) eyePos.x(), (int)eyePos.y(), (int)eyePos.z());
            BlockState eyeState = entity.level().getBlockState(eyeBlockPos);

            if (eyeState.is(QUICKSAND_DROWNABLE)) {
                event.setCanBreathe(false);
            }

        }

    }





}
