package net.mokai.quicksandrehydrated.block.quicksands;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mokai.quicksandrehydrated.block.QuicksandBase;

public class SoftQuicksand extends QuicksandBase {

    public SoftQuicksand(Properties pProperties) {super(pProperties);}


    private static final VoxelShape SHAPE =
            Block.box(0, 0, 0, 16, 13, 16);

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE;
    }

    @Override
    public double getOffset() {
        return 0.1875d; // This value is subtracted from depth for substances that aren't full blocks.
    }




}
