package net.mokai.quicksandrehydrated.mixins;

import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.mokai.quicksandrehydrated.util.ModTags.Blocks.QUICKSAND_DROWNABLE;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin {

    @Shadow private boolean cancelDrop;
    @Shadow BlockState blockState;


    @Inject(method = "tick()V", at = @At("HEAD"))
    public void tick(CallbackInfo ci)
    {
        //System.out.println(this.blockState.getTags().toList());
        if (this.blockState.getTags().toList().contains(QUICKSAND_DROWNABLE)) {
            this.cancelDrop = true; // setting cancelDrop will, later in the function, cause the item dropping to be skipped, and the onBrokenAfterFall method will fire instead without dropping an item.
        }
    }

}
