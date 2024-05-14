package net.mokai.quicksandrehydrated.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.mokai.quicksandrehydrated.block.QuicksandBase;
import net.mokai.quicksandrehydrated.entity.playerStruggling;
import net.mokai.quicksandrehydrated.networking.ModMessages;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public class StruggleReleaseC2SPacket {
    public StruggleReleaseC2SPacket() {

    }

    public StruggleReleaseC2SPacket(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {

            // HERE WE ARE ON THE SERVER!
            ServerPlayer player = context.getSender();

            playerStruggling strugglingPlayer = (playerStruggling) player;


            strugglingPlayer.attemptStruggle();
            strugglingPlayer.setHoldingStruggle(false);

        });
        return true;
    }

}
