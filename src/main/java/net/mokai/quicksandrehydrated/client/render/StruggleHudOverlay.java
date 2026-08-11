package net.mokai.quicksandrehydrated.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.client.EngulfClient;
import net.mokai.quicksandrehydrated.entity.entityQuicksandVar;
import net.mokai.quicksandrehydrated.entity.playerStruggling;

public class StruggleHudOverlay {

    private static final ResourceLocation FILLED_STRUGGLE = new ResourceLocation(QuicksandRehydrated.MOD_ID, "textures/gui/struggle_meter_full.png");
    private static final ResourceLocation EMPTY_STRUGGLE = new ResourceLocation(QuicksandRehydrated.MOD_ID, "textures/gui/struggle_meter_empty.png");


    public static final IGuiOverlay HUD_STRUGGLE = ((gui, guiGraphics, partialTick, width, height) -> {

        Player p = Minecraft.getInstance().player;
        if (p == null) return;

        boolean showQuicksandBar = p instanceof entityQuicksandVar es && es.getInQuicksand();
        boolean showEngulfBar = EngulfClient.isEngulfed();

        if (showQuicksandBar || showEngulfBar) {

            int bar_x = (width / 2) - 91;
            int bar_y = height - 29;

            int bar_w = 182;
            int bar_h = 5;

            guiGraphics.blit(EMPTY_STRUGGLE,bar_x, bar_y,0,0, bar_w, bar_h, bar_w, bar_h);

            float percent = showEngulfBar
                    ? EngulfClient.getStruggleProgress() / 12.0f
                    : ((playerStruggling) p).getStruggleHold() / 20.0f;
            int pixels_wide = (int) (182 * percent);

            if (pixels_wide > 182) {
                pixels_wide = 182;
            }

            guiGraphics.blit(FILLED_STRUGGLE,bar_x, bar_y,0,0, pixels_wide, bar_h, bar_w, bar_h);

        }

    });

}
