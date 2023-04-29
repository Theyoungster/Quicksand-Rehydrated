package net.mokai.quicksandrehydrated.event;

import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = QuicksandRehydrated.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
    }

    @Mod.EventBusSubscriber(modid = QuicksandRehydrated.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {


    }
}