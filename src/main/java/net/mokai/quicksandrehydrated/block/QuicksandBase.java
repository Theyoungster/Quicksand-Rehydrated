package net.mokai.quicksandrehydrated.block;


import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidType;
import net.mokai.quicksandrehydrated.util.EasingHandler;


public class QuicksandBase extends Block {

    public boolean bubbling = true;



    public QuicksandBase(Properties pProperties) {
        super(pProperties);
    }

    public double getSinkingSpeed(double depth) {

        return EasingHandler.ease_pow(0d, 1d, depth,1);

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

            pEntity.makeStuckInBlock(pState, new Vec3(depth, .1, depth));

            if (pEntity instanceof Player) {
                pEntity.setSprinting(false);
            }
        } else if (depth > .5) {

        }

    }

}
