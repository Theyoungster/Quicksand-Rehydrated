package net.mokai.quicksandrehydrated.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.mokai.quicksandrehydrated.entity.playerStruggling;

import java.util.function.Supplier;

public class StruggleDownC2SPacket {
    public StruggleDownC2SPacket() {

    }

    public StruggleDownC2SPacket(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {

            // HERE WE ARE ON THE SERVER!
            ServerPlayer player = context.getSender();

            System.out.println("<Server>: Struggle Down");

            playerStruggling strugglingPlayer = (playerStruggling) player;
            strugglingPlayer.setHoldingStruggle(true);


        });
        return true;
    }

}
