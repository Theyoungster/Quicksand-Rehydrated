package net.mokai.quicksandrehydrated.block.quicksands.core;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.entity.EntityBubble;
import net.mokai.quicksandrehydrated.entity.coverage.CoverageEntry;
import net.mokai.quicksandrehydrated.entity.coverage.PlayerCoverage;
import net.mokai.quicksandrehydrated.entity.data.QuicksandEffect;
import net.mokai.quicksandrehydrated.entity.data.QuicksandEffectManager;
import net.mokai.quicksandrehydrated.entity.entityQuicksandVar;
import net.mokai.quicksandrehydrated.entity.playerStruggling;
import net.mokai.quicksandrehydrated.util.EasingHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

import static net.mokai.quicksandrehydrated.util.ModTags.Blocks.QUICKSAND_DROWNABLE;
import static net.mokai.quicksandrehydrated.util.ModTags.Fluids.QUICKSAND_DROWNABLE_FLUID;
import static org.joml.Math.abs;
import static org.joml.Math.clamp;

/**
 * This is where the <font color=red>M</font>
 * <font color=orange>A</font>
 * <font color=yellow>G</font>
 * <font color=green>I</font>
 * <font color=blue>C</font> is going on!
 * This is the base of Blocks to function as Quicksand blocks.
 * Override the specific methods to achieve non-default behavior when implementing other
 * sinking blocks.
 */
public class QuicksandBase extends Block implements QuicksandInterface {

    private final Random rng = new Random();

    public QuicksandBehavior QSBehavior;
    public QuicksandBehavior getQuicksandBehavior() {return QSBehavior;}

    public QuicksandBase(Properties pProperties, QuicksandBehavior QuicksandBehavior) {
        super(pProperties);
        this.QSBehavior = QuicksandBehavior;
    }



    public String getCoverageTexture() {return QSBehavior.getCoverageTexture();}

    public String getSecretDeathMessage() {return QSBehavior.getSecretDeathMessage();}
    public double getSecretDeathMessageChance() {return QSBehavior.getSecretDeathMessageChance();}

    /**
     * The probability, per tick, of producing a bubble while an entity is in the block.
     * @return The probability. <code>0</code> = No bubbles. <code>1</code> = Always bubbles.
     */
    public double getBubbleChance(double depth) {return QSBehavior.getBubbleChance(depth);}

    /**
     * The sinking speed, depending on the depth.
     * normalized against the vertSpeed, so this value will remain effective - even
     * if the quicksand is very thick.
     * @param depth The depth in blocks. <code>0</code> is exactly on surface level.
     * @return The sinking value. Lower value means slower sinking.
     */
    public double getSinkSpeed(double depth) {return QSBehavior.getSinkSpeed(depth);}

    /**
     * Horizontal movement speed depending on the depth.
     * Thickness - but inverse.
     * @param depth The depth of the object. <code>0</code> is exactly on surface level.
     * @return The inverse resistance when walking. <code>0</code> = very thick; <code>1</code> = very thin.
     */
    public double getWalkSpeed(double depth) {return QSBehavior.getWalkSpeed(depth);}

    /**
     * Vertical movement speed depending on the depth.
     * Same as <code>getWalk()</code>
     * @param depth The depth of the object.
     * @return The inverse resistance when moving up/down. <code>0</code> = very thick; <code>1</code> = very thin.
     */
    public double getVertSpeed(double depth) {return QSBehavior.getVertSpeed(depth);} //TODO: invert this back

    /** Used by position based wobble.
     * How strongly the quicksand pulls the player horizontally towards the wobble point. 1 is full strength.
     * @return Horizontal wobble strength. [0, 1]
     */
    public double getWobbleTugHorizontal(double depth) {return QSBehavior.getWobbleTugHorizontal(depth);}

    /** Used by position based wobble.
     * How strongly the quicksand pulls the player vertically towards the wobble point. 1 is full strength.
     * If not set, will default to equal the horizontal wobble strength.
     * @param depth
     * @return Vertical wobble strength. [0, 1]
     */
    public double getWobbleTugVertical(double depth) {return QSBehavior.getWobbleTugVertical(depth);}

    /** Used by both Wobble types.
     * In position based wobble, How quickly the wobble point approaches the player, as a percentage of the distance per tick.
     * In momentum based wobble, how much of the momentum is applied to the player, as a percentage of the total per tick.
     * You can think of this as how "sticky" the quicksand is.
     * 1.0 = no effect on player's movement
     * 0.0 = player effectively cannot move unless they manage to stop touching the QS.
     * @param depth
     * @return Vertical wobble strength. [0, 1]
     **/
    public double getWobbleMove(double depth) {return QSBehavior.getWobbleMove(depth);}

    /** Used by momentum based wobble.
     * How much the wobble momentum decays, percentage per tick.
     * 1.0 = momentum never decays
     * 0.0 = momentum completely decays every tick
     * @param depth
     * @return Vertical tug strength. [0, 1]
     **/
    public double getWobbleDecay(double depth) {return QSBehavior.getWobbleDecay(depth);}

