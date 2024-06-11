package net.mokai.quicksandrehydrated.block.quicksands;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mokai.quicksandrehydrated.block.quicksands.core.QuicksandBase;
import net.mokai.quicksandrehydrated.block.quicksands.core.QuicksandBehavior;
import net.mokai.quicksandrehydrated.entity.entityQuicksandVar;
import net.mokai.quicksandrehydrated.registry.ModParticles;
import net.mokai.quicksandrehydrated.util.EasingHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static org.joml.Math.clamp;
import static org.joml.Math.lerp;

public class LivingSlime extends QuicksandBase {

    public String coverageTexture() {
        return "qsrehydrated:textures/entity/coverage/slime_coverage.png";
    }

    public LivingSlime(Properties pProperties, QuicksandBehavior QSB) {super(pProperties, QSB);}



    public double getSink(double depthRaw) { return EasingHandler.doubleListInterpolate(depthRaw/2, new double[]{.001d, .009d, .009d, .009d, .009d}); }

    public double getWalk(double depthRaw) { return EasingHandler.doubleListInterpolate(depthRaw/2, new double[]{1d, .9d, .7d, .4d, .2d}); }

    public double getVert(double depthRaw) { return EasingHandler.doubleListInterpolate(depthRaw/2, new double[]{.5d, .4d, .4d, .4d, .4d}); }

    public double getTugLerp(double depthRaw) { return EasingHandler.doubleListInterpolate(depthRaw/2, new double[]{0.025d, 0.025d, 0.025d, 0.025d, 0.025d}); }

    public double getTug(double depthRaw) { return EasingHandler.doubleListInterpolate(depthRaw/2, new double[]{0.08d, 0.07333d, 0.0666d, 0.06d, 0.06d}); }





    public void quicksandTugMove(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {

        double depth = getDepth(pLevel, pPos, pEntity);
        entityQuicksandVar es = (entityQuicksandVar) pEntity;

        Vec3 currentPos = pEntity.getPosition(0);

        // Get the Previous Position variable
        Vec3 prevPos = es.getPreviousPosition();

        // move previous pos towards player a tiny bit
        double lerpAmountHorizontal = getTugLerp(depth);
        double lerpAmountVertical = getTugLerp(depth);

        // if the entity's position is BELOW the previous position, move it down faster
//        if (currentPos.y < prevPos.y) {
//            lerpAmountVertical = 1;
//        }

        Vec3 newPrevPos = new Vec3(
            lerp(prevPos.x(), currentPos.x(), lerpAmountHorizontal),
            lerp(prevPos.y(), currentPos.y(), lerpAmountVertical),
            lerp(prevPos.z(), currentPos.z(), lerpAmountHorizontal)
        );

        newPrevPos = newPrevPos.add(0, -.02, 0);

        es.setPreviousPosition(newPrevPos);

    }



    public void struggleAttempt(@NotNull BlockState pState, @NotNull Entity pEntity, double struggleAmount) {

        // block pos has potential to be incorrect?
        double depth = getDepth(pEntity.level(), pEntity.blockPosition(), pEntity);
        entityQuicksandVar es = (entityQuicksandVar) pEntity;

        Vec3 entityPosition = pEntity.getPosition(0);
        Vec3 prevPos = es.getPreviousPosition();

//        Vec3 momentumVec3 = pEntity.getDeltaMovement();
//        momentumVec3 = new Vec3(momentumVec3.x, 0, momentumVec3.z);
//        double momentum = momentumVec3.length();

        double momentum = (entityPosition.subtract(prevPos)).length(); // difference between entity and the prev pos

        momentum = 0.3 - momentum;
        momentum = clamp(momentum, -0.3, 0.3);

        double offsetAmount = struggleAmount * (momentum/3);

        pEntity.addDeltaMovement(new Vec3(0.0, offsetAmount*0.75, 0.0));
        entityPosition = entityPosition.add(0.0, offsetAmount*1.5, 0.0);
        es.setPreviousPosition(entityPosition);

        pEntity.level().playSound(pEntity, pEntity.blockPosition(), SoundEvents.HONEY_BLOCK_STEP, SoundSource.BLOCKS, 0.25F, (pEntity.level().getRandom().nextFloat() * 0.1F) + 0.45F);

    }



    // normal block things v

    private static void spawnParticles(Level pLevel, BlockPos pPos) {

        RandomSource randomsource = pLevel.random;

        if (randomsource.nextInt(50) == 0) {

            Direction direction = Direction.UP;

            // taken from redstone ore block code
            BlockPos blockpos = pPos.relative(direction);
            if (!pLevel.getBlockState(blockpos).isSolidRender(pLevel, blockpos)) {
                Direction.Axis direction$axis = direction.getAxis();
                double d1 = direction$axis == Direction.Axis.X ? 0.5D + 0.5625D * (double) direction.getStepX() : (double) randomsource.nextFloat();
                double d2 = direction$axis == Direction.Axis.Y ? 0.5D + 0.5625D * (double) direction.getStepY() : (double) randomsource.nextFloat();
                double d3 = direction$axis == Direction.Axis.Z ? 0.5D + 0.5625D * (double) direction.getStepZ() : (double) randomsource.nextFloat();
                pLevel.addParticle(ModParticles.QUICKSAND_BUBBLE_PARTICLES.get(), (double) pPos.getX() + d1, (double) pPos.getY() + d2, (double) pPos.getZ() + d3, 0.0D, 0.0D, 0.0D);
            }

        }

    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        BlockState aboveState = pLevel.getBlockState(pPos.above());
        if (aboveState.isAir()) {
            spawnParticles(pLevel, pPos);
        }
    }


    // half transparent block
    public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pSide) {
        return pAdjacentBlockState.is(this) ? true : super.skipRendering(pState, pAdjacentBlockState, pSide);
    }

    // copied from stained glass
    public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
        return Shapes.empty();
    }

    // Doesn't occlude not Ambiently Occlude!
    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return Shapes.empty();
    }

    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 1.0F;
    }

    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return true;
    }

}
