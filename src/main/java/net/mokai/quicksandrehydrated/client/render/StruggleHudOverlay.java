package net.mokai.quicksandrehydrated.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.entity.entityQuicksandVar;
import net.mokai.quicksandrehydrated.entity.playerStruggling;

public class StruggleHudOverlay {

    private static final ResourceLocation FILLED_STRUGGLE = new ResourceLocation(QuicksandRehydrated.MOD_ID, "textures/gui/struggle_meter_full.png");
    private static final ResourceLocation EMPTY_STRUGGLE = new ResourceLocation(QuicksandRehydrated.MOD_ID, "textures/gui/struggle_meter_empty.png");


    public static final IGuiOverlay HUD_STRUGGLE = ((gui, guiGraphics, partialTick, width, height) -> {

        Player p = Minecraft.getInstance().player;
        entityQuicksandVar es = (entityQuicksandVar) p;

        if (es.getInQuicksand()) {

            int bar_x = (width / 2) - 91;
            int bar_y = height - 29;

            int bar_w = 182;
            int bar_h = 5;

            guiGraphics.blit(EMPTY_STRUGGLE,bar_x, bar_y,0,0, bar_w, bar_h, bar_w, bar_h);

            playerStruggling strugglingPlayer = (playerStruggling) p;

            float percent = (float) strugglingPlayer.getStruggleHold() / 20.0f;
            int pixels_wide = (int) (182 * percent);

            if (pixels_wide > 182) {
                pixels_wide = 182;
            }

            guiGraphics.blit(FILLED_STRUGGLE,bar_x, bar_y,0,0, pixels_wide, bar_h, bar_w, bar_h);

        }

    });

}
