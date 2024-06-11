package net.mokai.quicksandrehydrated.util;
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
    public ArrayList<Vector2d> pts;


    public DepthCurve (double constant) {
        interptype = InterpType.CONSTANT;
        start = constant;
    }

    public DepthCurve (ArrayList<Vector2d> points) {
        interptype = InterpType.CUSTOM;
        pts = points;
        start = points.get(0).y();
        end = points.get(points.size()-1).y();
    }

    public DepthCurve (InterpType type, double start, double end, double exponent) {
        assert(type != InterpType.CUSTOM);
        if (type == InterpType.CUSTOM) {
            System.out.println("ERROR: 'CUSTOM' is not a valid InterpType for DepthCurve(Interptype, double, double, double). Resolving to 'LINEAR'.");
            interptype = InterpType.LINEAR;
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
        case CUSTOM: return 0d; // not yet implemented!
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
        CUSTOM
    }

}
