package net.mokai.quicksandrehydrated.mixins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.mokai.quicksandrehydrated.block.QuicksandBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class SlowdownMixin {

    @Shadow protected Vec3 stuckSpeedMultiplier;
    @Shadow protected boolean onGround;

    public boolean changed = false;
    public double horizontal = Double.MAX_VALUE;
    public double vertical = Double.MAX_VALUE;



    @Inject(method = "collide", at = @At("HEAD"))
    private void collide(Vec3 pVec, CallbackInfoReturnable<Vec3> cir) {

        Entity thisEntity = (Entity)(Object)this;
        BlockState test = thisEntity.getFeetBlockState();
        this.onGround = this.onGround || test.getBlock() instanceof QuicksandBase;

    }


    /**
     *
     * @param pState
     * @param spd
     * @author Mokai
     * @reason makeStuckInBlock simply overwrote stuckSpeedMultiplier, which lead to instances of directional priority; now, it only accepts the strongest slowdown applied that tick.
     */

    @Overwrite
    public void makeStuckInBlock(BlockState pState, Vec3 spd) {

        // It's not that easy.

        if (horizontal > spd.x() || horizontal > spd.y() || !changed) {

            if (!changed) { horizontal = spd.x(); vertical = spd.y(); }
            if (horizontal > spd.x()) { horizontal = spd.x(); }
            if (vertical > spd.y()) { vertical = spd.y(); }
            changed = true;
            stuckSpeedMultiplier = new Vec3(horizontal, vertical, horizontal);
        }
        Entity e = (Entity)(Object) this;
        e.resetFallDistance();

        // It's that easy.
    }


    @Inject(method = "move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V", at = @At("TAIL"))
    public void emperorsNewMove(MoverType mv, Vec3 spd, final CallbackInfo ci)
    {
        changed = false;
    }

}

