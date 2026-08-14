package net.mokai.quicksandrehydrated.block.quicksands.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.*;

public class FlowingQuicksandBase extends QuicksandBase implements QuicksandInterface, Fallable {

    public FlowingQuicksandBase(Properties pProperties, QuicksandBehavior QSB) {
        super(pProperties, QSB);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 1));
    }

    @Override
    public double getOffset(BlockState state) {
        if (state == null) return 0.0;
        if (state.getBlock() != this) return 0.0;
        if (!state.hasProperty(LEVEL)) return 0.0;
        return (1.0 - (state.getValue(LEVEL) / 4.0)) + QSBehavior.getOffset();
    }

    public double getCoverageFraction(BlockState state) {
        if (state == null) return 0.0;
        if (state.getBlock() != this) return 0.0;
        if (!state.hasProperty(LEVEL)) return 0.0;
        return state.getValue(LEVEL) / 4.0;
    }

    public double coverageAt(Level level, double x, double feetY, double z) {
        if (level == null) return 0.0;

        BlockPos feetPos = BlockPos.containing(x, feetY, z);
        BlockState here = level.getBlockState(feetPos);
        BlockPos samplePos = null;

        if (here.getBlock() == this && here.hasProperty(LEVEL)) {
            samplePos = feetPos;
        } else if (here.isAir()) {
            BlockState below = level.getBlockState(feetPos.below());
            if (below.getBlock() == this && below.hasProperty(LEVEL)) {
                samplePos = feetPos.below();
            }
        }
        if (samplePos == null) return 0.0;

        BlockState s = level.getBlockState(samplePos);
        int lvl = s.getValue(LEVEL);
        double frac = lvl / 4.0;
        double surfaceY = samplePos.getY() + frac;

        double localX = x - samplePos.getX();
        double localZ = z - samplePos.getZ();
        final double MARGIN = 0.10;
        if (localX < MARGIN || localX > 1.0 - MARGIN || localZ < MARGIN || localZ > 1.0 - MARGIN) {
            return 0.0;
        }

        final double EPS = 0.02;
        double depth = surfaceY - feetY;
        if (depth <= EPS) return 0.0;

        double norm = Math.max(0.5, frac);
        double cov = Math.min(1.0, Math.max(0.0, depth / norm));
        return cov;
    }

    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 1, 4);

    protected static final VoxelShape[] SHAPE_BY_LEVEL = new VoxelShape[]{
            Shapes.empty(),
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(0, 0, 0, 16, 8, 16),
            Block.box(0, 0, 0, 16, 12, 16),
            Block.box(0, 0, 0, 16, 16, 16)
    };

    // Yeah I'm a bit surprised at this one myself tbh
    protected static final float[] SHADE_BRIGHTNESS_BY_LEVEL = new float[]{1.0f, 1.0f, 1.0f, 1.0f, 0.2f};

    @Override
    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return SHAPE_BY_LEVEL[pState.getValue(LEVEL)];
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BY_LEVEL[pState.getValue(LEVEL)];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return SHAPE_BY_LEVEL[pState.getValue(LEVEL)];
    }

    @Override
    public float getShadeBrightness(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return SHADE_BRIGHTNESS_BY_LEVEL[pState.getValue(LEVEL)];
    }

    @Override
    public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
        int current_level = pState.getValue(LEVEL);
        if (pUseContext.getItemInHand().is(this.asItem()) && current_level < 4) {
            if (pUseContext.replacingClickedOnBlock()) {
                return pUseContext.getClickedFace() != Direction.DOWN;
            } else {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState current_block = pContext.getLevel().getBlockState(pContext.getClickedPos());
        if (current_block.is(this)) {
            int current_level = current_block.getValue(LEVEL);
            if (current_level < 4) {
                return current_block.setValue(LEVEL, current_level + 1);
            } else {
                return current_block;
            }
        } else {
            return super.getStateForPlacement(pContext);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(LEVEL);
    }

    @Override
    public void onBrokenAfterFall(Level pLevel, BlockPos pPos, FallingBlockEntity pFallingBlock) {
        BlockState fell = pLevel.getBlockState(pPos);
        BlockState self = pFallingBlock.getBlockState();

        if (fell.canBeReplaced()) {
            pLevel.setBlock(pPos, self, 3);
        }
        else if (fell.getBlock() == self.getBlock()) {
            int selfLevel = self.getValue(LEVEL);
            int fellLevel = fell.getValue(LEVEL);
            int total = fellLevel + selfLevel;

            int newHere = Math.min(total, 4);
            pLevel.setBlock(pPos, fell.setValue(LEVEL, newHere), 3);

            if (newHere == 4 && total > 4) {
                int extra = total - 4;
                BlockPos up = pPos.above();
                BlockState upState = pLevel.getBlockState(up);
                if (upState.canBeReplaced()) {
                    pLevel.setBlock(up, self.setValue(LEVEL, extra), 3);
                }
            }
        } else {
            pLevel.setBlock(pPos.above(), self.setValue(LEVEL, self.getValue(LEVEL)), 3);
        }
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        BlockState below = pLevel.getBlockState(pPos.below());

        boolean canReplace = below.canBeReplaced();
        boolean canMerge = (below.getBlock() == pState.getBlock()
                && below.hasProperty(LEVEL)
                && below.getValue(LEVEL) < 4);

        if (canReplace || canMerge) {
            FallingBlockEntity falling = FallingBlockEntity.fall(pLevel, pPos, pState);
            this.falling(falling);
        } else {
            pLevel.scheduleTick(pPos, pState.getBlock(), 10);
        }
    }

    public int getDepth(ServerLevel pLevel, BlockPos pos, Block ourType) {
        BlockState a = pLevel.getBlockState(pos);
        BlockState b = pLevel.getBlockState(pos.below());
        if (a.isAir()) {
            if (b.getBlock() == ourType) {
                return b.getValue(LEVEL) - 8;
            } else {
                if (b.isAir()) return -8;
                return -4;
            }
        } else {
            if (a.getBlock() == ourType) return a.getValue(LEVEL) - 4;
            return 0;
        }
    }

    public void spreadTick(ServerLevel pLevel, BlockPos pPos, RandomSource rand) {
        BlockState below = pLevel.getBlockState(pPos.below());
        if (pLevel.getBlockState(pPos.below()).getBlock() == this.asBlock()) {
            if (below.getValue(LEVEL) < 4){
                below.setValue(LEVEL, below.getValue(LEVEL) + 1);
                BlockState current = pLevel.getBlockState(pPos);
                int currentLevel = current.getValue(LEVEL);
                if (currentLevel == 1) {
                    pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
                } else {
                    current.setValue(LEVEL, current.getValue(LEVEL) - 1);
                }
                pLevel.setBlock(pPos.below(), current.setValue(
                        LEVEL,
                        below.isAir() ? 1 : below.getValue(LEVEL) + 1), 3);
                return;
            }
        }

        if (pLevel.getBlockState(pPos.above()).getBlock() != this.asBlock()) {
            BlockState self = pLevel.getBlockState(pPos);
            int selfLevel = self.getValue(LEVEL);
            List<BlockPos> check = Arrays.asList(pPos.north(), pPos.east(), pPos.south(), pPos.west());
            Collections.shuffle(check);

            int bestDepth = 0;
            BlockPos bestPos = BlockPos.ZERO;

            for (BlockPos candidate : check) {
                int d = getDepth(pLevel, candidate, self.getBlock());
                if (d < bestDepth) {
                    bestDepth = d;
                    bestPos = candidate;
                }
            }

            if (bestDepth != 0 && (bestDepth < selfLevel - 5)) {
                BlockPos dst = (bestDepth < -4) ? bestPos.below() : bestPos;

                BlockState dstState = pLevel.getBlockState(dst);

                if (selfLevel == 1) {
                    pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
                } else {
                    pLevel.setBlock(pPos, self.setValue(LEVEL, selfLevel - 1), 3);
                }

                pLevel.setBlock(dst, self.setValue(
                        LEVEL,
                        dstState.isAir() ? 1 : dstState.getValue(LEVEL) + 1), 3);
            }
        } else {
            pLevel.scheduleTick(pPos.above(), this.asBlock(), 1);
        }
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        pLevel.scheduleTick(pPos, this, this.getDelayAfterPlace());
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        pLevel.scheduleTick(pCurrentPos, this, this.getDelayAfterPlace());
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (isFree(pLevel.getBlockState(pPos.below())) && pPos.getY() >= pLevel.getMinBuildHeight()) {
            FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(pLevel, pPos, pState);
            this.falling(fallingblockentity);
        } else {
            spreadTick(pLevel, pPos, pRandom);
        }
    }

    protected void falling(FallingBlockEntity pEntity) {}

    protected int getDelayAfterPlace() { return 2; }

    public static boolean isFree(BlockState pState) {
        return pState.isAir() || pState.is(BlockTags.FIRE) || pState.liquid() || pState.canBeReplaced();
    }

    @Override
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
