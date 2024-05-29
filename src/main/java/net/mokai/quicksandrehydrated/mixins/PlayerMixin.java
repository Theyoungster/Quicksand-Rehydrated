package net.mokai.quicksandrehydrated.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.mokai.quicksandrehydrated.block.quicksands.core.QuicksandBase;
import net.mokai.quicksandrehydrated.entity.entityQuicksandVar;
import net.mokai.quicksandrehydrated.entity.playerStruggling;
import net.mokai.quicksandrehydrated.networking.ModMessages;
import net.mokai.quicksandrehydrated.networking.packet.StruggleDownC2SPacket;
import net.mokai.quicksandrehydrated.networking.packet.StruggleReleaseC2SPacket;
import net.mokai.quicksandrehydrated.util.Keybinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Player.class)
public class PlayerMixin implements playerStruggling {

    private static final UUID GRAVITY_MODIFIER_QUICKSAND_UUID = UUID.fromString("b8c5b4f6-8188-4466-8239-53c567b11b32");
    private static final AttributeModifier GRAVITY_MODIFIER_QUICKSAND = new AttributeModifier(GRAVITY_MODIFIER_QUICKSAND_UUID, "Quicksand Gravity Cancel", (double)-1.0F, AttributeModifier.Operation.MULTIPLY_BASE);

    int struggleHold = 0;
    boolean holdingStruggle = false;



    @Override
    public boolean getHoldingStruggle() {return this.holdingStruggle;}

    @Override
    public void setHoldingStruggle(boolean set) {this.holdingStruggle = set;}

    @Override
    public int getStruggleHold() {
        return this.struggleHold;
    }
    @Override
    public void setStruggleHold(int set) {
        this.struggleHold = set;
    }
    @Override
    public void addStruggleHold(int add) {
        this.struggleHold = this.struggleHold + add;
    }

    @Override
    public void attemptStruggle() {

        Player player = (Player)(Object) this;
        entityQuicksandVar QuicksandVarEntity = (entityQuicksandVar) player;
        playerStruggling strugglingPlayer = (playerStruggling) player;

        if (QuicksandVarEntity.getInQuicksand()) {

            int ticks = strugglingPlayer.getStruggleHold();
            double boost_amount = ticks / 20.0;
            boost_amount *= 0.5; // half a block at max
            player.addDeltaMovement(new Vec3(0.0, boost_amount, 0.0));

            BlockPos bp = QuicksandVarEntity.getStuckBlock(player);
            if (bp != null) {

                Level eLevel = player.getLevel();
                BlockState bs = eLevel.getBlockState(bp);

                QuicksandBase qs = (QuicksandBase) bs.getBlock();
                qs.struggleAttempt(bs, player, boost_amount);

            }

        }

        strugglingPlayer.setStruggleHold(0);

    }







    @Inject(method = "tick", at = @At("HEAD"))
    public void tickStruggleCooldown(CallbackInfo ci)
    {
        Player player = (Player)(Object) this;
        entityQuicksandVar QuicksandVarEntity = (entityQuicksandVar) player;
        playerStruggling strugglingPlayer = (playerStruggling) player;



        AttributeInstance gravity = player.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
        if (QuicksandVarEntity.getInQuicksand()) {
            if (gravity != null && !gravity.hasModifier(GRAVITY_MODIFIER_QUICKSAND)) {
                gravity.addTransientModifier(GRAVITY_MODIFIER_QUICKSAND);
            }
        }
        else {
            if (gravity != null && gravity.hasModifier(GRAVITY_MODIFIER_QUICKSAND)) {
                gravity.removeModifier(GRAVITY_MODIFIER_QUICKSAND);
            }
        }

//        if (gravity != null && gravity.hasModifier(GRAVITY_MODIFIER_QUICKSAND)) {
//            player.displayClientMessage(Component.literal("has quicksand grav modifier"), false);
//        }

        if (player.getLevel().isClientSide()) {

            boolean keyDown = Keybinding.STRUGGLE_KEY.isDown();
            boolean flagHolding = strugglingPlayer.getHoldingStruggle();

            if ( keyDown && !flagHolding) {
                // key IS down this tick, flagHolding is NOT
                ModMessages.sendToServer(new StruggleDownC2SPacket());
            }
            else if ( !keyDown && flagHolding) {
                // key is NOT DOWN this tick, just released
                ModMessages.sendToServer(new StruggleReleaseC2SPacket());
            }

            strugglingPlayer.setHoldingStruggle(keyDown);

        }

        // both server AND client
        if (strugglingPlayer.getHoldingStruggle()) {
            addStruggleHold(1);
        }
        else if (strugglingPlayer.getStruggleHold() > 0) {
            strugglingPlayer.attemptStruggle();
        }

    }








}
