package net.mokai.quicksandrehydrated.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class BookMenu extends Screen {
    protected BookMenu(Component pTitle) {
        super(pTitle);
    }

    @Override
    protected void init() {
        super.init();
    }

    /*
    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderFluidAreaTooltips(pPoseStack, pMouseX, pMouseY, x, y);
    }

    private void renderFluidAreaTooltips(PoseStack pPoseStack, int pMouseX, int pMouseY, int x, int y) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, 33, 14)) {
            renderTooltip(pPoseStack, renderer.getTooltip(menu.getFluidStack(), TooltipFlag.Default.NORMAL),
                    Optional.empty(), pMouseX - x, pMouseY - y);
        }
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, 107, 14)) {
            renderTooltip(pPoseStack, rendererB.getTooltip(menu.getFluidStack(), TooltipFlag.Default.NORMAL),
                    Optional.empty(), pMouseX - x, pMouseY - y);
        }
    }*/


}
