package net.mokai.quicksandrehydrated.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.mokai.quicksandrehydrated.entity.EntityBubble;
import net.mokai.quicksandrehydrated.entity.entityQuicksandVar;
import net.mokai.quicksandrehydrated.registry.ModParticles;
import net.mokai.quicksandrehydrated.util.EasingHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static net.mokai.quicksandrehydrated.QuicksandRehydrated.MOD_ID;
import static net.mokai.quicksandrehydrated.util.ModTags.Blocks.QUICKSAND_DROWNABLE;
import static net.mokai.quicksandrehydrated.util.ModTags.Fluids.QUICKSAND_DROWNABLE_FLUID;


public class QuicksandBase extends Block implements QuicksandInterface {

    public QuicksandBase(Properties pProperties) {
        super(pProperties);
    }
    private final Random rng = new Random();

    // ----- OVERRIDE AND MODIFY THESE VALUES FOR YOUR QUICKSAND TYPE ----- //

    public String getTex() {return "qsrehydrated:textures/block/quicksand.png";}

    public double getOffset(BlockState blockstate) { return 0d; } // This value is subtracted from depth for substances that aren't full blocks.

    public double getStruggleSensitivity() {return .01d;} //.05 is very sensitive, .2 is moderately sensitive, and .8+ is very un-sensitive.

    public double getBubblingChance() { return .75d; } // Does this substance bubble when you sink?

    // Sinking speed. Lower is slower.
    // normalized against the vertSpeed, so this value remains effective in spite of how thick the quicksand is.
    public double[] getSink() { return new double[]{.001d, .001d, .001d, .001d, .001d}; }

    public double[] vertSpeed() { return new double[]{.3d, .2d, .1d, .05d, .025d}; } // Vertical movement speed

    public double[] walkSpeed() { return new double[]{0.8d, 0.7d, .5d, .25d, 0d}; } // Horizontal movement speed





    public double[] getTugLerp() {return new double[]{1d, 1d, 1d, 1d, 1d}; } // the previous position will lerp towards the player this amount *per tick* !
    public double[] getTug() {return new double[]{0d, 0d, 0d, 0d, 0d}; } // how much force is applied on the player, towards the previous Position, *per tick* !

    public double[] gravity() { return new double[] {0d, 0d, .1d, .2d, .3d}; } // Gravity pulls you towards the center of a given mess block- potentially away from any ledges one could climb up on.
    public int[] jumpPercentage() { return new int[]{30, 20, 0, 0, 0}; } // Chance per tick to be able to jump.

    public double getCustomDeathMessageOdds() { return .25; }

    public void KILL(LivingEntity pEntity) {
        pEntity.hurt(new DamageSource(MOD_ID + "_quicksand"), 2);
    }

    // 

    // ----- OKAY YOU CAN STOP OVERRIDING AND MODIFYING VALUES NOW ----- //

    public int toInt(double y) {
        if (y < .375) {
            return 0; //         Surface: 0 - .375
        } else if (y < .75) {
            return 1; //         Knee deep: .375 - .75
        } else if (y < 1.25) {
            return 2; //         Waist deep: .75 - 1.25
        } else if (y < 1.625) {
            return 3; //         Chest deep: 1.25 - 1.625
        } else {
            return 4; //         Under: 1.625+
        }
    }

    public double getDepth(Level pLevel, BlockPos pPos, Entity pEntity) { return EasingHandler.getDepth(pEntity, pLevel, pPos, getOffset(null)); }

    public double getSink(double depth) { return getSink()[toInt(depth)]; }
    public double getWalk(double depth) { return walkSpeed()[toInt(depth)]; }
    public double getVert(double depth) { return vertSpeed()[toInt(depth)]; }


    public double getTugLerp(double depth) { return getTugLerp()[toInt(depth)]; }
    public double getTug(double depth) { return getTug()[toInt(depth)]; }


    public boolean getJump(double depth) {
        if (depth < 0.375) {return true;}
        return false;
    }

    public void quicksandTugMove(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity, double depth) {

        entityQuicksandVar es = (entityQuicksandVar) pEntity;

        Vec3 currentPos = pEntity.getPosition(0);

        // Get the Previous Position variable
        Vec3 prevPos = es.getPreviousPosition();

        // move previous pos towards player a tiny bit
        es.setPreviousPosition(prevPos.lerp(currentPos, getTugLerp(depth)));

    }

    public void quicksandTug(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity, double depth) {

        Vec3 Momentum = pEntity.getDeltaMovement();
        entityQuicksandVar es = (entityQuicksandVar) pEntity;

        // the difference between the entity position, and the previous position.
        Vec3 differenceVec = es.getPreviousPosition().subtract( pEntity.getPosition(0));

        // apply momentum towards previous pos to entity
        double spd = getTug(depth);
        Vec3 addMomentum = differenceVec.multiply(new Vec3(spd, spd, spd));
        pEntity.setDeltaMovement(Momentum.add(addMomentum));

    }

