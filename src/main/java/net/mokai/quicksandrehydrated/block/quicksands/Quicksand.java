package net.mokai.quicksandrehydrated.block.quicksands;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.mokai.quicksandrehydrated.block.quicksands.core.QuicksandBase;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.mokai.quicksandrehydrated.block.quicksands.core.QuicksandBehavior;
import net.mokai.quicksandrehydrated.entity.data.QuicksandEffect;
import net.mokai.quicksandrehydrated.entity.data.QuicksandEffectManager;
import net.mokai.quicksandrehydrated.entity.entityQuicksandVar;
import net.mokai.quicksandrehydrated.util.DepthCurve;
import net.mokai.quicksandrehydrated.util.EasingHandler;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Random;

import static net.mokai.quicksandrehydrated.util.EasingHandler.lerp;
import static org.joml.Math.abs;
import static org.joml.Math.clamp;

public class Quicksand extends QuicksandBase {

    public static final IntegerProperty LIQUEFACTION = IntegerProperty.create("liquefaction", 0, 7);

    public Quicksand(Properties pProperties, QuicksandBehavior QSB) {
        super(pProperties, QSB);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIQUEFACTION, 0));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LIQUEFACTION);
    }


    public double getSinkSpeed(double depth) {return 0.01d;}
    public double getWalkSpeed(double depth) {return EasingHandler.doubleListInterpolate(depth/2, new double[]{1, 0.1, 0.08, 0.05, 0.02, 0.01, 0, 0, 0});}
    public double getVertSpeed(double depth) {return .1d;}

    @Override
    public void firstTouch(BlockPos pPos, Entity pEntity, Level pLevel) {

        super.firstTouch(pPos, pEntity, pLevel);

        entityQuicksandVar pQSEnt = (entityQuicksandVar)pEntity;

        if (pEntity.getDeltaMovement().y <= -0.333) {
            double mvt = pEntity.getDeltaMovement().y;
            mvt = clamp(mvt, -0.666, 0);
            pLevel.playSound(pEntity, pEntity.blockPosition(), SoundEvents.MUD_FALL, SoundSource.BLOCKS, abs(0.3F+(float) mvt), (pLevel.getRandom().nextFloat() * 0.1F) + 0.45F);
            pLevel.playSound(pEntity, pEntity.blockPosition(), SoundEvents.SOUL_SOIL_STEP, SoundSource.BLOCKS, abs(0.3F+(float) mvt), (pLevel.getRandom().nextFloat() * 0.1F) + 0.45F);
        }

    }

    @Override
    public void quicksandMomentum(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity, double depth) {

        // get quicksand Variables
        double walk = getWalkSpeed(depth);
        double vert = getVertSpeed(depth);

        double liquid = pState.getValue(LIQUEFACTION);
        liquid = (liquid/7.0d);

        vert = 0.05 + (0.3 * liquid);

        // ranges 0.1 ... 2.45
        double sinkDepth = (liquid * (2.2) ) + 0.25;

        // this value is added to
        double a_ = Math.pow(sinkDepth/2, 2);

        // math. ranges 1 ... 0
        // https://www.desmos.com/calculator/ghoxz0dxk1
        double d0 = ( -Math.pow(depth/2, 2) + a_ ) * (1/a_);
        d0 = Math.max(d0, 0);
        d0 *= 0.002d;



        double sink = d0;

        // sinking is a replacement for gravity.
        Vec3 Momentum = pEntity.getDeltaMovement();

        entityQuicksandVar entQS = (entityQuicksandVar) pEntity;

        boolean playerFlying = false;
        if (pEntity instanceof Player) {
            Player p = (Player) pEntity;
            playerFlying = p.getAbilities().flying;
        }
        if (!playerFlying) {
//            if (vert != 0.0) {
//                sink = sink / vert; // counteract vertical thickness (?)
//            }
            Vec3 addVec = new Vec3(0, -sink, 0);
            entQS.addQuicksandAdditive(addVec);
        }

        Vec3 thicknessVector = new Vec3(walk, vert, walk);
        entQS.multiOrSetQuicksandMultiplier(thicknessVector);

    }



    @Override
    public void applyQuicksandEffects(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Entity pEntity) {

        super.applyQuicksandEffects(pState, pLevel, pPos, pEntity);
        double depth = getDepth(pLevel, pPos, pEntity);
        double walk = getWalkSpeed(depth);
        entityQuicksandVar entQS = (entityQuicksandVar) pEntity;
        double liquid = pState.getValue(LIQUEFACTION);

        // randomly play sounds
        Random rand = new Random();
        if (rand.nextInt(20*8) < 1) { // every 6 seconds
            pLevel.playSound(pEntity, pEntity.blockPosition(), SoundEvents.SILVERFISH_STEP, SoundSource.BLOCKS, 0.25F, (pEntity.level().getRandom().nextFloat() * 0.1F) + 0.5F);
        }

        // acquire entity horizontal momentum
        Vec3 momentumVec3 = pEntity.getDeltaMovement();
        momentumVec3 = new Vec3(momentumVec3.x, 0, momentumVec3.z);
        double horizontalMomentum = momentumVec3.length();

        // apply a small amount of sink force when moving. it's like quicksand!
        double sinkAmount = horizontalMomentum * 0.1;
        entQS.addQuicksandAdditive(new Vec3(0.0, -sinkAmount, 0.0));



        // this is bad and results in client/server desync
        Random rng = new Random();
        double rngVal = rng.nextDouble();

        double movementChance = 0.0;
        if (walk != 0.0) {
            movementChance = (horizontalMomentum / walk); // doesn't have much effect
        }

        double liquefyChance = movementChance - Math.pow(liquid/7, 1.5) * 0.3;

        if (rngVal < liquefyChance) {
            liquefyQuicksand(pLevel, pState, pPos);
        }

    }

    @Override
    public void struggleAttempt(@NotNull BlockState pState, @NotNull Entity pEntity, double struggleAmount) {

        double struggleForce = struggleAmount * 0.05;

//        Random rng = new Random();
//        double rngVal = rng.nextDouble(-0.1, 0.05);
//        struggleForce += rngVal;

        pEntity.addDeltaMovement(new Vec3(0.0, struggleForce, 0.0));

//        pEntity.level().playSound(pEntity, pEntity.blockPosition(), SoundEvents.HONEY_BLOCK_PLACE, SoundSource.BLOCKS, 0.25F, (pEntity.level().getRandom().nextFloat() * 0.1F) + 0.45F);
        pEntity.level().playSound(pEntity, pEntity.blockPosition(), SoundEvents.SILVERFISH_STEP, SoundSource.BLOCKS, 0.3F, (pEntity.level().getRandom().nextFloat() * 0.1F) + 0.5F);

        if (pEntity.level().getRandom().nextDouble() < 0.25) {
            pEntity.addDeltaMovement(new Vec3(0.0, 0.5, 0.0));
            liquefyQuicksand(pEntity.level(), pState, pEntity.blockPosition());
        }

        for (int i = 0; i < 5; i++) {
            spawnParticles(pEntity.level(), pEntity.blockPosition());
        }

    }


    public void liquefyQuicksand(Level pLevel, BlockState pState, BlockPos pPos) {
        if (pState.getBlock() instanceof Quicksand) {
            int liquid = pState.getValue(LIQUEFACTION);
            if (liquid < 7) {

                pLevel.setBlock(pPos, pState.setValue(LIQUEFACTION, liquid+1), 3);
                for (int i = 0; i < 3; i++) {
                    spawnParticles(pLevel, pPos);
                }
                pLevel.playSound(null, pPos, SoundEvents.SILVERFISH_STEP, SoundSource.BLOCKS, 0.3F, (pLevel.getRandom().nextFloat() * 0.1F) + 0.5F);

            }
        }
    }
    public void solidifyQuicksand(Level pLevel, BlockState pState, BlockPos pPos) {
        if (pState.getBlock() instanceof Quicksand) {
            int liquid = pState.getValue(LIQUEFACTION);
            if (liquid > 0) {
                pLevel.setBlock(pPos, pState.setValue(LIQUEFACTION, liquid-1), 3);
            }
        }
    }

    public void randomTick(BlockState pState, ServerLevel pSLevel, BlockPos pPos, RandomSource pRandom) {
        solidifyQuicksand(pSLevel, pState, pPos);
//        if (pRandom.nextInt(0, 2) == 0) {
//
//        }
    }


    public static final Vector3f QUICKSAND_COLOR = Vec3.fromRGB24(12893318).toVector3f();
    private static void spawnParticles(Level pLevel, BlockPos pPos) {

        RandomSource randomsource = pLevel.random;
        Direction direction = Direction.UP;

        // taken from redstone ore block code

        Direction.Axis direction$axis = direction.getAxis();
        double d1 = direction$axis == Direction.Axis.X ? 0.5D + 0.5625D * (double) direction.getStepX() : (double) randomsource.nextFloat();
        double d2 = direction$axis == Direction.Axis.Y ? 0.5D + 0.5625D * (double) direction.getStepY() : (double) randomsource.nextFloat();
        double d3 = direction$axis == Direction.Axis.Z ? 0.5D + 0.5625D * (double) direction.getStepZ() : (double) randomsource.nextFloat();

        pLevel.addParticle(new DustParticleOptions(QUICKSAND_COLOR, 1.0F), (double) pPos.getX() + d1, (double) pPos.getY() + d2, (double) pPos.getZ() + d3, 0.0D, 0.0D, 0.0D);


    }

}