    /** Used by momentum based wobble.
     * How much the wobble actually wobbles back and forth.
     * Mathematically, how much of the actual wobble momentum is added to the wobble momentum's *momentum* as a percentage per tick.
     * 1.0 = flips back and forth every single tick.
     * 0.0 = wobble does not change. just drifts the player in some random direction.
     * @param depth
     * @return Vertical tug strength. [0, 1]
     **/
    public double getWobbleRebound(double depth) {return QSBehavior.getWobbleRebound(depth);}

    /** Used by momentum based wobble.
     * How much of the entity's momentum is added to the wobble momentum.
     * You could think of it as how thick the quicksand is- separate from the Vert and Walk speeds.
     * Mathematically, what percent of the entity's momentum is added to the wobble momentum per tick.
     * 1.0 = 100% of the entity's momentum is added
     * 0.0 = 0% added, no effect.
     * @param depth
     * @return Vertical tug strength. [0, 1]
     **/
    public double getWobbleApply(double depth) {return QSBehavior.getWobbleApply(depth);}



    /** The lowest point the TugPoint will sink to.
     * @return The buoyancy depth.
     */
    public double getBuoyancyPoint() {return QSBehavior.getBuoyancyPoint();}

    /** If the player is above this height, they can simply jump out of the quicksand.
     * @param depth
     * @return true if the player should be allowed to jump out.
     */
    public boolean canStepOut(double depth) {return QSBehavior.canStepOut(depth);}

    /**
     * How far from the top of a block the surface actually is.
     * For example, Mud and Soulsand would use 0.125 (or 1/8th of a block) before sinking begins.
     * @param blockstate The BlockState.
     * @return distance from the top of the block.
     */
    public double getOffset(BlockState blockstate) {return QSBehavior.getOffset();}


    /** The depth, in blocks, that the entity has sunk. Scales based on the entity's height, but is 1:1 for a Player.
     * @return depth, in blocks.
     */
    public double getDepth(Level pLevel, BlockPos pPos, Entity pEntity) {
        return EasingHandler.getDepth(pEntity, pLevel, pPos, getOffset(pLevel.getBlockState(pPos)));
    }


