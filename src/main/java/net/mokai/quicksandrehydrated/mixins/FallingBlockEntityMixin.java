package net.mokai.quicksandrehydrated.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.mokai.quicksandrehydrated.util.ModTags.Blocks.QUICKSAND_DROWNABLE;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin {

    @Shadow
    private boolean cancelDrop;
    @Shadow
    BlockState blockState;

    @Shadow
    public abstract void disableDrop();

    @Shadow
    public abstract BlockState getBlockState();

    @Shadow
    public abstract void callOnBrokenAfterFall(Block p_149651_, BlockPos p_149652_);

    @Inject(method = "tick()V", at = @At("HEAD"))
    public void tick(CallbackInfo ci)
    {
        if (this.blockState.getTags().toList().contains(QUICKSAND_DROWNABLE)) {
            this.disableDrop();
            // setting cancelDrop will, later in the function, cause the item dropping to be skipped.
            // the onBrokenAfterFall method will fire instead, without dropping an item.

            FallingBlockEntity fbe = (FallingBlockEntity) (Object) this;
            BlockPos currentPosition = new BlockPos(new Vec3i(fbe.getBlockX(),fbe.getBlockY(),fbe.getBlockZ()));
            BlockState currentOverlap = fbe.level().getBlockState(currentPosition);
            if (currentOverlap.getTags().toList().contains(QUICKSAND_DROWNABLE)) {
                this.callOnBrokenAfterFall(getBlockState().getBlock(), currentPosition);
                fbe.discard();
            }
        }

    }

}
