package net.mokai.quicksandrehydrated.block.quicksands.core;
import net.minecraft.world.level.block.state.BlockState;

public interface QuicksandInterface {
    QuicksandBehavior getQuicksandBehavior();
    double getOffset(BlockState blockstate);

}
