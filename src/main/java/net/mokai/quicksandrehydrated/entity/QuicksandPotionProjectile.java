package net.mokai.quicksandrehydrated.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.mokai.quicksandrehydrated.recipe.SinkingPotionConversionRecipe;

import java.util.Optional;

public class QuicksandPotionProjectile extends ThrownPotion {

    public QuicksandPotionProjectile(Level pLevel, LivingEntity pShooter) {
        super(pLevel, pShooter);
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {}



    @Override
    protected void onHit(HitResult pResult) {
        if (!this.level().isClientSide) {

            Vec3 so = pResult.getLocation();
            Vec3 source = new Vec3(Math.floor(so.x)-.5, Math.floor(so.y)+.5, Math.floor(so.z)+.5);
            double sx = source.x-4;
            double sy = source.y-4;
            double sz = source.z-4;
            for (double x=sx; x<sx+8; x++) {
                for (double z=sz; z<sz+8; z++) {
                    if (source.distanceToSqr(x, source.y, z)<7) {
                        for (double y=sy; y<sy+6; y++) {
                            affectBlock(new BlockPos((int) x, (int) y, (int) z));
                        }
                    }
                }
            }

            this.level().levelEvent(2007, this.blockPosition(), PotionUtils.getColor(Potions.TURTLE_MASTER));
            this.discard();
        }
    }

    public InteractionResult affectBlock(BlockPos pPos) {
        Level level = this.level();
        BlockState blockstate = level.getBlockState(pPos);

        ItemStack BlockToItemstack = blockstate.getBlock().asItem().getDefaultInstance();
        SimpleContainer inventory = new SimpleContainer(1);
        inventory.addItem(BlockToItemstack);

        Optional<SinkingPotionConversionRecipe> recipe = level.getRecipeManager().getRecipeFor(SinkingPotionConversionRecipe.Type.INSTANCE, inventory, level);

        if (recipe.isPresent()) {

            RegistryAccess ra = level.registryAccess();

            Item teststack = recipe.get().getResultItem(ra).getItem();

            if (teststack instanceof BlockItem) {
                level.setBlockAndUpdate(pPos, ((BlockItem) teststack).getBlock().defaultBlockState());
            } else {
                level.setBlockAndUpdate(pPos, Blocks.OAK_SIGN.defaultBlockState());
            }
            return InteractionResult.sidedSuccess(level.isClientSide);

        } else {
            return InteractionResult.PASS;
        }

    }
}
