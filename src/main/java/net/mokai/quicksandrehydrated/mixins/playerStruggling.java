package net.mokai.quicksandrehydrated.mixins;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface playerStruggling {

    int struggleCooldown = 0;

    int getStruggleCooldown();
    void setStruggleCooldown(int set);
    void addStruggleCooldown(int add);

    void attemptStruggle();

}
