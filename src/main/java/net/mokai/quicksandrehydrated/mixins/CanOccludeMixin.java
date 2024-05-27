package net.mokai.quicksandrehydrated.mixins;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.mokai.quicksandrehydrated.block.QuicksandBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mokai.quicksandrehydrated.util.ModTags.Blocks.QUICKSAND_AO_OVERRIDE;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class CanOccludeMixin {

    @Inject(method = "canOcclude", at = @At("HEAD"), cancellable = true)
    private void onCanOcclude(CallbackInfoReturnable<Boolean> cir) {

        BlockBehaviour.BlockStateBase bsb = (BlockBehaviour.BlockStateBase)(Object) this;

        if (bsb.is(QUICKSAND_AO_OVERRIDE)) {
            cir.setReturnValue(true);
        }

    }


    // IMPORTANT
    @Inject(method = "isCollisionShapeFullBlock", at = @At("HEAD"), cancellable = true)
    private void onIsCollisionShapeFullBlock(CallbackInfoReturnable<Boolean> cir) {

        BlockBehaviour.BlockStateBase bsb = (BlockBehaviour.BlockStateBase)(Object) this;
        if (bsb.is(QUICKSAND_AO_OVERRIDE)) {
            cir.setReturnValue(true);
        }

    }



    @Inject(method = "useShapeForLightOcclusion", at = @At("HEAD"), cancellable = true)
    private void onUseShapeForLightOcclusion(CallbackInfoReturnable<Boolean> cir) {

        BlockBehaviour.BlockStateBase bsb = (BlockBehaviour.BlockStateBase)(Object) this;
        if (bsb.is(QUICKSAND_AO_OVERRIDE)) {
            cir.setReturnValue(true);
        }

    }

}
