package net.mokai.quicksandrehydrated.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.mokai.quicksandrehydrated.block.quicksands.core.QuicksandBase;

public class EasingHandler {

    public static double lerp(double start, double end, double position) {
        position = Math.max(0, Math.min(position, 1)); // Bound `position` to [0,1]
        return ease(start, end, position);
    }

    public static double ease_pow(double start, double end, double position, double exponent) {
        position = Math.max(0, Math.min(position, 1)); // Bound `position` to [0,1]
        return ease(Math.pow(position,exponent), start, end);
    }

    public static double ease_pow_inv(double start, double end, double position, double exponent) {
        position = Math.max(0, Math.min(position, 1));
        return Math.pow(position-1,exponent)*(start-end)+end;
    }

    public static double ease_inout(double start, double end, double position, double exponent) {
        if(position>.5){
            return ease_pow(start, end, position, exponent);
        } else {
            return ease_pow_inv(start, end, position, exponent);
        }
    }

    public static double reverse_interp(double start, double end, double position) {
        double b = end-start;
        return (position-start)/b;
    }

    public static double reverse_interp_sqrt(double start, double end, double position) {
        double b = end-start;
        return (position-start)/b;
    }


    public static double getDepth(Entity pEntity, Level pLevel, BlockPos pPos, double offset) {

        double playerY = pEntity.getPosition(1).y();
        double depth;

        BlockPos playercube;
        double currentHeight = pPos.getY();

        do {
            currentHeight++;
            BlockPos check = new BlockPos(pPos.getX(), currentHeight, pPos.getZ());
            playercube = check;
        } while (pLevel.getBlockState(playercube).getBlock() instanceof QuicksandBase);

        depth = playercube.getY() - playerY - offset;

        return depth;

    }


    private static double ease(double start, double end, double pos) {
        return (pos*(end-start))+start;
    }
}
