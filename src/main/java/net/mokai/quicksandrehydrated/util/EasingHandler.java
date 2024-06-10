package net.mokai.quicksandrehydrated.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.mokai.quicksandrehydrated.block.quicksands.core.QuicksandBase;

public class EasingHandler {

    public static double lerp(double start, double end, double position) {
        position = Math.max(0, Math.min(position, 1)); // limits `position` to [0,1]
        return ease(start, end, position);
    }

    public static double ease_pow(double start, double end, double position, double exponent) {
        position = Math.max(0, Math.min(position, 1));
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


    public static double getDepth(Entity pEntity, Level pLevel, BlockPos pPos, double offset) {

        double playerY = pEntity.getPosition(1).y();
        double depth;

        BlockPos playercube;
        double currentHeight = pPos.getY();

        do {
            currentHeight++;



            BlockPos check = new BlockPos(pPos.getX(), (int) currentHeight, pPos.getZ());
            playercube = check;
        } while (pLevel.getBlockState(playercube).getBlock() instanceof QuicksandBase);

        depth = playercube.getY() - playerY - offset;

        return depth;

    }



    public static double doubleListInterpolate(double val, double[] listOfDoubles) {

        // val should be scaled 0 to 1, to be mapped to either end of list
        // listOfDoubles is just a list of vals
        if (listOfDoubles.length == 0) {
            throw new IndexOutOfBoundsException("cannot interpolate into an empty list. What would the correct default value be?");
        }
        else if (listOfDoubles.length == 1) {
            return listOfDoubles[0];
        }

        if (val >= 1.0) {
            return listOfDoubles[listOfDoubles.length-1];
        }
        else if (val <= 0.0) {
            return listOfDoubles[0];
        }

        int indexMaximum = (listOfDoubles.length)-1;
        double scaledDouble = val * indexMaximum;

        int leftIndex = (int) Math.floor(scaledDouble);
        int rightIndex = leftIndex;

        double percent = (val * indexMaximum) - rightIndex;

        double leftNumber = listOfDoubles[leftIndex];
        double rightNumber = listOfDoubles[rightIndex];

        return ease(leftNumber, rightNumber, percent);

    }

    private static double ease(double start, double end, double pos) {

        return (pos*(end-start))+start;
    }
}
