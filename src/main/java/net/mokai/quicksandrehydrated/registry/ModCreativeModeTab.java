package net.mokai.quicksandrehydrated.registry;

import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = QuicksandRehydrated.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeModeTab {
    public static CreativeModeTab QUICKSAND_TAB;

    // TODO reimplement creative tab
//    @SubscribeEvent
//    public static void registerCreativeModeTabs(CreativeModeTabEvent.Register event) {
//        QUICKSAND_TAB = event.registerCreativeModeTab(new ResourceLocation(QuicksandRehydrated.MOD_ID, "quicksand_tab"),
//                builder -> builder.icon(() -> new ItemStack(ModBlocks.QUICKSAND.get())).title(Component.literal("Quicksand Rehydrated")).build());
//    }

}
