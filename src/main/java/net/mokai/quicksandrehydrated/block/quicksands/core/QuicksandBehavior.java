package net.mokai.quicksandrehydrated.block.quicksands.core;

import net.mokai.quicksandrehydrated.entity.data.QuicksandEffect;
import net.mokai.quicksandrehydrated.util.DepthCurve;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class QuicksandBehavior {

    /**
     * This class defines a variety of common behaviors of the various quicksand-y substances.
     * Construct an instance of this class by defining a new QuicksandBehavior, chaining .set() methods, and then
     * feeding the result to the constructor of QuicksandBase during registration in ModBlocks.
     */




    public List<Class<? extends QuicksandEffect>> effectsList = new ArrayList<Class<? extends QuicksandEffect>>();

    public DepthCurve bubbleChance = new DepthCurve(1d);
    public DepthCurve sinkSpeed = new DepthCurve(.01d);
    public DepthCurve walkSpeed = new DepthCurve(.5, 0);
    public DepthCurve vertSpeed = new DepthCurve(.1d);

    public DepthCurve wobbleTugHorizontal = new DepthCurve(0d); // Wobble P (position)
    public DepthCurve wobbleTugVertical = new DepthCurve(0d);

    public DepthCurve wobbleMove = new DepthCurve(1d); // used by both

    // TODO separate by Horizontal and Vertical?
    public DepthCurve wobbleRebound = new DepthCurve(0.1d); // Wobble M (momentum)
    public DepthCurve wobbleDecay = new DepthCurve(0.9d); // inverse, 1 means it never decays
    public DepthCurve wobbleApply = new DepthCurve(1.0d); // how much of the player's momentum is added to wobble momentum.


    public String coverageTexture = "quicksand_coverage";
    public String secretDeathMessage = "quicksand";
    public double secretDeathMessageChance = 0;
    public double buoyancyPoint = 2d;
    public double offset = 0;
    public double stepOutHeight = .1d;



    public QuicksandBehavior addQuicksandEffect(Class<? extends QuicksandEffect> effectClass) {
        if (QuicksandEffect.class.isAssignableFrom(effectClass)) {
            effectsList.add(effectClass);
        }
        return this;
    }

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

    public QuicksandBehavior setWobbleTugHorizontal(DepthCurve curve) {
        wobbleTugHorizontal = curve; return this;}
    public QuicksandBehavior setWobbleTugHorizontal(double tug) {
        wobbleTugHorizontal = new DepthCurve(tug); return this;}
    public QuicksandBehavior setWobbleTugHorizontal(double[] tug) {
        wobbleTugHorizontal = new DepthCurve(tug); return this;}
    public QuicksandBehavior setWobbleTugHorizontal(ArrayList<Vector2d> tug) {
        wobbleTugHorizontal = new DepthCurve(tug); return this;}
    public double getWobbleTugHorizontal(double depth) {return wobbleTugHorizontal.getAt(depth);}

    public QuicksandBehavior setWobbleTugVertical(DepthCurve curve) {
        wobbleTugVertical = curve; return this;}
    public QuicksandBehavior setWobbleTugVertical(double tug) {
        wobbleTugVertical = new DepthCurve(tug); return this;}
    public QuicksandBehavior setWobbleTugVertical(double[] tug) {
        wobbleTugVertical = new DepthCurve(tug); return this;}
    public QuicksandBehavior setWobbleTugVertical(ArrayList<Vector2d> tug) {
        wobbleTugVertical = new DepthCurve(tug); return this;}
    public double getWobbleTugVertical(double depth) {return wobbleTugVertical.getAt(depth);}

    public QuicksandBehavior setWobbleMove(DepthCurve curve) {
        wobbleMove = curve; return this;}
    public QuicksandBehavior setWobbleMove(double depth) {
        wobbleMove = new DepthCurve(depth); return this;}
    public QuicksandBehavior setWobbleMove(double[] depth) {
        wobbleMove = new DepthCurve(depth); return this;}
    public QuicksandBehavior setWobbleMove(ArrayList<Vector2d> depth) {
        wobbleMove = new DepthCurve(depth); return this;}
    public double getWobbleMove(double depth) {return wobbleMove.getAt(depth);}



    public QuicksandBehavior setWobbleDecay(DepthCurve curve) {
        wobbleDecay = curve; return this;}
    public QuicksandBehavior setWobbleDecay(double depth) {
        wobbleDecay = new DepthCurve(depth); return this;}
    public QuicksandBehavior setWobbleDecay(double[] depth) {
        wobbleDecay = new DepthCurve(depth); return this;}
    public QuicksandBehavior setWobbleDecay(ArrayList<Vector2d> depth) {
        wobbleDecay = new DepthCurve(depth); return this;}
    public double getWobbleDecay(double depth) {return wobbleDecay.getAt(depth);}

    public QuicksandBehavior setWobbleRebound(DepthCurve curve) {
        wobbleRebound = curve; return this;}
    public QuicksandBehavior setWobbleRebound(double depth) {
        wobbleRebound = new DepthCurve(depth); return this;}
    public QuicksandBehavior setWobbleRebound(double[] depth) {
        wobbleRebound = new DepthCurve(depth); return this;}
    public QuicksandBehavior setWobbleRebound(ArrayList<Vector2d> depth) {
        wobbleRebound = new DepthCurve(depth); return this;}
    public double getWobbleRebound(double depth) {return wobbleRebound.getAt(depth);}

    public QuicksandBehavior setWobbleApply(DepthCurve curve) {
        wobbleApply = curve; return this;}
    public QuicksandBehavior setWobbleApply(double depth) {
        wobbleApply = new DepthCurve(depth); return this;}
    public QuicksandBehavior setWobbleApply(double[] depth) {
        wobbleApply = new DepthCurve(depth); return this;}
    public QuicksandBehavior setWobbleApply(ArrayList<Vector2d> depth) {
        wobbleApply = new DepthCurve(depth); return this;}
    public double getWobbleApply(double depth) {return wobbleApply.getAt(depth);}




    public QuicksandBehavior setCoverageTexture(String coverageText) {this.coverageTexture = coverageText; return this;}
    public String            getCoverageTexture() {return "qsrehydrated:textures/entity/coverage/" + coverageTexture + ".png";}
    public String            getCoverageTex() {return coverageTexture;}
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
