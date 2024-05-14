package net.mokai.quicksandrehydrated.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StruggleResultS2CPacket {
    private final double amount;

    public StruggleResultS2CPacket(double amount) {
        this.amount = amount;
    }

    public StruggleResultS2CPacket(FriendlyByteBuf buf) {
        this.amount = buf.readDouble();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDouble(amount);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {

                Player thisPlayer = Minecraft.getInstance().player;
                thisPlayer.addDeltaMovement(new Vec3(0.0, this.amount, 0.0));

        });
        return true;
    }
}
