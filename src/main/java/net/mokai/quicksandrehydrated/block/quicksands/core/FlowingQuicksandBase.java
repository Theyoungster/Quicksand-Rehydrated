package net.mokai.quicksandrehydrated.block.quicksands.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mokai.quicksandrehydrated.entity.EntityBubble;
import net.mokai.quicksandrehydrated.util.EasingHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

import static net.mokai.quicksandrehydrated.QuicksandRehydrated.MOD_ID;
import static net.mokai.quicksandrehydrated.util.ModTags.Blocks.QUICKSAND_DROWNABLE;
import static net.mokai.quicksandrehydrated.util.ModTags.Fluids.QUICKSAND_DROWNABLE_FLUID;

public class FlowingQuicksandBase extends FallingBlock implements QuicksandInterface{


    public FlowingQuicksandBase(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 2));
    }


    private final Random rng = new Random();


    // ----- OVERRIDE AND MODIFY THESE VALUES FOR YOUR QUICKSAND TYPE ----- //


    public double getOffset(BlockState blockstate) {
        int level = blockstate.getValue(LEVEL);
        return 0.25d * (4-level);
    }

    public double getBubblingChance() { return .75d; } // Does this substance bubble when you sink?

    public double getStruggleSensitivity() {return .01d;} //.05 is very sensitive, .2 is moderately sensitive, and .8+ is very un-sensitive.

    public double[] getSink() { return new double[]{.1d, .08d, .05d, .0d, .1d}; } // Sinking speed. Lower is slower.
    public double[] walkSpeed() { return new double[]{1d, .5d, .25d, .125d, 0d}; } // Horizontal movement speed (ignoring Gravity)
    public double[] gravity() { return new double[] {0d, 0d, .1d, .2d, .3d}; } // Gravity pulls you towards the center of a given mess block- potentially away from any ledges one could climb up on.
    public int[] jumpPercentage() { return new int[]{30, 20, 0, 0, 0}; } // Chance per tick to be able to jump.
    public int getSpread() {return 1;} // How far to search for holes to fill. Negative instead indicates how tall to pile before spreading out.

    public double getCustomDeathMessageOdds() { return .25; }

    public void KILL(LivingEntity pEntity) {
        pEntity.hurt(new DamageSource(MOD_ID + "_quicksand"), 4);
    }




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

    public double getDepth(Level pLevel, BlockPos pPos, Entity pEntity, BlockState pState) { return EasingHandler.getDepth(pEntity, pLevel, pPos, getOffset(pState)); }

    public double getSink(double depth) { return getSink()[toInt(depth)] ; }
    public double getWalk(double depth) { return walkSpeed()[toInt(depth)]; }
    public boolean getJump(double depth, Level pLevel) {
        rng.setSeed(pLevel.getGameTime());
        float p = rng.nextInt(100);
        return p < jumpPercentage()[toInt(depth)];
    }




    @SuppressWarnings("deprecation")
    @Override
    public void entityInside(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Entity pEntity) {

        if (pEntity instanceof EntityBubble) {return;}

        //if (pEntity instanceof ItemEntity) { pEntity.makeStuckInBlock(pState, new Vec3(.1, .02, .1)); return; }  /// This is an ugly hack; items are jittering in quicksand, but skipping all of the code works fine.

        double depth = getDepth(pLevel, pPos, pEntity, pState);

        if (pEntity instanceof Player) {
            if (((Player) pEntity).isCreative() && !((Player) pEntity).isAffectedByFluids()) {
                return;
            }
        }
        double mod = 1;
        if (depth > 0) {



            ////// SINKIN' CODE HERE //////
            pEntity.setSprinting(false);
            if (!(pEntity instanceof Player)) {
                pEntity.setOnGround(true);
                mod = 1.62 / pEntity.getEyeHeight(); // 1.62 is the player's eye height; this gets a multiplier so shorter entities sink slower.
                depth = depth * mod;
            }

            boolean canJump = getJump(depth, pLevel);
            double sink = getSink(depth);
            double walk = getWalk(depth);
            if (canJump) { pEntity.setOnGround(true); } // Semi-randomly set the player on 'land'. Above depth .6, this means the player can step back onto land.
            if(pPos.getX() != pEntity.getBlockX() || pPos.getZ() != pEntity.getBlockZ()) // If the player is NOT fully inside this Quicksand block...
            { walk = Math.max(walk, .0625); }// ... Cap the speed reduction.
            else {
                // Bubble code!
                if(getBubblingChance()>0) {
                    if (Math.random() > getBubblingChance()) {
                        Vec3 pos = new Vec3(pEntity.getX() + Math.random(), pPos.getY() + .5, pEntity.getZ() + Math.random());
                        BlockPos np = new BlockPos(pos);
                        spawnBubble(pLevel.getBlockState(np), pLevel, pos, np, this.defaultBlockState());
                    }
                }
                // End bubble code
            }



            /**
             * Things that might slurp the player down faster:
             * - Heavy armor
             * - struggling
             * - Sinking potion effect
             */

            if(pLevel.isClientSide()) { // This bit is broken. We need to have the player themselves calculate their rotation / struggle speed, using the AirControlMixin to add the function, and then later call it.
                double test = Math.abs(pEntity.getViewVector(0).x - pEntity.getViewVector(1).x);
                if (test > getStruggleSensitivity()) {
                    sink = sink + 1.1111111f;
                }
            }
            pEntity.makeStuckInBlock(pState, new Vec3(walk, sink, walk)); /// Lower values are slower.


            ////// SINKIN' CODE END


        }
        // Kill code here.
        if (pEntity instanceof LivingEntity) {
            LivingEntity le = (LivingEntity) pEntity;
            if (pEntity.getAirSupply() <= -20 && le.getHealth() <= 2) {
                deathMessage(pLevel, le);
            }
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



    // ------------------------------- Flowing specific ------------------------------------



    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        pLevel.scheduleTick(pPos, this, this.getDelayAfterPlace());
    }

    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 1, 4);

    protected static final VoxelShape[] SHAPE_BY_LEVEL = new VoxelShape[]{
            Shapes.empty(),
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(0, 0, 0, 16, 8, 16),
            Block.box(0, 0, 0, 16, 12, 16),
            Block.box(0, 0, 0, 16, 16, 16)
    };


    // VANILLA bounding box stuff

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BY_LEVEL[pState.getValue(LEVEL)];
    }

    public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return SHAPE_BY_LEVEL[pState.getValue(LEVEL)];
    }

    public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BY_LEVEL[pState.getValue(LEVEL)];
    }

    // VANILLA placement stuff

    public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {

        int current_level = (Integer)pState.getValue(LEVEL);

        // can only add onto if holding qs, and block isn't full
        if (pUseContext.getItemInHand().is(this.asItem()) && current_level < 4) {

            if (pUseContext.replacingClickedOnBlock()) {
                return pUseContext.getClickedFace() != Direction.DOWN;
            } else {
                return true;
            }

        }

        return false;

    }

    // VANILLA blocktick stuff

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {

        BlockState current_block = pContext.getLevel().getBlockState(pContext.getClickedPos());

        if (current_block.is(this)) {

            int current_level = (Integer)current_block.getValue(LEVEL);

            if (current_level < 4) {
                return (BlockState)current_block.setValue(LEVEL, current_level + 1);
            }
            else {
                return current_block;
            }

        } else {
            return super.getStateForPlacement(pContext);
        }

    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LEVEL);
    }


    // VANILLA falling block stuff

    public void onBrokenAfterFall(Level pLevel, BlockPos pPos, FallingBlockEntity pFallingBlock) {

        BlockState fellBlockState = pLevel.getBlockState(pPos);
        BlockState selfBlockState = pFallingBlock.getBlockState();

        if (fellBlockState.canBeReplaced()) {
            pLevel.setBlock(pPos, selfBlockState, 3);
        }
        else if (fellBlockState.getBlock() == selfBlockState.getBlock()) {

            // first we get how much quicksand is in the falling block, and the block it fell onto
            int selfLevel = selfBlockState.getValue(LEVEL);
            int fellLevel = fellBlockState.getValue(LEVEL);

            // we get the total height
            int newTotalLevel = fellLevel + selfLevel;

            // but it needs to be limited to 4
            int newFellLevel = Math.min(newTotalLevel, 4);
            pLevel.setBlock(pPos, fellBlockState.setValue(LEVEL, newFellLevel), 3);

            // if there's overflow ...
            if (newFellLevel == 4 && newTotalLevel > 4) {

                // math?
                int extraLevel = newTotalLevel - 4;
                BlockState nextBlockUp = pLevel.getBlockState(pPos.above());

                if (nextBlockUp.canBeReplaced()) {
                    pLevel.setBlock(pPos.above(), selfBlockState.setValue(LEVEL, extraLevel), 3);
                }

            }

        } else {
            pLevel.setBlock(pPos.above(), selfBlockState.setValue(LEVEL, selfBlockState.getValue(LEVEL)), 3);
        }

    }



    // spready flow

    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {

        BlockState belowState = pLevel.getBlockState(pPos.below());

        boolean flag_replaceable = belowState.canBeReplaced();
        boolean flag_combinable_quicksand = (belowState.getBlock() == pState.getBlock() && belowState.getValue(LEVEL) < 4);

        if (flag_replaceable || flag_combinable_quicksand) {
            FallingBlockEntity fallingBlock = FallingBlockEntity.fall(pLevel, pPos, pState);
            this.falling(fallingBlock);
        }
        else {
            pLevel.scheduleTick(pPos, pState.getBlock(), 10);
        }

    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (isFree(pLevel.getBlockState(pPos.below())) && pPos.getY() >= pLevel.getMinBuildHeight()) {
            FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(pLevel, pPos, pState);
            this.falling(fallingblockentity);
        } else {
            spreadTick(pLevel, pPos, pRandom);
        }
    }

    public int getDepth(ServerLevel pLevel, BlockPos pos, Block ourType) {
        BlockState bsA = pLevel.getBlockState(pos);
        BlockState bsB = pLevel.getBlockState(pos.below());
        if (bsA.isAir()) {
            if (bsB.getBlock() == ourType) {
                return bsB.getValue(LEVEL)-8;
            } else {
                if (bsB.isAir()) {
                    return -8;
                } else {
                    return -4;
                }
            }
        } else {
            if (bsA.getBlock() == ourType) {
                return bsA.getValue(LEVEL)-4;
            } else {
                return 0;
            }
        }
    }



    public void spreadTick(ServerLevel pLevel, BlockPos pPos, RandomSource rand) {
        if (pLevel.getBlockState(pPos.above()).getBlock() != this.asBlock()) { // Make sure we're the topmost block of a pile.

            BlockState current_block = pLevel.getBlockState(pPos);
            int current_level = current_block.getValue(LEVEL);
            List<BlockPos> check = new ArrayList<>(
                    Arrays.asList(pPos.north(), pPos.east(), pPos.south(), pPos.west())
            );
            Collections.shuffle(check);


            int bestdepth = 0;
            BlockPos finalPosition = BlockPos.ZERO; // Yall better make sure this gets changed during this code or WE'RE GONNA HAVE A PROBLEM


            for (BlockPos position : check) {
                int depth = getDepth(pLevel, position, current_block.getBlock());
                if (depth < bestdepth) {
                    bestdepth = depth;
                    finalPosition = position;
                }
            }


            if ( bestdepth != 0 && (bestdepth < current_level-5)) {   // Here is where we actually move the blocks from point A to point B.
                finalPosition = (bestdepth < -4) ? finalPosition.below() : finalPosition;

                BlockState finalBS = pLevel.getBlockState(finalPosition);

                if (current_level == 1) {
                    pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
                } else {
                    pLevel.setBlock(pPos, current_block.setValue(LEVEL, current_level-1), 3);
                }

                pLevel.setBlock(finalPosition, current_block.setValue(
                        LEVEL,
                        finalBS.isAir() ? 1 : finalBS.getValue(LEVEL)+1), 3);



            }
        } else { // We're not on top of the pile. Tick up!
            pLevel.scheduleTick(pPos.above(),this.asBlock(), 1);
        }

    }

}
