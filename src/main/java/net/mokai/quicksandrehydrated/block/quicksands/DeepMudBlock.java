package net.mokai.quicksandrehydrated.block.quicksands;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.mokai.quicksandrehydrated.block.quicksands.core.QuicksandBase;
import net.mokai.quicksandrehydrated.entity.entityQuicksandVar;
import net.mokai.quicksandrehydrated.util.DEPTH;
import net.mokai.quicksandrehydrated.util.EasingHandler;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Random;

import static net.minecraft.core.particles.DustColorTransitionOptions.SCULK_PARTICLE_COLOR;
import static org.joml.Math.lerp;

public class DeepMudBlock extends QuicksandBase {

    public String coverageTexture() {
        return "qsrehydrated:textures/entity/coverage/mud_coverage.png";
    }

    public DeepMudBlock(Properties pProperties) {super(pProperties);}

    // helps determine what behaviour the mud should have. Is the entity deep enough to get stuck in place?
    public double stuckDepth = DEPTH.KNEE;
    public boolean depthIsStuck(double depthRaw) {
        return depthRaw >= stuckDepth;
    }

    public double getSink(double depthRaw) { return 0.0; } // no sinky

    public double getWalk(double depthRaw) {
        if (!depthIsStuck(depthRaw)) {
            return 0.99;
        }
        else {
            double normalDepth = (depthRaw-stuckDepth) / (2-stuckDepth); // start the array at knee depth
            return EasingHandler.doubleListInterpolate(normalDepth, new double[]{0.6, 0.35, 0.1, 0.0, 0.0});
        }
    }

    public double getVert(double depthRaw) {
        if (!depthIsStuck(depthRaw)) {
            // clamp it from 0 to DEPTH_KNEE

            return 0.3;

//            Math.max(0, Math.min(stuckDepth, depthRaw));
//            double normalDepth = depthRaw / stuckDepth;
//            return EasingHandler.doubleListInterpolate(normalDepth, new double[]{0.99, 0.6});
        }
        else {
            double normalDepth = (depthRaw-stuckDepth) / (2-stuckDepth); // start the array at knee depth
            return EasingHandler.doubleListInterpolate(normalDepth, new double[]{0.3, 0.1});
        }
    }

    public double getTugLerp(double depthRaw) {
        if (!depthIsStuck(depthRaw)) {
            return 1.0; // go right to player if above
        }
        else {
            return 0.001;
            //double normalDepth = (depthRaw-stuckDepth) / (2-stuckDepth); // start the array at knee depth
            //return EasingHandler.doubleListInterpolate(normalDepth, new double[]{0.001}); // player not allowed to move
        }
    }



    public double getTug(double depthRaw) {
        if (!depthIsStuck(depthRaw)) {
            return 0.0; // go right to player if above
        }
        else {
            double normalDepth = (depthRaw-stuckDepth) / (2-stuckDepth); // start the array at knee depth
            return EasingHandler.doubleListInterpolate(normalDepth, new double[]{0.15, 0.3}); // player not allowed to move
        }
    }


    @Override
    public void entityInside(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Entity pEntity) {

        super.entityInside(pState, pLevel, pPos, pEntity);

        double depthRaw = getDepth(pLevel, pPos, pEntity);

        if (depthIsStuck(depthRaw)) {

            entityQuicksandVar eqs = (entityQuicksandVar) pEntity;
            Random rng = new Random();

            Vec3 momentumVec3 = pEntity.getDeltaMovement();
            momentumVec3 = new Vec3(momentumVec3.x, 0, momentumVec3.z);

            double momentum = momentumVec3.length();

            double maxMomentum = 0.3;
            double sinkForce = Math.max(-maxMomentum, Math.min(momentum, maxMomentum)) / 10;

            pEntity.addDeltaMovement(new Vec3(0, -sinkForce, 0));

        }

        tryApplyCoverage(pState, pLevel, pPos, pEntity);

    }



    public void quicksandTugMove(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity, double depth) {

        entityQuicksandVar es = (entityQuicksandVar) pEntity;

        Vec3 currentPos = pEntity.getPosition(0);

        // Get the Previous Position variable
        Vec3 prevPos = es.getPreviousPosition();

        double lerpAmountHorizontal = getTugLerp(depth);

        Vec3 newPrevPos = new Vec3(
            lerp(prevPos.x(), currentPos.x(), lerpAmountHorizontal),
            lerp(prevPos.y(), currentPos.y(), 1.0), // always go right to player Y level
            lerp(prevPos.z(), currentPos.z(), lerpAmountHorizontal)
        );

        // move previous pos towards player by set amount
        es.setPreviousPosition(newPrevPos);

    }

    public void quicksandTug(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity, double depth) {

        Vec3 Momentum = pEntity.getDeltaMovement();
        entityQuicksandVar es = (entityQuicksandVar) pEntity;

        // the difference between the entity position, and the previous position.
        Vec3 differenceVec = es.getPreviousPosition().subtract( pEntity.getPosition(0));

        // apply momentum towards previous pos to entity
        double spd = getTug(depth);
        Vec3 addMomentum = differenceVec.multiply(new Vec3(spd, 0.0, spd)); // do not affect y at all
        pEntity.setDeltaMovement(Momentum.add(addMomentum));

    }





    public void struggleAttempt(@NotNull BlockState pState, @NotNull Entity pEntity, double struggleAmount) {

        double min = 0.25;
        struggleAmount /= 3; // half a block for the full second windup
        double struggleForce = min+struggleAmount;

        pEntity.addDeltaMovement(new Vec3(0.0, struggleForce, 0.0));
        pEntity.getLevel().playSound(pEntity, pEntity.blockPosition(), SoundEvents.HONEY_BLOCK_PLACE, SoundSource.BLOCKS, 0.25F, (pEntity.getLevel().getRandom().nextFloat() * 0.1F) + 0.45F);

    }



    public static final Vector3f MUD_COLOR = Vec3.fromRGB24(3815485).toVector3f();

    // normal block things v



    public void firstTouch(Entity pEntity) {
        trySetCoverage(pEntity);
        if (pEntity.getDeltaMovement().y <= -0.5) {
            pEntity.getLevel().playSound(pEntity, pEntity.blockPosition(), SoundEvents.HONEY_BLOCK_FALL, SoundSource.BLOCKS, 0.4F, (pEntity.getLevel().getRandom().nextFloat() * 0.1F) + 0.45F);
        }
    }



    private static void spawnParticles(Level pLevel, BlockPos pPos) {

        RandomSource randomsource = pLevel.random;

        Direction direction = Direction.UP;

        // taken from redstone ore block code
        BlockPos blockpos = pPos.relative(direction);

        Direction.Axis direction$axis = direction.getAxis();
        double d1 = direction$axis == Direction.Axis.X ? 0.5D + 0.5625D * (double) direction.getStepX() : (double) randomsource.nextFloat();
        double d2 = direction$axis == Direction.Axis.Y ? 0.5D + 0.5625D * (double) direction.getStepY() : (double) randomsource.nextFloat();
        double d3 = direction$axis == Direction.Axis.Z ? 0.5D + 0.5625D * (double) direction.getStepZ() : (double) randomsource.nextFloat();
        //pLevel.addParticle(MUD, d1, d2, d3, 0.0D, 0.0, 0.0D);

        pLevel.addParticle(new DustParticleOptions(MUD_COLOR, 1.0F), (double) pPos.getX() + d1, (double) pPos.getY() + d2, (double) pPos.getZ() + d3, 0.0D, 0.0D, 0.0D);


    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        //spawnParticles(pLevel, pPos);
    }


}
