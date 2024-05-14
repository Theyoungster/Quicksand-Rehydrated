package net.mokai.quicksandrehydrated.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.mokai.quicksandrehydrated.block.QuicksandBase;
import net.mokai.quicksandrehydrated.entity.entityQuicksandVar;
import net.mokai.quicksandrehydrated.registry.ModParticles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import static net.mokai.quicksandrehydrated.util.ModTags.Blocks.QUICKSAND_DROWNABLE;

import static net.minecraft.world.level.block.Blocks.AIR;
import static net.mokai.quicksandrehydrated.util.ModTags.Blocks.QUICKSAND_DROWNABLE;

@Mixin(Entity.class)
public abstract class SlowdownMixin implements entityQuicksandVar {

    @Shadow protected Vec3 stuckSpeedMultiplier;
    @Shadow protected boolean onGround;

    public boolean changed = false;
    public double horizontal = Double.MAX_VALUE;
    public double vertical = Double.MAX_VALUE;

    public Vec3 previousPosition = new Vec3(0.0, 0.0, 0.0);

    public Vec3 getPreviousPosition() {return this.previousPosition;}
    public void setPreviousPosition(Vec3 set) {this.previousPosition = set;}

    public boolean inQuicksand = false;


    public boolean getInQuicksand() {
        return this.inQuicksand;
    }
    public void setInQuicksand(boolean set) {
        this.inQuicksand = set;
    }



//    @Inject(method = "collide", at = @At("HEAD"))
//    private void collide(Vec3 pVec, CallbackInfoReturnable<Vec3> cir) {
//
//        Entity thisEntity = (Entity)(Object)this;
//        BlockState test = thisEntity.getFeetBlockState();
//
//        this.onGround = this.onGround || test.getBlock() instanceof QuicksandBase;
//
//    }


    public BlockPos getStuckBlock(Entity pEntity) {

        Level pLevel = pEntity.getLevel();

        Vec3 ePos = pEntity.position();
        Level eLevel = pEntity.getLevel();

        if (eLevel.getBlockState(pEntity.blockPosition()).getBlock() instanceof QuicksandBase) {
            return pEntity.blockPosition();
        }

        // offset into middle of current block.

        BlockPos blockPosOffset = new BlockPos(ePos.add(0.5, 0, 0.5));

        // there is bias towards the southwest block currently
        // Should eventually change to a more sophisticated approach
        // that accurately gets the closest quicksand block, instead.

        if (eLevel.getBlockState(blockPosOffset).getBlock() instanceof QuicksandBase) {
            return blockPosOffset;
        }
        else if (eLevel.getBlockState(blockPosOffset.north()).getBlock() instanceof QuicksandBase) {
            return  blockPosOffset.north();
        }
        else if (eLevel.getBlockState(blockPosOffset.west()).getBlock() instanceof QuicksandBase) {
            return  blockPosOffset.west();
        }
        else if (eLevel.getBlockState(blockPosOffset.north().west()).getBlock() instanceof QuicksandBase) {
            return blockPosOffset.north().west();
        }
        return null;

    }



    @Inject(method = "tick", at = @At("HEAD"))
    private void tickEnd(CallbackInfo ci) {

        Entity thisEntity = (Entity)(Object)this;
        entityQuicksandVar QuicksandVarEntity = (entityQuicksandVar)(Object)this;

        if (!QuicksandVarEntity.getInQuicksand()) {
            // if not in quicksand, move previous position directly to position
            QuicksandVarEntity.setPreviousPosition(thisEntity.getPosition(0));
        }
        else {

            // white effect == previous position variable
            //Vec3 PrevPos = QuicksandVarEntity.getPreviousPosition();
            //thisEntity.getLevel().addParticle(ModParticles.QUICKSAND_BUBBLE_PARTICLES.get(), PrevPos.x(), PrevPos.y(), PrevPos.z(), 0.0D, 0.0D, 0.0D);

            BlockPos bp = getStuckBlock(thisEntity);

            if (bp != null) {
                Level eLevel = thisEntity.getLevel();
                BlockState bs = eLevel.getBlockState(bp);

                QuicksandBase qs = (QuicksandBase) bs.getBlock();
                qs.applyQuicksandEffects(bs, eLevel, bp, thisEntity);

            }

        }

        QuicksandVarEntity.setInQuicksand(false);

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


    @Shadow protected boolean onGround;
    @Inject(method = "collide", at = @At("HEAD"))
    private void collide(Vec3 pVec, CallbackInfoReturnable<Vec3> cir) {

        Entity thisEntity = (Entity)(Object)this;
        BlockState test = thisEntity.getFeetBlockState();
        this.onGround = this.onGround || test.getTags().toList().contains(QUICKSAND_DROWNABLE);
        // Allows for step-up even while falling if your center is inside a Quicksand_Drownable.

    }

    @Inject(method = "move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V", at = @At("TAIL"))
    public void emperorsNewMove(MoverType mv, Vec3 spd, final CallbackInfo ci)
    {
        changed = false;
    }

}

