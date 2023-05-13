package net.mokai.quicksandrehydrated;

import com.mojang.logging.LogUtils;
import net.mokai.quicksandrehydrated.registry.*;
import net.mokai.quicksandrehydrated.item.ModCreativeModeTab;
import net.mokai.quicksandrehydrated.loot.ModLootModifiers;
import net.mokai.quicksandrehydrated.networking.ModMessages;
import net.mokai.quicksandrehydrated.screen.MixerScreen;
import net.mokai.quicksandrehydrated.screen.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(QuicksandRehydrated.MOD_ID)
public class QuicksandRehydrated {
    public static final String MOD_ID = "qsrehydrated";
    private static final Logger LOGGER = LogUtils.getLogger();

    // Very Important Comment
    public QuicksandRehydrated() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModFluids.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        ModLootModifiers.register(modEventBus);


        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {

        event.enqueueWork(() -> {
/*            SpawnPlacements.register(ModEntityTypes.CHOMPER.get(),
                    SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkMonsterSpawnRules);
*/
            ModMessages.register();
        });
    }

    private void addCreative(CreativeModeTabEvent.BuildContents event) {
        if(event.getTab() == ModCreativeModeTab.QUICKSAND_TAB) {
            event.accept(ModItems.ROPE);
            event.accept(ModItems.CRANBERRY);

            event.accept(ModBlocks.QUICKSAND);
            event.accept(ModBlocks.SOFT_QUICKSAND);
            event.accept(ModBlocks.MIXER);
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            //ItemBlockRenderTypes.setRenderLayer(ModFluids.DRY_QUICKSAND.get(), RenderType.solid());
            //ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_DRY_QUICKSAND.get(), RenderType.solid());

            MenuScreens.register(ModMenuTypes.MIXER_MENU.get(), MixerScreen::new);

        }
    }


}
