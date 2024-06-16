package net.mokai.quicksandrehydrated.block.quicksands.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
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

import static net.mokai.quicksandrehydrated.util.ModTags.Blocks.QUICKSAND_DROWNABLE;
import static net.mokai.quicksandrehydrated.util.ModTags.Fluids.QUICKSAND_DROWNABLE_FLUID;

public class FlowingQuicksandBase extends QuicksandBase implements QuicksandInterface, Fallable {

    private final Random rng = new Random();

    public FlowingQuicksandBase(Properties pProperties, QuicksandBehavior QSB) {super(pProperties, QSB);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 2));
    }



    // ------------------------------- Flowing specific ------------------------------------


    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 1, 4);

    protected static final VoxelShape[] SHAPE_BY_LEVEL = new VoxelShape[]{
            Shapes.empty(),
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(0, 0, 0, 16, 8, 16),
            Block.box(0, 0, 0, 16, 12, 16),
            Block.box(0, 0, 0, 16, 16, 16)
    };

    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        int level = pState.getValue(LEVEL);
        return SHAPE_BY_LEVEL[level];
    }

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

    // Vanilla blocktick stuff

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




    // ------------------------------------------- THIS IS FOR FALLINGBLOCK IMPLEMENTATION ----------------------


    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        pLevel.scheduleTick(pPos, this, this.getDelayAfterPlace());
    }

    /**
     * Update the provided state given the provided neighbor direction and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific direction passed in.
     */
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        pLevel.scheduleTick(pCurrentPos, this, this.getDelayAfterPlace());
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (isFree(pLevel.getBlockState(pPos.below())) && pPos.getY() >= pLevel.getMinBuildHeight()) {
            FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(pLevel, pPos, pState);
            this.falling(fallingblockentity);
        } else {
            spreadTick(pLevel, pPos, pRandom);
        }
    }

    protected void falling(FallingBlockEntity pEntity) {
    }

    protected int getDelayAfterPlace() {
        return 2;
    }

    public static boolean isFree(BlockState pState) {
        return pState.isAir() || pState.is(BlockTags.FIRE) || pState.liquid() || pState.canBeReplaced();
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles).
     */
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pRandom.nextInt(16) == 0) {
            BlockPos blockpos = pPos.below();
            if (isFree(pLevel.getBlockState(blockpos))) {
                ParticleUtils.spawnParticleBelow(pLevel, pPos, pRandom, new BlockParticleOption(ParticleTypes.FALLING_DUST, pState));
            }
        }

    }

    public int getDustColor(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return -16777216;
    }

}
