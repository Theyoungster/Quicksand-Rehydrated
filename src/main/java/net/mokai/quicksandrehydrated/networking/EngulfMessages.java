package net.mokai.quicksandrehydrated.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.networking.packet.EngulfSneakC2SPacket;
import net.mokai.quicksandrehydrated.networking.packet.EngulfStateSyncS2CPacket;

public class EngulfMessages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        packetId = 0;
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(QuicksandRehydrated.MOD_ID, "engulf_messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions("1.0"::equals)
                .serverAcceptedVersions("1.0"::equals)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(EngulfStateSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(EngulfStateSyncS2CPacket::new)
                .encoder(EngulfStateSyncS2CPacket::toBytes)
                .consumerMainThread(EngulfStateSyncS2CPacket::handle)
                .add();

        net.messageBuilder(EngulfSneakC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(EngulfSneakC2SPacket::new)
                .encoder(EngulfSneakC2SPacket::toBytes)
                .consumerMainThread(EngulfSneakC2SPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
