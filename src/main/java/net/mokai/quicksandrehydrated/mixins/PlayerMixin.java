package net.mokai.quicksandrehydrated.mixins;

import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.mokai.quicksandrehydrated.networking.ModMessages;
import net.mokai.quicksandrehydrated.networking.packet.StruggleAttemptC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin implements playerStruggling {

    int struggleCooldown = 0;

    @Override
    public int getStruggleCooldown() {
        return this.struggleCooldown;
    }
    @Override
    public void setStruggleCooldown(int set) {
        this.struggleCooldown = set;
    }
    @Override
    public void addStruggleCooldown(int add) {
        this.struggleCooldown = this.struggleCooldown + add;
    }

    @Override
    public void attemptStruggle() {
        if (this.getStruggleCooldown() <= 0) {
            ModMessages.sendToServer(new StruggleAttemptC2SPacket());
            this.addStruggleCooldown(5);
        }
    }


    @Inject(method = "tick", at = @At("HEAD"))
    public void tickStruggleCooldown(CallbackInfo ci)
    {
        if (this.getStruggleCooldown() > 0) {
            this.addStruggleCooldown(-1);
        }
    }








}
