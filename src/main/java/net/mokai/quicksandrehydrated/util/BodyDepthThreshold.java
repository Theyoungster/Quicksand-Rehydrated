package net.mokai.quicksandrehydrated.util;


/**
 * Enum holding values for the height level in blocks as orientation of the default body.
 */
public enum BodyDepthThreshold {
    /**
     * Ground height level.
     */
    GROUND      (0d, 0),
    /**
     * Feet height level.
     */
    FEET        (.1875d, 1),
    /**
     * Knee height level.
     */
    KNEE        (.375d, 2),
    /**
     * Waist height level.
     */
    WAIST       (.75d, 3),
    /**
     * Chest height level.
     */
    CHEST       (1.125d, 4),
    /**
     * Shoulders height level.
     */
    SHOULDERS   (1.5d, 5),
    /**
     * Head height level.
     */
    HEAD        (1.8d, 6),
    /**
     * Submerging height level.
     */
    UNDER       (2d, 7);

    /**
     * Depth level in blocks.
     * <code>0</code> is floor level.
     */
    public final double depth;
    /**
     * An integer ID representing the stage.
     * Beginning from <code>0</code> (<code>GROUND</code>) to <code>7</code> (<code>UNDER</code>).
     */
    public final int stageNumber;


    BodyDepthThreshold(double depth, int stageNumber) {
        this.depth = depth;
        this.stageNumber = stageNumber;
    }

    /**
     * Creates the Threshold instance by an assigned height level.
     * @param height The height to be covered. <code>0</code> is floor level.
     * @return The BodyDepthThreshold covering the given height.
     * Will be the next higher depth level, so if
     * <p><code>height</code> in ]<code>FEET</code>;<code>KNEE</code>]: returns <code>KNEE</code></p>
     * <p>If <code>height</code> > <code>HEAD</code>: returns <code>UNDER</code></p>
     */
    public static BodyDepthThreshold fromHeight(double height) {
        if (height <= BodyDepthThreshold.GROUND.depth)
            return GROUND;
        if (height <= BodyDepthThreshold.FEET.depth)
            return FEET;
        if (height <= BodyDepthThreshold.KNEE.depth)
            return KNEE;
        if (height <= BodyDepthThreshold.WAIST.depth)
            return WAIST;
        if (height <= BodyDepthThreshold.CHEST.depth)
            return CHEST;
        if (height <= BodyDepthThreshold.SHOULDERS.depth)
            return SHOULDERS;
        if (height <= BodyDepthThreshold.HEAD.depth)
            return HEAD;
        // v > HEAD - return UNDER value.
        return UNDER;
    }
}