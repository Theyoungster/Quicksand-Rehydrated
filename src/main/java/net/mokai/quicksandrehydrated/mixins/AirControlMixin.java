package net.mokai.quicksandrehydrated.mixins;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeLivingEntity;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fluids.FluidType;
import net.mokai.quicksandrehydrated.block.QuicksandBase;
import net.mokai.quicksandrehydrated.event.livingBreathingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;
import java.util.Arrays;

import static net.mokai.quicksandrehydrated.util.ModTags.Blocks.QUICKSAND_DROWNABLE;

/**
 * Overrules the standard air supply management of LivingEntity to allow interaction with the ability system
 * @author Remem
 *
 */

//Yeah, uh, huge shoutouts to Lyinginbedmon for this bit.

@Mixin(LivingEntity.class)
public abstract class AirControlMixin
{
    @Shadow public abstract boolean isInWall();

    private int cachedAir = 0;

    @Inject(method = "baseTick()V", at = @At("HEAD"))
    public void airControlHead(final CallbackInfo ci)
    {
        LivingEntity living = (LivingEntity)(Object)this;
        cachedAir = living.getAirSupply();
    }

    @Inject(method = "baseTick()V", at = @At("RETURN"))
    public void airControlReturn(final CallbackInfo ci)
    {
        LivingEntity living = (LivingEntity)(Object)this;

        living.setAirSupply(handleAir(living, cachedAir));
    }

    /** Manage air for creatures that breathe */
    private int handleAir(LivingEntity entity, int air)
    {

        boolean breathes = true;

        //if(entity.level.isClientSide()) { return air; }

        boolean isPlayer = entity.getType() == EntityType.PLAYER;
        boolean isInvulnerablePlayer = isPlayer && (((Player)entity).getAbilities().invulnerable);

        int maxAir = entity.getMaxAirSupply();
        if(!breathes || air > maxAir
                // TODO: RE-ENABLE THIS BEFORE RELEASE. THIS IS DEBUG TO TEST IN CREATIVE MODE.
        //        || isInvulnerablePlayer
        )
            return maxAir;
        else if(entity.isAlive())
        {












            BlockState currentHeadBlock = entity.getLevel().getBlockState(new BlockPos(entity.getX(), entity.getEyeY(), entity.getZ()));

            boolean isBubbled = currentHeadBlock.is(Blocks.BUBBLE_COLUMN);


            if(canBreathe(entity) || isBubbled ) // If we're in air and can breathe normally, increase air
            {
                if(air < maxAir) { return breatheIn(air, maxAir, entity); }
            }
            else if(!(MobEffectUtil.hasWaterBreathing(entity) || isBubbled)) // If we're not in air AND don't have water breathing / bubble columns
            {
                if(air == -20)
                {
                    entity.hurt(DamageSource.DROWN, 2.0F);
                    return 0;
                }
                else
                    return decreaseAirSupply(air, entity);
            }
        }

        if(isPlayer && !entity.getLevel().isClientSide() && entity.getLevel().getGameTime()%1200 == 0) { // Correct clientside air every minute. Should make this more frequent.
            //PacketHandler.sendTo((ServerPlayer)entity, new PacketSyncAir(air));
        }

        return air;
    }

    /**
     * Returns true if the given entity is able to breathe based on the fluid contents of its eye level.<br>
     * Similar to IForgeLivingEntity.canDrownInFluidType, but bypassing vanilla breathing lets us treat air as a fluid as well.
     * @param entity
     * @return
     */
    private boolean canBreathe(LivingEntity entity)
    {
        FluidType stateAtEyes = entity.getEyeInFluidType();
        BlockState blockAtEyes = entity.getLevel().getBlockState(new BlockPos(entity.getX(), entity.getEyeY(),entity.getZ()));
        //if (blockAtEyes.getBlock().getCollisionShape(blockAtEyes,entity.level,new BlockPos(entity.getX(), entity.getEyeY(), entity.getZ()), entity.collision)
        livingBreathingEvent.LivingCanBreatheFluidEvent event = new livingBreathingEvent.LivingCanBreatheFluidEvent(entity, entity.getLevel().getFluidState(entity.blockPosition().offset(0, entity.getEyeHeight(), 0)));
        MinecraftForge.EVENT_BUS.post(event);


        float f = 0.1f;
        AABB aabb = AABB.ofSize(entity.getEyePosition(), (double)f, 1.0E-6D, (double)f);
        boolean test = BlockPos.betweenClosedStream(aabb).anyMatch((p_201942_) -> {
            BlockState blockstate = entity.level.getBlockState(p_201942_);
            return !blockstate.isAir()
                    //&& blockstate.isSuffocating(entity.level, p_201942_)
                    //&& Shapes.joinIsNotEmpty(blockstate.getShape(entity.level, new BlockPos(entity.getX(), entity.getEyeY(), entity.getZ())
                    && Shapes.joinIsNotEmpty(blockstate.getShape(entity.level, p_201942_).move((double)p_201942_.getX(), (double)p_201942_.getY(), (double)p_201942_.getZ()), Shapes.create(aabb), BooleanOp.AND);
        });




        boolean matchesTag = blockAtEyes.getTags().toList().contains(QUICKSAND_DROWNABLE);
        boolean drowning =
                ((IForgeLivingEntity) entity).canDrownInFluidType(stateAtEyes) || (matchesTag && test);


        return !drowning;
    }

    private int decreaseAirSupply(int air, LivingEntity entityIn)
    {
        int i = EnchantmentHelper.getRespiration(entityIn);
        return i > 0 && entityIn.getRandom().nextInt(i + 1) > 0 ? air : air - 1;
    }

    private int breatheIn(int currentAir, int maxAir, LivingEntity entityIn)
    {
        return Math.min(currentAir + 4, maxAir);
    }

}


