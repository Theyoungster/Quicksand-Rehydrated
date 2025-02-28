package net.mokai.quicksandrehydrated.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.mokai.quicksandrehydrated.entity.coverage.PlayerCoverage;

import java.util.UUID;

public interface playerStruggling {

    int struggleHold = 0;
    boolean holdingStruggle = false;

    PlayerCoverage coveragePercent = null;

    PlayerCoverage getCoverage();

//    void setCoveragePercent(double set);

//    String coverageTexture = null;

//    String getCoverageTexture();
//    void setCoverageTexture(String set);



    boolean getHoldingStruggle();
    void setHoldingStruggle(boolean set);

    int getStruggleHold();
    void setStruggleHold(int set);
    void addStruggleHold(int add);

    void attemptStruggle();

    Vec3 velPosition0 = new Vec3(0, 0, 0);
    Vec3 getVelPos0();
    void setVelPos0(Vec3 set);

    Vec3 velPosition1 = new Vec3(0, 0, 0);
    Vec3 getVelPos1();
    void setVelPos1(Vec3 set);


    Vec3 getVelocity();


}
