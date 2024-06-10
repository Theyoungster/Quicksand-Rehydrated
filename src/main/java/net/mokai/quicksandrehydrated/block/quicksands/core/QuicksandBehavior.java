package net.mokai.quicksandrehydrated.block.quicksands.core;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.mokai.quicksandrehydrated.block.quicksands.Quicksand;
import net.mokai.quicksandrehydrated.util.DepthCurve;
import net.mokai.quicksandrehydrated.util.EasingHandler;

public class QuicksandBehavior {

    public String coverageTexture() {
        return "qsrehydrated:textures/entity/coverage/quicksand_coverage.png";
    }


    public QuicksandBehavior setOffset(DepthCurve curve) {

        return null;
    }





    /**
     * The relative offset in blocks, the sinking mechanic should respect.
     * This allows not full blocks (think of mud with a water surface) to function with
     * the actual sinking mechanic.
     * @param blockstate The BlockState.
     * @return
     * <p>The offset in blocks, that the sinking mechanic will subtract.</p>
     * <p><code>0</code> (default) means, that the sinking mechanic starts directly at the surface.</p>
     * <p><code>0.5</code> means, the block is considered as half a block, so "sinking" starts only below this level.</p>
     */
    public double getOffset(BlockState blockstate) {
        return 0d;
    }

    /**
     * Probability, that the substance bubbles when one sinks in it.
     * @return The probability. <code>0</code> = No bubbles. <code>1</code> = Always bubbles.
     */
    public double getBubblingChance() {
        return .75d;
    }

    public double getCustomDeathMessageOdds() {
        return .25;
    }

    public double getDepth(Level pLevel, BlockPos pPos, Entity pEntity) {
        return EasingHandler.getDepth(pEntity, pLevel, pPos, getOffset(null));
    }

    /**
     * The sinking speed, depending on the depth.
     * normalized against the vertSpeed, so this value will remain effective - even
     * if the quicksand is very thick.
     * @param depthRaw The depth in blocks. <code>0</code> is exactly on surface level.
     * @return The sinking value. Lower value means slower sinking.
     */
    public double getSink(double depthRaw) {
        return EasingHandler.doubleListInterpolate(depthRaw / 2, new double[] { 0.01, 0.01 });
    }

    /**
     * Horizontal movement speed depending on the depth.
     * Thickness - but inverse.
     * @param depthRaw The depth of the object. <code>0</code> is exactly on surface level.
     * @return The inverse resistance when walking. <code>0</code> = very thick; <code>1</code> = very thin.
     */
    public double getWalk(double depthRaw) {
        return EasingHandler.doubleListInterpolate(depthRaw / 2, new double[] { 0.5, 0.0 });
    }

    /**
     * Vertical movement speed depending on the depth.
     * Same as <code>getWalk()</code>
     * @param depthRaw The depth of the object.
     * @return The inverse resistance when moving up/down. <code>0</code> = very thick; <code>1</code> = very thin.
     */
    public double getVert(double depthRaw) {
        return EasingHandler.doubleListInterpolate(depthRaw / 2, new double[] { 0.1, 0.1 });
    }

    /** the previous position will move towards the player this amount *per tick* !
     * You can think of this as how "sticky" the quicksand is.
     * 1.0 = no effect on player's movement
     * 0.0 = player effectively cannot move unless they manage to stop touching the
     * QS block.
     **/
    public double getTugLerp(double depthRaw) {
        return EasingHandler.doubleListInterpolate(depthRaw / 2, new double[] { 1.0, 1.0 });
    }

    /** how much the player is pulled backwards
     * You can think of this as how *strong* the sticky effect of the quicksand is
     * 1.0 = player *cannot* move away from the previous position, fully locked in
     * place.
     * 0.0 = no effect on player's movement
     */
    public double getTug(double depthRaw) {
        return EasingHandler.doubleListInterpolate(depthRaw / 2, new double[] { 0.0, 0.0 });
    }


}
