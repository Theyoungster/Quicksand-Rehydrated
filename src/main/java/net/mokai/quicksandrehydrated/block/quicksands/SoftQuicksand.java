package net.mokai.quicksandrehydrated.block.quicksands;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mokai.quicksandrehydrated.block.QuicksandBase;

import static net.mokai.quicksandrehydrated.QuicksandRehydrated.MOD_ID;

public class SoftQuicksand extends QuicksandBase {

    public SoftQuicksand(Properties pProperties) {super(pProperties);}

    @Override
    public double getOffset() {return 0.1875d;}

    @Override
    public double[] walkSpeed() {
        return new double[]{0d, 0d, 0d, 0d, 0d};
    }

    @Override
    public double[] getSink() { return new double[]{1d, 1d, 1d, 1d, .1d}; }




    private static final VoxelShape SHAPE =
            Block.box(0, 0, 0, 16, 13, 16);

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE;
    }

    /*
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pFacing == Direction.UP) return pState.setValue(SNOWY, Boolean.valueOf(isSnowySetting(pFacingState)));
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }
*/


    @Override
    public void KILL(LivingEntity pEntity) {
        pEntity.hurt(new DamageSource(MOD_ID + "_soft_quicksand"), 2);
    }
}
