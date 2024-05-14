package net.mokai.quicksandrehydrated.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
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


    public static final IGuiOverlay HUD_STRUGGLE = ((gui, poseStack, partialTick, width, height) -> {

        Player p = Minecraft.getInstance().player;
        entityQuicksandVar es = (entityQuicksandVar) p;

        if (es.getInQuicksand()) {

            int x = width / 2;
            int y = height;

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, EMPTY_STRUGGLE);

            GuiComponent.blit(poseStack,x - 94, y - 27,0,0,182, 5, 182, 5);


            RenderSystem.setShaderTexture(0, FILLED_STRUGGLE);


            playerStruggling strugglingPlayer = (playerStruggling) p;

            float percent = (float) strugglingPlayer.getStruggleHold() / 20.0f;
            int pixels_wide = (int) (182 * percent);

            if (pixels_wide > 182) {
                pixels_wide = 182;
            }

            GuiComponent.blit(poseStack, x - 94, y - 27, 0, 0, pixels_wide, 5, 182, 5);

        }

    });

}
