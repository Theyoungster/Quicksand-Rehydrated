package net.mokai.quicksandrehydrated.fluid.quicksands.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.mokai.quicksandrehydrated.fluid.FluidQuicksandBase;
import org.jetbrains.annotations.NotNull;


public class DryQuicksandBlock extends FluidQuicksandBase {

    public static class Flowing extends ForgeFlowingFluid.Flowing {

        public Flowing(Properties properties) {
                super(properties);
            }

        @Override
        public int getSlopeFindDistance(LevelReader worldIn) {
            return 1;
        }

        @Override
        public int getDropOff(LevelReader worldIn) {
            return 7;
        }
    }

    public static class Source extends ForgeFlowingFluid.Source {

        public Source(Properties properties) {
                super(properties);
            }

        @Override
        public int getSlopeFindDistance(LevelReader worldIn) {
            return 1;
        }

        @Override
        public int getDropOff(LevelReader worldIn) {
            return 7;
        }

    }

    VoxelShape[] shapes = new VoxelShape[16];

    public DryQuicksandBlock(java.util.function.Supplier<? extends FlowingFluid> supplier, Block.Properties props) {
        super(supplier, props);
        int max = 15; //max of the property LEVEL.getAllowedValues()
        float offset = 0F;
        for (int i = 0; i <= max; i++) { //x and z go from [0,1]
            shapes[i] = Shapes.create(new AABB(0, 0, 0, 1, offset - i / 8F, 1)); // Inherited from existing code; potentially skip this.
        }
    }


    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getOcclusionShape(BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos) {
        return shapes[state.getValue(LEVEL)];
    }




    public double getOffset() {
        return 0d; // This value is subtracted from depth for substances that aren't full blocks.
    }


}
