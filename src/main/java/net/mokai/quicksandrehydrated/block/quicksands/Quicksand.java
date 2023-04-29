package net.mokai.quicksandrehydrated.block.quicksands;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mokai.quicksandrehydrated.block.QuicksandBase;

public class Quicksand extends QuicksandBase {

    public Quicksand(Properties pProperties) {super(pProperties);}

    private static final double height = 4;
    private static VoxelShape SHAPE = Block.box(0,0,0,16, height,16);

}
