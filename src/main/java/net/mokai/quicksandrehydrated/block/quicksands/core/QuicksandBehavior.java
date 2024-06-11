package net.mokai.quicksandrehydrated.block.quicksands.core;

import net.mokai.quicksandrehydrated.util.DepthCurve;
public class QuicksandBehavior {

    /**
     * This class defines a variety of possible, common behaviors of the various quicksand-y substances.
     * Construct an instance of this class by defining a new QuicksandBehavior, chaining .set() methods, and then
     * feeding the result to the constructor of QuicksandBase.
     * <p>
     */

    public String coverageTexture = "quicksand_coverage";
    public DepthCurve bubbleChance = new DepthCurve(1d);
    public String secretDeathMessage = "quicksand";
    public double secretDeathMessageChance;
    public DepthCurve sinkSpeed = new DepthCurve(.01d);
    public DepthCurve walkSpeed = new DepthCurve(.5, 0);
    public DepthCurve vertSpeed = new DepthCurve(.1d);
    public DepthCurve tugStrengthHorizontal = new DepthCurve(0d);
    public DepthCurve tugStrengthVertical = new DepthCurve(0d);
    public DepthCurve tugLerp = new DepthCurve(1d);
    public double buoyancyPoint = 2d;
    public double stepOutHeight = .25;


    public QuicksandBehavior setCoverageTexture(String coverageText) {this.coverageTexture = coverageText; return this;}
    public String            getCoverageTexture() {return "qsrehydrated:textures/entity/coverage/" + coverageTexture + ".png";}

    public QuicksandBehavior setBubbleChance(DepthCurve curve) {bubbleChance = curve; return this;}
    public double            getBubbleChance(double depth) {return bubbleChance.getAt(depth);}

    public QuicksandBehavior setSecretDeathMessage(String deathmessage) {secretDeathMessage = deathmessage; return this;}
    public String            getSecretDeathMessage(){ return secretDeathMessage;}

    public QuicksandBehavior setSecretDeathMessageChance(double chance) {secretDeathMessageChance = chance; return this;}
    public double            getSecretDeathMessageChance() {return secretDeathMessageChance;}

    public QuicksandBehavior setSinkSpeed(DepthCurve sinkCurve) {sinkSpeed = sinkCurve; return this;}
    public double            getSinkSpeed(double depth) {return sinkSpeed.getAt(depth);}

    public QuicksandBehavior setWalkSpeed(DepthCurve curve) {walkSpeed = curve; return this;}
    public double            getWalkSpeed(double depth) {return walkSpeed.getAt(depth);}

    public QuicksandBehavior setVertSpeed(DepthCurve curve) {vertSpeed = curve; return this;}
    public double            getVertSpeed(double depth) {return vertSpeed.getAt(depth);}

    public QuicksandBehavior setTugStrengthHorizontal(DepthCurve curve) {tugStrengthHorizontal = curve; return this;}
    public double            getTugStrengthHorizontal(double depth) {return tugStrengthHorizontal.getAt(depth);}

    public QuicksandBehavior setTugStrengthVertical(DepthCurve curve) {tugStrengthVertical = curve; return this;}
    public double            getTugStrengthVertical(double depth) {return tugStrengthVertical.getAt(depth);}

    public QuicksandBehavior setTugLerp(DepthCurve curve) {tugLerp = curve; return this;}
    public double            getTugLerp(double depth) {return tugLerp.getAt(depth);}

    public QuicksandBehavior setBuoyancyPoint(double point) {buoyancyPoint = point; return this;}
    public double            getBuoyancyPoint() {return buoyancyPoint;}


    public QuicksandBehavior setStepOutHeight(double point) {stepOutHeight = point; return this;}
    public double            getStepOutHeight() {return stepOutHeight;}
    public boolean           canStepOut(double height) {
        System.out.println("Height: " + height + "          stepOutHeight " + stepOutHeight+ "       " + (height>=stepOutHeight));
        return height>=stepOutHeight;}






    /**
     * Probability, that the substance bubbles when one sinks in it.
     * @return The probability. <code>0</code> = No bubbles. <code>1</code> = Always bubbles.
     */

    /**
     * The sinking speed, depending on the depth.
     * normalized against the vertSpeed, so this value will remain effective - even
     * if the quicksand is very thick.
     * @param depthRaw The depth in blocks. <code>0</code> is exactly on surface level.
     * @return The sinking value. Lower value means slower sinking.
     */

    /**
     * Horizontal movement speed depending on the depth.
     * Thickness - but inverse.
     * @param depthRaw The depth of the object. <code>0</code> is exactly on surface level.
     * @return The inverse resistance when walking. <code>0</code> = very thick; <code>1</code> = very thin.
     */

    /**
     * Vertical movement speed depending on the depth.
     * Same as <code>getWalk()</code>
     * @param depthRaw The depth of the object.
     * @return The inverse resistance when moving up/down. <code>0</code> = very thick; <code>1</code> = very thin.
     */

    /** the previous position will move towards the player this amount *per tick* !
     * You can think of this as how "sticky" the quicksand is.
     * 1.0 = no effect on player's movement
     * 0.0 = player effectively cannot move unless they manage to stop touching the
     * QS block.
     **/

    /** how much the player is pulled backwards
     * You can think of this as how *strong* the sticky effect of the quicksand is
     * 1.0 = player *cannot* move away from the previous position, fully locked in
     * place.
     * 0.0 = no effect on player's movement
     */




}
