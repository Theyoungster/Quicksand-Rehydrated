package net.mokai.quicksandrehydrated.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ModCreativeModeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, QuicksandRehydrated.MOD_ID);

    public static RegistryObject<CreativeModeTab> QUICKSAND_TAB;

    public static void setupCreativeTab() {
        QUICKSAND_TAB = CREATIVE_TABS.register("quicksand_messes_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.QUICKSAND.get().asItem()))
                    .title(Component.translatable("creativetab.qsrehydrated.quicksand_messes_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.acceptAll(ModBlocks.setupCreativeGroups());
                        pOutput.acceptAll(ModItems.setupCreativeGroups());
                    })
                    .build());
    }



    public static void register(IEventBus eventbus) {
        setupCreativeTab();
        CREATIVE_TABS.register(eventbus);
    }

}
