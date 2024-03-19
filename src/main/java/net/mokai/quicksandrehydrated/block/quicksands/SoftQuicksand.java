package net.mokai.quicksandrehydrated.block.quicksands;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.mokai.quicksandrehydrated.block.FlowingQuicksandBase;

import static net.mokai.quicksandrehydrated.QuicksandRehydrated.MOD_ID;

public class SoftQuicksand extends FlowingQuicksandBase {

    public SoftQuicksand(Properties pProperties) {super(pProperties);}

    @Override
    public double[] walkSpeed() {
        return new double[]{1d, 0d, 1d, 1d, .1d};
    }

    @Override
    public double[] getSink() { return new double[]{1d, 1d, 1d, 1d, .1d}; }


    @Override
    public void KILL(LivingEntity pEntity) {
        pEntity.hurt(new DamageSource(MOD_ID + "_soft_quicksand"), 2);
    }
}
