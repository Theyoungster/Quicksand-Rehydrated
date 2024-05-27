package net.mokai.quicksandrehydrated.block.quicksands;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mokai.quicksandrehydrated.block.QuicksandBase;
import net.mokai.quicksandrehydrated.util.EasingHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static org.joml.Math.abs;
import static org.joml.Math.clamp;

public class Quicksand extends QuicksandBase {
    public Quicksand(Properties pProperties) {super(pProperties);}

    public double getSink(double depthRaw) { return EasingHandler.doubleListInterpolate(depthRaw/2, new double[]{0.004, 0.004}); }

    public void struggleAttempt(@NotNull BlockState pState, @NotNull Entity pEntity, double struggleAmount) {

        // struggleAmount should be 0 .. 1, from 0 to 20 ticks

        double middlePoint = -1 * abs(struggleAmount - 0.5) + 0.5;

        // curve it
        middlePoint = 5.25 * (middlePoint*middlePoint) - 0.15; // ranges -0.5 to 0.5

        pEntity.addDeltaMovement(new Vec3(0.0, middlePoint, 0.0));

        pEntity.getLevel().playSound(pEntity, pEntity.blockPosition(), SoundEvents.SOUL_SOIL_STEP, SoundSource.BLOCKS, 0.25F, (pEntity.getLevel().getRandom().nextFloat() * 0.1F) + 0.5F);

    }

    public void firstTouch(Entity pEntity) {
        trySetCoverage(pEntity);
        if (pEntity.getDeltaMovement().y <= -0.333) {
            double mvt = pEntity.getDeltaMovement().y;
            mvt = clamp(mvt, -0.666, 0);
            pEntity.getLevel().playSound(pEntity, pEntity.blockPosition(), SoundEvents.MUD_FALL, SoundSource.BLOCKS, 0.3F+(float) mvt, (pEntity.getLevel().getRandom().nextFloat() * 0.1F) + 0.45F);
            pEntity.getLevel().playSound(pEntity, pEntity.blockPosition(), SoundEvents.SOUL_SOIL_STEP, SoundSource.BLOCKS, 0.3F+(float) mvt, (pEntity.getLevel().getRandom().nextFloat() * 0.1F) + 0.45F);
        }
    }

    @Override
    public void applyQuicksandEffects(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Entity pEntity) {

        super.applyQuicksandEffects(pState, pLevel, pPos, pEntity);

        Random rand = new Random();
        if (rand.nextInt(20*8) < 1) { // every 6 seconds
            pLevel.playSound(pEntity, pEntity.blockPosition(), SoundEvents.SILVERFISH_STEP, SoundSource.BLOCKS, 0.25F, (pEntity.getLevel().getRandom().nextFloat() * 0.1F) + 0.5F);
        }

    }

}
