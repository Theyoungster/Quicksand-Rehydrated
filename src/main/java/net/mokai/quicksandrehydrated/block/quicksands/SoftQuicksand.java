package net.mokai.quicksandrehydrated.block.quicksands;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mokai.quicksandrehydrated.block.FlowingQuicksandBase;

import static net.mokai.quicksandrehydrated.QuicksandRehydrated.MOD_ID;

public class SoftQuicksand extends FlowingQuicksandBase {

    public SoftQuicksand(Properties pProperties) {super(pProperties);}

    @Override
    public void KILL(LivingEntity pEntity) {
        pEntity.hurt(new DamageSource(MOD_ID + "_soft_quicksand"), 2);
    }

}
