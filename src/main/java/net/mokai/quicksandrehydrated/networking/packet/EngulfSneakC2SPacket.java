package net.mokai.quicksandrehydrated.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.mokai.quicksandrehydrated.event.EngulfEvents;

import java.util.function.Supplier;

public class EngulfSneakC2SPacket {
    private final boolean sneakDown;

    public EngulfSneakC2SPacket(boolean sneakDown) {
        this.sneakDown = sneakDown;
    }

    public EngulfSneakC2SPacket(FriendlyByteBuf buf) {
        this.sneakDown = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(sneakDown);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                EngulfEvents.handleClientSneakChange(player, sneakDown);
            }
        });
        return true;
    }
}
