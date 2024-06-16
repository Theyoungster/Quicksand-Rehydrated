package net.mokai.quicksandrehydrated.block.quicksands.core;

import net.mokai.quicksandrehydrated.util.DepthCurve;
import org.joml.Vector2d;

import java.util.ArrayList;

public class QuicksandBehavior {

    /**
     * This class defines a variety of common behaviors of the various quicksand-y substances.
     * Construct an instance of this class by defining a new QuicksandBehavior, chaining .set() methods, and then
     * feeding the result to the constructor of QuicksandBase during registration in ModBlocks.
     */


    public DepthCurve bubbleChance = new DepthCurve(1d);
    public DepthCurve sinkSpeed = new DepthCurve(.01d);
    public DepthCurve walkSpeed = new DepthCurve(.5, 0);
    public DepthCurve vertSpeed = new DepthCurve(.1d);
    public DepthCurve tugStrengthHorizontal = new DepthCurve(0d);
    public DepthCurve tugStrengthVertical = new DepthCurve(0d);
    public DepthCurve tugPointSpeed = new DepthCurve(1d);

    public String coverageTexture = "quicksand_coverage";
    public String secretDeathMessage = "quicksand";
    public double secretDeathMessageChance = 0;
    public double buoyancyPoint = 2d;
    public double offset = 0;
    public double stepOutHeight = .1d;


    public QuicksandBehavior setBubbleChance(DepthCurve curve) {bubbleChance = curve; return this;}
    public QuicksandBehavior setBubbleChance(double chance) {bubbleChance = new DepthCurve(chance); return this;}
    public QuicksandBehavior setBubbleChance(double[] chance) {bubbleChance = new DepthCurve(chance); return this;}
    public QuicksandBehavior setBubbleChance(ArrayList<Vector2d> chance) {bubbleChance = new DepthCurve(chance); return this;}
    public double            getBubbleChance(double depth) {return bubbleChance.getAt(depth);}

    public QuicksandBehavior setSinkSpeed(DepthCurve sinkCurve) {sinkSpeed = sinkCurve; return this;}
    public QuicksandBehavior setSinkSpeed(double speed) {sinkSpeed = new DepthCurve(speed); return this;}
    public QuicksandBehavior setSinkSpeed(double[] speed) {sinkSpeed = new DepthCurve(speed); return this;}
    public QuicksandBehavior setSinkSpeed(ArrayList<Vector2d> speed) {sinkSpeed = new DepthCurve(speed); return this;}
    public double            getSinkSpeed(double depth) {return sinkSpeed.getAt(depth);}

    public QuicksandBehavior setWalkSpeed(DepthCurve curve) {walkSpeed = curve; return this;}
    public QuicksandBehavior setWalkSpeed(double speed) {walkSpeed = new DepthCurve(speed); return this;}
    public QuicksandBehavior setWalkSpeed(double[] speed) {walkSpeed = new DepthCurve(speed); return this;}
    public QuicksandBehavior setWalkSpeed(ArrayList<Vector2d> speed) {walkSpeed = new DepthCurve(speed); return this;}
    public double            getWalkSpeed(double depth) {return walkSpeed.getAt(depth);}

    public QuicksandBehavior setVertSpeed(DepthCurve curve) {vertSpeed = curve; return this;}
    public QuicksandBehavior setVertSpeed(double speed) {vertSpeed = new DepthCurve(speed); return this;}
    public QuicksandBehavior setVertSpeed(double[] speed) {vertSpeed = new DepthCurve(speed); return this;}
    public QuicksandBehavior setVertSpeed(ArrayList<Vector2d> speed) {vertSpeed = new DepthCurve(speed); return this;}
    public double            getVertSpeed(double depth) {return vertSpeed.getAt(depth);}

    public QuicksandBehavior setTugStrengthHorizontal(DepthCurve curve) {tugStrengthHorizontal = curve; return this;}
    public QuicksandBehavior setTugStrengthHorizontal(double tug) {tugStrengthHorizontal = new DepthCurve(tug); return this;}
    public QuicksandBehavior setTugStrengthHorizontal(double[] tug) {tugStrengthHorizontal = new DepthCurve(tug); return this;}
    public QuicksandBehavior setTugStrengthHorizontal(ArrayList<Vector2d> tug) {tugStrengthHorizontal = new DepthCurve(tug); return this;}
    public double            getTugStrengthHorizontal(double depth) {return tugStrengthHorizontal.getAt(depth);}

    public QuicksandBehavior setTugStrengthVertical(DepthCurve curve) {tugStrengthVertical = curve; return this;}
    public QuicksandBehavior setTugStrengthVertical(double tug) {tugStrengthVertical = new DepthCurve(tug); return this;}
    public QuicksandBehavior setTugStrengthVertical(double[] tug) {tugStrengthVertical = new DepthCurve(tug); return this;}
    public QuicksandBehavior setTugStrengthVertical(ArrayList<Vector2d> tug) {tugStrengthVertical = new DepthCurve(tug); return this;}
    public double            getTugStrengthVertical(double depth) {return tugStrengthVertical.getAt(depth);}

    public QuicksandBehavior setTugPointSpeed(DepthCurve curve) {tugPointSpeed = curve; return this;}
    public QuicksandBehavior setTugPointSpeed(double depth) {tugPointSpeed = new DepthCurve(depth); return this;}
    public QuicksandBehavior setTugPointSpeed(double[] depth) {tugPointSpeed = new DepthCurve(depth); return this;}
    public QuicksandBehavior setTugPointSpeed(ArrayList<Vector2d> depth) {tugPointSpeed = new DepthCurve(depth); return this;}
    public double            getTugPointSpeed(double depth) {return tugPointSpeed.getAt(depth);}


    public QuicksandBehavior setCoverageTexture(String coverageText) {this.coverageTexture = coverageText; return this;}
    public String            getCoverageTexture() {return "qsrehydrated:textures/entity/coverage/" + coverageTexture + ".png";}

    public QuicksandBehavior setSecretDeathMessage(String deathmessage) {secretDeathMessage = deathmessage; return this;}
    public String            getSecretDeathMessage(){ return secretDeathMessage;}

    public QuicksandBehavior setSecretDeathMessageChance(double chance) {secretDeathMessageChance = chance; return this;}
    public double            getSecretDeathMessageChance() {return secretDeathMessageChance;}

    public QuicksandBehavior setBuoyancyPoint(double point) {buoyancyPoint = point; return this;}
    public double            getBuoyancyPoint() {return buoyancyPoint;}

    public QuicksandBehavior setOffset(double point) {offset = point; return this;}
    public double            getOffset() {return offset;}

    public QuicksandBehavior setStepOutHeight(double point) {stepOutHeight = point; return this;}
    public double            getStepOutHeight() {return stepOutHeight;}
    public boolean           canStepOut(double height) {return height>=stepOutHeight;}
}
