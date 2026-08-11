package net.mokai.quicksandrehydrated.event;

import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.client.EngulfClient;
import net.mokai.quicksandrehydrated.util.Keybinding;
import net.mokai.quicksandrehydrated.networking.EngulfMessages;
import net.mokai.quicksandrehydrated.networking.packet.EngulfSneakC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = QuicksandRehydrated.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EngulfClientEvents {
    private static boolean lastSneakDown = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        EngulfClient.tickClient();

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            lastSneakDown = false;
            return;
        }

        boolean engulfed = EngulfClient.isEngulfed();
        boolean sneakDown = Keybinding.STRUGGLE_KEY.isDown();
        if (!engulfed) {
            lastSneakDown = sneakDown;
            return;
        }

        if (!lastSneakDown && sneakDown) {
            EngulfMessages.sendToServer(new EngulfSneakC2SPacket(true));
        } else if (lastSneakDown && !sneakDown) {
            EngulfMessages.sendToServer(new EngulfSneakC2SPacket(false));
        }
        lastSneakDown = sneakDown;
    }

    @SubscribeEvent
    public static void onComputeCamera(ViewportEvent.ComputeCameraAngles e) {
        if (!EngulfClient.isEngulfed()) return;
        e.setPitch(e.getPitch() + EngulfClient.getPitchOffset());
        e.setRoll(e.getRoll() + EngulfClient.getRollOffset());
    }
}
