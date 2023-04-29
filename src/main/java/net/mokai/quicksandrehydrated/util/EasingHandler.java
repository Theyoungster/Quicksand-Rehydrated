package net.mokai.quicksandrehydrated.util;

public class EasingHandler {

    /**
     * Basic linear interpolation.
     *
     * @param start
     * @param end
     * @param position
     * @return Final position
     *
     */
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


    private static double ease(double start, double end, double pos) {
        return (pos*(end-start))+start;
    }
}
