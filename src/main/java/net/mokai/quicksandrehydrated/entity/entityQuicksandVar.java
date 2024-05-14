package net.mokai.quicksandrehydrated.entity;

import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public interface entityQuicksandVar {



    Vec3 previousPosition = new Vec3(0.0, 0.0, 0.0);

    Vec3 getPreviousPosition();
    void setPreviousPosition(Vec3 set);



    boolean inQuicksand = false;
    boolean getInQuicksand();
    void setInQuicksand(boolean set);

    BlockPos getStuckBlock(Entity pEntity);

}