    public void quicksandMomentum(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity, double depth) {

        // get quicksand Variables
        double walk = getWalkSpeed(depth);
        double vert = getVertSpeed(depth);
        double sink = getSinkSpeed(depth);

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

    public void applyQuicksandEffects(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos,
            @NotNull Entity pEntity) {

        if (pEntity instanceof EntityBubble) {
            return;
        }

        double depth = getDepth(pLevel, pPos, pEntity);
        if (depth > 0) {

            trySetCoverage(pEntity);

            // there are three main effects that happen every tick.

            // first, the quicksand's main effects are applied. Thickness and sinking.
            quicksandMomentum(pState, pLevel, pPos, pEntity, depth);



            // magic stuff to do fun effects ...
            entityQuicksandVar qsE = (entityQuicksandVar) pEntity;

            // TODO Move this to using a map instead of an array?
            // instead of just adding all the effects at the beginning, and blindly running all the effects the entity has,
            // should probably instead check for all the ones that Should be there, and run those
            // and add them if they don't exist
            for (QuicksandEffect e : qsE.getQuicksandEffectManager().effects) {
                e.effectEntity(pPos, pEntity, getQuicksandBehavior());
            }





            // Some movement stuff. Dealing with whether the entity is "OnGround"
            // whether they can jump, and step out onto a solid block.



            // Okay, so letting people jump on the top layer of the quicksand lets everyone just hold spacebar and jump over it all. I just commented it all out to disable it, and works better (tm)

            if (canStepOut(depth)) {

                boolean playerFlying = false;
                if (pEntity instanceof Player) {
                    Player p = (Player) pEntity;
                    playerFlying = p.getAbilities().flying;
                }

                if (!playerFlying) {
                    // if the player is flying ... they shouldn't be set on ground! cause then they
                    // can't fly.
                    // quicksand effects are still dealt, above, though.
                    pEntity.setOnGround(true);
                }

            } else {
                // sets to false even if they're at bottom (touching block underneath the QS)
                pEntity.setOnGround(false);
                pEntity.resetFallDistance();
            }

            pEntity.resetFallDistance();

        }

    }

    public void trySetCoverage(Entity pEntity) {

        // Set a section of coverage, bottom to the current depth value (as a percentage)
        // It's called "try" set coverage, but it does it every time, no matter what.
        // Ideally, some kind of check should be done so that it doesn't edit it if it doesn't need to.

        // ... though, that *could* be done in the PlayerCoverage class instead of here.

        if (pEntity instanceof Player) {

            playerStruggling pS = (playerStruggling) pEntity;

            PlayerCoverage pC = pS.getCoverage();

            double depth = getDepth(pEntity.level(), pEntity.blockPosition(), pEntity);
            double depth_percent = clamp(depth, 0.0, 2.0)/2.0;

            int begin = 0;
            int end = (int) (32 * depth_percent);
            end = clamp(end, 0, 32);

            ResourceLocation tex = new ResourceLocation(QuicksandRehydrated.MOD_ID, QSBehavior.getCoverageTex());
            pC.addCoverageEntry(new CoverageEntry(begin, end, tex));

        }
    }
    public void firstTouch(BlockPos pPos, Entity pEntity, Level pLevel) {
        trySetCoverage(pEntity);
        entityQuicksandVar es = (entityQuicksandVar) pEntity;

        QuicksandBehavior qb = getQuicksandBehavior();

        QuicksandEffectManager qEM = es.getQuicksandEffectManager();

        qEM.clear();

        System.out.print("Adding Quicksand Effects for ");
        System.out.print(getClass().getSimpleName());
        System.out.println(" ... ");
        for (Class<? extends QuicksandEffect> e : qb.effectsList) {
            System.out.print("  + ");
            System.out.println(e.getSimpleName());
            qEM.addEffect(e, pPos, pEntity);
        }

    }

    public void struggleAttempt(@NotNull BlockState pState, @NotNull Entity pEntity, double struggleAmount) {

        // runs when the player struggles in a block of this type.

        // particle should happen at surface in an area around.
        // Vec3 pos = pEntity.position();
        // pEntity.getLevel().addParticle(ModParticles.QUICKSAND_BUBBLE_PARTICLES.get(),pos.x,
        // pos.y, pos.z, 0, 0, 0);

        // struggleAmount should be 0 .. 1, from 0 to 20 ticks

        double middlePoint = -1 * abs(struggleAmount - 0.5) + 0.5;

        // curve it
        middlePoint = 5.25 * (middlePoint*middlePoint) - 0.15; // ranges -0.5 to 0.5

        pEntity.addDeltaMovement(new Vec3(0.0, middlePoint, 0.0));

        playStruggleSound(pEntity, struggleAmount);

    }

    public void playStruggleSound(Entity pEntity, double struggleAmount) {
        pEntity.level().playSound(pEntity, pEntity.blockPosition(), SoundEvents.SOUL_SOIL_STEP, SoundSource.BLOCKS, 0.25F, (pEntity.level().getRandom().nextFloat() * 0.1F) + 0.5F);
    }

    public void tryApplyCoverage(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Entity pEntity) {
//        double depth = getDepth(pLevel, pPos, pEntity);
//        if (depth > 0) {
//            if (pEntity instanceof Player) {
//
//                playerStruggling pS = (playerStruggling) pEntity;
//                double currentAmount = pS.getCoveragePercent();
//                double shouldBe = depth/1.875;
//                if (shouldBe > 1.0) {shouldBe = 1.0;}
//
//                if (shouldBe > currentAmount) {pS.setCoveragePercent(shouldBe);}
//
//            }
//        }
    }

    // special function for sinkables that runs when an entity jumps on, or in it.
    public void sinkableJumpOff(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
    }

    @Override
    public void entityInside(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos,
            @NotNull Entity pEntity) {

        // when an entity is touching a quicksand block ...

        double depth = getDepth(pLevel, pPos, pEntity);

        if (depth > 0) {

            // all that needs to be done is set the fact that this entity is in quicksand.
            // Entities choose which quicksand block to run the applyQuicksandEffects
            // function from.

            entityQuicksandVar es = (entityQuicksandVar) pEntity;

            // more movement variables.
            es.setInQuicksand(true);

            pEntity.resetFallDistance();

            tryApplyCoverage(pState, pLevel, pPos, pEntity);

        }

    }


    // @Override
    // public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos,
    // RandomSource pRandom) {}

    public void spawnBubble(BlockState pState, Level pLevel, Vec3 pos, BlockPos pPos, BlockState bs) {
        BlockState upOne = pLevel.getBlockState(pPos.above());
        if (checkDrownable(pState) && !checkDrownable(upOne)) {
            double offset = 0d;
            Block gb = pState.getBlock();
            if (gb instanceof QuicksandInterface) {
                offset = ((QuicksandInterface) gb).getOffset(pState);
            }
            pos = pos.add(new Vec3(0, -offset, 0));
            spawnBubble(pLevel, pos);
        }
    }

    public void spawnBubble(Level pLevel, Vec3 pos) {
        if (!pLevel.isClientSide()) {
            EntityBubble.spawn(pLevel, pos, Blocks.COAL_BLOCK.defaultBlockState());
        }
    }



    public boolean canBeReplaced(BlockState pState, Fluid pFluid) {
        System.out.println("no replace!");
        return false;
    }

    // This needs to be set for quicksand blocks that have Ambient Occlusion
    // small detail but important, IMO
    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return Shapes.block();
    }

    public boolean checkDrownable(BlockState pState) {
        return pState.getTags().toList().contains(QUICKSAND_DROWNABLE)
                || pState.getFluidState().getTags().toList().contains(QUICKSAND_DROWNABLE_FLUID);
    }



}
