package net.mokai.quicksandrehydrated.util;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import org.checkerframework.checker.units.qual.A;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.logging.LogManager;

public class DepthCurve {

    /**
     * This class is a helper class that stores a curve, designed to be instantiated to define quicksand behavior.
     */

    public InterpType interptype;
    public double start;
    public double end;
    public double exp;
    public double[] array;
    public ArrayList<Vector2d> points;


    public DepthCurve (double constant) {
        interptype = InterpType.CONSTANT;
        start = constant;
    }

    public DepthCurve (double[] array) {
        interptype = InterpType.ARRAY;
        this.array = array;
        start = array[0];
        end = array[array.length-1];
    }

    public DepthCurve (ArrayList<Vector2d> points) {
        interptype = InterpType.CUSTOM;
        this.points = points;
        start = points.get(0).y;
        end = points.get(points.size()-1).y;
    }

    public DepthCurve (InterpType type, double start, double end, double exponent) {
        if (type == InterpType.CUSTOM || type == InterpType.ARRAY) {
            throw new IllegalArgumentException("DepthCurve(Interptype, double, double, double) CANNOT be CUSTOM or ARRAY.");
        } else {
            interptype = type;
        }
            this.start = start;
            this.end = end;
            this.exp = exponent;
    }

    public DepthCurve(double start, double end) {
        interptype = InterpType.LINEAR;
        this.start = start;
        this.end = end;
    }


public double getAt(double pos) {

    pos = Math.max(0, Math.min(pos, 1));

    switch (interptype) {
        case CONSTANT:  return start;
        case LINEAR:    return ease(start, end, pos);
        case POW_IN:    return ease(start, end, Math.pow(pos, exp));
        case POW_OUT:   return ease(start, end, 1-Math.pow(1-pos, exp));
        case POW_INOUT: return pos<.5 ? ease(start, end, Math.pow(pos*2, exp)/2) : ease(start, end, 1-Math.pow(2+pos*-2, exp)/2);
        case ARRAY:

            int len = array.length;
            if (array.length == 0) {
                throw new IndexOutOfBoundsException("Cannot interpolate into an empty list. What would the correct default value be?");
            } else if (len == 1 || pos == 0) {
                return array[0];
            } else if (pos == 1.0) {
                return array[len-1];
            }

            int indexMaximum = len - 1;
            double scaledDouble = pos * indexMaximum;
            int leftIndex = (int) Math.floor(scaledDouble);
            int rightIndex = leftIndex + 1;

            double percent = scaledDouble - rightIndex;

            return ease(array[leftIndex], array[rightIndex], percent);


        case CUSTOM:



        default: return 0d;
    }
}

    private double ease(double start, double end, double pos) {
        return (pos*(end-start))+start;
    }


    public enum InterpType {
        CONSTANT,
        LINEAR,
        POW_IN,
        POW_OUT,
        POW_INOUT,
        ARRAY,
        CUSTOM

    }

}
