package net.mokai.quicksandrehydrated.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.mokai.quicksandrehydrated.block.quicksands.core.QuicksandBase;
import net.mokai.quicksandrehydrated.networking.ModMessages;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public class StruggleAttemptC2SPacket {
    public StruggleAttemptC2SPacket() {

    }

    public StruggleAttemptC2SPacket(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // HERE WE ARE ON THE SERVER!
            ServerPlayer player = context.getSender();
            ServerLevel level = player.serverLevel();

            BlockState playerBlockState = player.getFeetBlockState();
            Block playerBlock = playerBlockState.getBlock();

            if (playerBlock instanceof QuicksandBase) {

                Random rand = new Random();
                double amount = rand.nextDouble(0.0, 1.0);

                QuicksandBase playerQuicksand = (QuicksandBase) playerBlock;
                player.addDeltaMovement(new Vec3(0.0, amount, 0.0));

                UUID playerId = player.getUUID();
                ModMessages.sendToPlayer(new StruggleResultS2CPacket(amount), player);

            }
            
        });
        return true;
    }

}
