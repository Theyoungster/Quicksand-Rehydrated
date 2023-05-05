package net.mokai.quicksandrehydrated.fluid;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.mokai.quicksandrehydrated.block.QuicksandBase;


public class DryQuicksandBlock extends LiquidBlock {

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

    VoxelShape shapes[] = new VoxelShape[16];

    public DryQuicksandBlock(java.util.function.Supplier<? extends FlowingFluid> supplier, Block.Properties props) {
        super(supplier, props);
        int max = 15; //max of the property LEVEL.getAllowedValues()
        float offset = 0F;
        for (int i = 0; i <= max; i++) { //x and z go from [0,1]
            shapes[i] = Shapes.create(new AABB(0, 0, 0, 1, offset - i / 8F, 1)); // Inherited from existing code; potentially skip this.
        }
    }

    @Override
    @Deprecated
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return shapes[state.getValue(LEVEL).intValue()];
    }

    @Override
    @Deprecated
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return shapes[state.getValue(LEVEL).intValue()];
    }





    public double getOffset() {
        return 0d; // This value is subtracted from depth for substances that aren't full blocks.
    }

    public double getMaxWalkingSpeed(double depth) {
        return depth * .1;
    }


    public double getDepth(Entity pEntity, Level pLevel, BlockPos pPos) {
        double playerY = pEntity.getPosition(1).y();
        double depth;
        BlockPos playercube;
        double currentHeight = pPos.getY();
        do {
            currentHeight++;
            playercube = new BlockPos(pPos.getX(), currentHeight, pPos.getY());
        } while (pLevel.getBlockState(playercube).getBlock() instanceof QuicksandBase);

        depth  = playercube.getY() - playerY - getOffset();

        return depth;
    }



    @SuppressWarnings("deprecation")
    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {

        double depth = getDepth(pEntity, pLevel, pPos);


        if (depth >0 && pEntity instanceof Player) {
            pEntity.setSprinting(false);
        }


        /////BEGIN SINKING CODE///
        if (depth > 0) {

            //pEntity.makeStuckInBlock(pState, new Vec3(depth, .1, depth));

            if (pEntity instanceof Player) {
                pEntity.setSprinting(false);
            }
        } else if (depth > .5) {

        }

    }
}
