package net.mokai.quicksandrehydrated.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public interface playerStruggling {

    int struggleHold = 0;
    boolean holdingStruggle = false;

    double coveragePercent = 0.0;

    double getCoveragePercent();
    void setCoveragePercent(double set);

    String coverageTexture = null;

    String getCoverageTexture();
    void setCoverageTexture(String set);



    boolean getHoldingStruggle();
    void setHoldingStruggle(boolean set);

    int getStruggleHold();
    void setStruggleHold(int set);
    void addStruggleHold(int add);

    void attemptStruggle();

}
