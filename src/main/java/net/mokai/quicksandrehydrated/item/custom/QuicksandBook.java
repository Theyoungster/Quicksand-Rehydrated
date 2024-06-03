package net.mokai.quicksandrehydrated.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.internal.TextComponentMessageFormatHandler;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class QuicksandBook extends Item {
    public QuicksandBook(Properties pProperties) {
        super(pProperties);
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        pPlayer.openItemGui(itemstack, pHand);

        if (pPlayer.isCrouching()) {
            // Debug code can go here!

            String outtext;
            if (pPlayer.getAbilities().instabuild) {
                outtext = "Setting to Survival";
                if (pPlayer instanceof ServerPlayer) {
                    ServerPlayer sp = (ServerPlayer)pPlayer;
                    sp.setGameMode(GameType.SURVIVAL);
                }
            } else {
                 outtext = "Setting to Creative";
                if (pPlayer instanceof ServerPlayer) {
                    ServerPlayer sp = (ServerPlayer)pPlayer;
                    sp.setGameMode(GameType.CREATIVE);
                }
            }

            if (pPlayer instanceof AbstractClientPlayer) {
                pPlayer.displayClientMessage(Component.literal(outtext), true);
            }





        } else {

        }

        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }




    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(Component.literal("DEBUG: Hold Shift to switch gamemodes.").withStyle(ChatFormatting.YELLOW));

        super.appendHoverText(stack, level, components, flag);
    }

}
