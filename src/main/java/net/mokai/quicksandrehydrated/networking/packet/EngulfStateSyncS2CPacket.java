package net.mokai.quicksandrehydrated.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.mokai.quicksandrehydrated.networking.ClientPacketHandler;

import java.util.function.Supplier;

public class EngulfStateSyncS2CPacket {
    private final boolean engulfed;
    private final int progress;

    public EngulfStateSyncS2CPacket(boolean engulfed, int progress) {
        this.engulfed = engulfed;
        this.progress = progress;
    }

    public EngulfStateSyncS2CPacket(FriendlyByteBuf buf) {
        this.engulfed = buf.readBoolean();
        this.progress = buf.readVarInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(engulfed);
        buf.writeVarInt(progress);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> ClientPacketHandler.handleEngulfSync(this.engulfed, this.progress));
        return true;
    }
}
