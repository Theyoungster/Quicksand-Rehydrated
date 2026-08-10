package net.mokai.quicksandrehydrated.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.mokai.quicksandrehydrated.client.EngulfClient;
import net.mokai.quicksandrehydrated.entity.coverage.PlayerCoverage;
import net.mokai.quicksandrehydrated.entity.playerStruggling;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {

    public static void handleStruggleResult(double amount) {
        Player thisPlayer = Minecraft.getInstance().player;
        if (thisPlayer == null) {
            return;
        }
        thisPlayer.addDeltaMovement(new Vec3(0.0, amount, 0.0));
    }

    public static void handleEngulfSync(boolean engulfed, int progress) {
        EngulfClient.setEngulfedClient(engulfed, progress);
    }

    public static void handleCoverageSync(PlayerCoverage coverage, int id) {
        // this is the function that runs on a client when it receives a CoverageSync packet.

        ClientLevel level = Minecraft.getInstance().level;

        if (level != null) {
            Entity entity = level.getEntity(id);

            if (entity instanceof Player player) {
                playerStruggling pS = (playerStruggling) player;
                pS.replaceCoverage(coverage);
            }
        }
    }

}