    public void quicksandMomentum(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity, double depth) {

        // get quicksand Variables
        double walk = getWalk(depth);
        double vert = getVert(depth);
        double sink = getSink(depth);

        // sinking is a replacement for gravity.
        Vec3 Momentum = pEntity.getDeltaMovement();
        boolean playerFlying = false;
        if (pEntity instanceof Player) {
            Player p = (Player) pEntity;
            playerFlying = p.getAbilities().flying;
        }
        if (!playerFlying) {
            sink = sink / vert; // counteract vertical thickness
            Momentum = Momentum.add(0.0, -sink, 0.0);
        }

        Momentum = Momentum.multiply(walk, vert, walk);
        pEntity.setDeltaMovement(Momentum);

    }

    public void applyQuicksandEffects(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Entity pEntity) {

        if (pEntity instanceof EntityBubble) {return;}

        double depth = getDepth(pLevel, pPos, pEntity);

        if (depth >= 0) {

            // there are three main effects that happen every tick.


            // first, the quicksand's main effects are applied. Thickness and sinking.
            quicksandMomentum(pState, pLevel, pPos, pEntity, depth);



            // the next two deal with the entity's "Previous Position" variable.
            // This can be used in different ways to get different effects.

            // first the player is pushed towards this position a small amount
            quicksandTug(pState, pLevel, pPos, pEntity, depth);

            // then we move that position a little bit towards the player
            quicksandTugMove(pState, pLevel, pPos, pEntity, depth);

            // By default, the tug position is constantly set to the entity's position, and applies no force on the entity towards it.



            // Some movement stuff. Dealing with whether the entity is "OnGround"
            // whether they can jump, and step out onto a solid block.

            if (getJump(depth)) {
                if (pLevel.getBlockState(pPos.above()).isAir()) {

                    boolean playerFlying = false;
                    if (pEntity instanceof Player) {
                        Player p = (Player) pEntity;
                        playerFlying = p.getAbilities().flying;
                    }

                    if (!playerFlying) {
                        // if the player is flying ... they shouldn't be set on ground! cause then they can't fly.
                        // quicksand effects are still dealt, above, though.
                        pEntity.setOnGround(true);
                    }

                }
            }
            else {
                // set on false even if they're at bottom
                pEntity.setOnGround(false);
                pEntity.resetFallDistance();
            }

        }

    }

    public void struggleAttempt(@NotNull BlockState pState, @NotNull Entity pEntity, double struggleAmount) {
        // runs when the player struggles in a block of this type. Default has nothing happen.

        Vec3 pos = pEntity.position();

        pEntity.getLevel().addParticle(ModParticles.QUICKSAND_BUBBLE_PARTICLES.get(),
                pos.x, pos.y, pos.z, 0, 0, 0);

    }

    @Override
    public void entityInside(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Entity pEntity) {

        // when an entity is touching a quicksand block ...

        double depth = getDepth(pLevel, pPos, pEntity);

        if (depth >= 0) {

            // all that needs to be done is set the fact that this entity is in quicksand.
            // Entities choose which quicksand block to run the applyQuicksandEffects function from.

            boolean canJump = getJump(depth);
            entityQuicksandVar es = (entityQuicksandVar) pEntity;

            // more movement variables.
            es.setInQuicksand(true);
            pEntity.resetFallDistance();

        }

    }


    public void deathMessage(Level pLevel, LivingEntity pEntity) {
        if (pLevel.getLevelData().isHardcore()) {
            pEntity.hurt(new DamageSource(MOD_ID + "_hardcore"), 2);
        } else {
            double p = Math.random();
            if (p > getCustomDeathMessageOdds()) {
                pEntity.hurt(new DamageSource(MOD_ID + "_generic_" + (int) (Math.random() * 3)), 2);
            } else {
                KILL(pEntity);
            }
        }
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {

    }

    public void spawnBubble(BlockState pState, Level pLevel, Vec3 pos, BlockPos pPos, BlockState bs) {
        BlockState upOne = pLevel.getBlockState(pPos.above());
        if (checkDrownable(pState) && !checkDrownable(upOne)) {
            double offset = 0d;
            Block gb = pState.getBlock();
            if (gb instanceof QuicksandInterface) {
                offset = ((QuicksandInterface)gb).getOffset(pState);
            }
            pos = pos.add( new Vec3(0, -offset, 0) );
            spawnBubble(pLevel, pos);
        }
    }

    public void spawnBubble(Level pLevel, Vec3 pos) {
        if (!pLevel.isClientSide()) {
            EntityBubble.spawn(pLevel, pos, Blocks.COAL_BLOCK.defaultBlockState());
        }
    }



    public boolean checkDrownable(BlockState pState) {
        return pState.getTags().toList().contains(QUICKSAND_DROWNABLE) || pState.getFluidState().getTags().toList().contains(QUICKSAND_DROWNABLE_FLUID);
    }

}
