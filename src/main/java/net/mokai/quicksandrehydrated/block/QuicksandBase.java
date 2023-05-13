package net.mokai.quicksandrehydrated.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.mokai.quicksandrehydrated.util.EasingHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static net.mokai.quicksandrehydrated.QuicksandRehydrated.MOD_ID;


public class QuicksandBase extends Block {

    public QuicksandBase(Properties pProperties) {
        super(pProperties);
    }
    private Random rng = new Random();


    // ----- OVERRIDE AND MODIFY THESE VALUES FOR YOUR QUICKSAND TYPE ----- //

    public String getTex() {return "qsrehydrated:textures/block/quicksand.png";}

    public double getOffset() { return 0d; } // This value is subtracted from depth for substances that aren't full blocks.

    public double getStruggleSensitivity() {return .01d;} //.05 is very sensitive, .2 is moderately sensitive, and .8+ is very un-sensitive.

    public boolean getBubbling() { return true; } // Does this substance bubble when you sink?

    public double[] getSink() { return new double[]{.1d, .08d, .05d, .0d, .1d}; } // Sinking speed. Lower is slower.
    public double[] walkSpeed() { return new double[]{1d, .5d, .25d, .125d, 0d}; } // Horizontal movement speed (ignoring Gravity)
    public double[] gravity() { return new double[] {0d, 0d, .1d, .2d, .3d}; } // Gravity pulls you towards the center of a given mess block- potentially away from any ledges one could climb up on.
    public int[] jumpPercentage() { return new int[]{30, 20, 0, 0, 0}; } // Chance per tick to be able to jump.

    public double getCustomDeathMessageOdds() { return .25; }

    public void KILL(LivingEntity pEntity) {
        pEntity.hurt(new DamageSource(MOD_ID + "_quicksand"), 2);
    }




    // ----- OKAY YOU CAN STOP OVERRIDING AND MODIFYING VALUES NOW ----- //



    public int toInt(double y) {
        if (y < .375) {
            return 0; //         Surface: 0 - .375
        } else if (y < .75) {
            return 1; //         Knee deep: .375 - .75
        } else if (y < 1.25) {
            return 2; //         Waist deep: .75 - 1.25
        } else if (y < 1.625) {
            return 3; //         Chest deep: 1.25 - 1.625
        } else {
            return 4; //         Under: 1.625+
        }
    }

    public double getDepth(Level pLevel, BlockPos pPos, Entity pEntity) { return EasingHandler.getDepth(pEntity, pLevel, pPos, getOffset()); }

    public double getSink(double depth) { return getSink()[toInt(depth)] ; }
    public double getWalk(double depth) { return walkSpeed()[toInt(depth)]; }
    public boolean getJump(double depth, Level pLevel) {
        rng.setSeed(pLevel.getGameTime());
        float p = rng.nextInt(100);
        return p < jumpPercentage()[toInt(depth)];
    }




    @SuppressWarnings("deprecation")
    @Override
    public void entityInside(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Entity pEntity) {

        //if (pEntity instanceof ItemEntity) { pEntity.makeStuckInBlock(pState, new Vec3(.1, .02, .1)); return; }  /// This is an ugly hack; items are jittering in quicksand, but skipping all of the code works fine.

        double depth = getDepth(pLevel, pPos, pEntity);

        double mod = 1;
        if (depth > 0) {



            ////// SINKIN' CODE HERE //////
            pEntity.setSprinting(false);
            if (!(pEntity instanceof Player)) {
                pEntity.setOnGround(true);
                mod = 1.62 / pEntity.getEyeHeight(); // 1.62 is the player's eye height; this gets a multiplier so shorter entities sink slower.
                depth = depth * mod;
            }

            boolean canJump = getJump(depth, pLevel);
            double sink = getSink(depth);
            double walk = getWalk(depth);
            if(pPos.getX() != pEntity.getBlockX() || pPos.getZ() != pEntity.getBlockZ())
                { walk = Math.max(walk, .0625); }// Cap the speed reduction if the player is only partially inside any quicksand blocks.
            if (canJump) { pEntity.setOnGround(true); } // Above depth .6, the player can step back onto land.


            /**
             * Things that might slurp the player down faster:
             * - Heavy armor
             * - struggling
             * - Sinking potion effect
             */

            if(pLevel.isClientSide()) { // This bit is broken. We need to have the player themselves calculate their rotation / struggle speed, using the AirControlMixin to add the function, and then later call it.

                double test = Math.abs(pEntity.getViewVector(0).x - pEntity.getViewVector(1).x);
                if (test > getStruggleSensitivity()) {
                    sink = sink + 1.1111111f;
                }

            }

            pEntity.makeStuckInBlock(pState, new Vec3(walk, sink, walk)); /// Lower values are slower.

            ////// SINKIN' CODE END


        }
           // Kill code here.
        if (pEntity instanceof LivingEntity) {
            LivingEntity le = (LivingEntity) pEntity;
            if (pEntity.getAirSupply() <= -20 && le.getHealth() <= 2) {
                deathMessage(pLevel, le);
            }
        }
    }


    public void deathMessage(Level pLevel, LivingEntity pEntity) {
        if (pLevel.getLevelData().isHardcore()) {
            pEntity.hurt(new DamageSource(MOD_ID + "_hardcore"), 2);
        } else {
            double p = Math.random();
            if (p > getCustomDeathMessageOdds()) {
                pEntity.hurt(new DamageSource(MOD_ID + "_generic_" + (int) (Math.random() * 3)), 2);
            } else {
                KILL(pEntity);
            }
        }
    }

}
