package net.mokai.quicksandrehydrated.fluid;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.mokai.quicksandrehydrated.util.EasingHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.Consumer;
import java.util.function.Supplier;


public class FluidQuicksandBase extends LiquidBlock {

    public FluidQuicksandBase(Supplier<? extends FlowingFluid> pFluid, Properties pProperties) {
        super(pFluid, pProperties);
    }

    public double getOffset() { return 0d; } // This value is subtracted from depth for substances that aren't full blocks.

    public boolean getBubbling() { return true; }


    public double getDepth(Level pLevel, BlockPos pPos, Entity pEntity) {
        return EasingHandler.getDepth(pEntity, pLevel, pPos, getOffset());
    }


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

    public double getSink(double depth, double height) { return getSink()[toInt(depth)] * height; }

    public double getWalk(double depth) { return walkSpeed()[toInt(depth)]; }

    public boolean getJump(double depth) { return Math.random() < jumpPercentage()[toInt(depth)]; }

    public double[] getSink() { return new double[]{1d, .3d, .1d, .04d, .01d}; }
    public double[] walkSpeed() { return new double[]{0d, .5d, .25d, .125d, 0d}; }
    public double[] jumpPercentage() { return new double[]{.1d, .25d, .125d, 0d, 0d}; }







    @SuppressWarnings("deprecation")
    @Override
    public void entityInside(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Entity pEntity) {

        double depth = getDepth(pLevel, pPos, pEntity);
        double mult = 1;
        if (depth >0) {
            pEntity.sendSystemMessage(Component.literal("Depth: " + depth + "  " + pPos.toShortString()));
            pEntity.setSprinting(false);

            if (!(pEntity instanceof Player)) {
                pEntity.setOnGround(true);
                mult = .2;
            }

            //SINKIN' CODE HERE//
            boolean move = getJump(depth);
            if (move) {
                pEntity.setOnGround(true); // This allows the player to step up above a depth of .6
                pEntity.makeStuckInBlock(pState, new Vec3(getWalk(depth), getSink(depth, mult), getWalk(depth))); /// Lower values are slower.
            } else {
                pEntity.makeStuckInBlock(pState, new Vec3(getWalk(depth), getSink(depth, mult), getWalk(depth))); /// Lower values are slower.
            }


        }

    }



}
