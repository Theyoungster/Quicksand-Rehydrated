package net.mokai.quicksandrehydrated.block.quicksands;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.mokai.quicksandrehydrated.entity.entityQuicksandVar;
import net.mokai.quicksandrehydrated.registry.ModParticles;
import org.jetbrains.annotations.NotNull;

import static org.joml.Math.lerp;

public class LivingSlime extends QuicksandBase {

    public LivingSlime(Properties pProperties) {super(pProperties);}



    public double[] getSink() { return new double[]{.001d, .009d, .009d, .009d, .009d}; }

    public double[] vertSpeed() { return new double[]{.5d, .4d, .4d, .4d, .4d}; } // Vertical movement speed

    public double[] walkSpeed() { return new double[]{1d, .9d, .7d, .4d, .2d}; } // Horizontal movement speed

    public double[] getTugLerp() {return new double[]{0.025d, 0.025d, 0.025d, 0.025d, 0.025d}; } // the previous position will lerp towards the player this amount *per tick* !
    public double[] getTug() {return new double[]{0.08d, 0.07333d, 0.0666d, 0.06d, 0.06d}; } // how much force is applied on the player, towards the previous Position, *per tick* !



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
        if (currentPos.y < prevPos.y) {
            lerpAmountVertical = 1;
        }

        Vec3 newPrevPos = new Vec3(
                lerp(prevPos.x(), currentPos.x(), lerpAmountHorizontal),
                lerp(prevPos.y(), currentPos.y(), lerpAmountVertical),
                lerp(prevPos.z(), currentPos.z(), lerpAmountHorizontal)
        );

        newPrevPos = newPrevPos.add(0, -.01, 0);

        es.setPreviousPosition(newPrevPos);

    }



    public void struggleAttempt(@NotNull BlockState pState, @NotNull Entity pEntity, double struggleAmount) {

        // block pos has potential to be incorrect
        double depth = getDepth(pEntity.getLevel(), pEntity.blockPosition(), pEntity);
        entityQuicksandVar es = (entityQuicksandVar) pEntity;

        Vec3 prevPos = pEntity.getPosition(0);

        double offsetAmount = struggleAmount * 0.4;
        prevPos = prevPos.add(0.0, -offsetAmount, 0.0);

        es.setPreviousPosition(prevPos);

    }



    // normal block things v

    private static void spawnParticles(Level pLevel, BlockPos pPos) {

        RandomSource randomsource = pLevel.random;

        if (randomsource.nextInt(10) == 0) {

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

    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 1.0F;
    }

    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return true;
    }

}
