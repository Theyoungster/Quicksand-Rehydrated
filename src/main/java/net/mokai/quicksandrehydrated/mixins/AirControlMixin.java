package net.mokai.quicksandrehydrated.mixins;


import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.mokai.quicksandrehydrated.event.livingBreathingEvent;

import static net.mokai.quicksandrehydrated.util.ModTags.Blocks.QUICKSAND_DROWNABLE;

/**
 * Overrules the standard air supply management of LivingEntity, to allow blocks with the '#qsrehydrated.quicksand_drownabble` tag to trigger loss of oxygen.
 * @author Remem
 * @author Mokai
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


        boolean isPlayer = entity.getType() == EntityType.PLAYER;
        boolean isInvulnerablePlayer = isPlayer && (((Player)entity).getAbilities().invulnerable);
        int maxAir = entity.getMaxAirSupply();


        if(!breathes || air > maxAir || isInvulnerablePlayer)
            return maxAir;
        else if(entity.isAlive()) {



            BlockState currentHeadBlock = entity.level().getBlockState(new BlockPos((int) entity.getX(), (int) entity.getEyeY(), (int) entity.getZ()));
            BlockState blockAtEyes = entity.level().getBlockState(new BlockPos((int) entity.getX(), (int) entity.getEyeY(),(int) entity.getZ()));
            boolean isQuicksand = blockAtEyes.getTags().toList().contains(QUICKSAND_DROWNABLE);

            boolean isBubbled = currentHeadBlock.is(Blocks.BUBBLE_COLUMN);


            if(canBreathe(entity, isQuicksand) || isBubbled ) // If we're in air and can breathe normally, increase air
            {
                if(air < maxAir) { return breatheIn(air, maxAir, entity); }
            }
            else if(!(MobEffectUtil.hasWaterBreathing(entity))) // If we're not in air AND don't have water breathing / bubble columns
            {
                if(air <= -20 && (entity.getHealth()>2 || !isQuicksand))
                {
                    //entity.hurt(DamageSource., 2.0F); TODO
                    return 0;
                } else {
                    return decreaseAirSupply(air, entity);
                }
            }
        }

        if(isPlayer && !entity.level().isClientSide() && entity.level().getGameTime()%1200 == 0) { // Correct clientside air every minute. Should make this more frequent.
            //PacketHandler.sendTo((ServerPlayer)entity, new PacketSyncAir(air));
        }

        return air;
    }


    /**
     * Returns true if the given entity is able to breathe based on the fluid contents of its eye level
     * Similar to IForgeLivingEntity.canDrownInFluidType, but bypassing vanilla breathing lets us treat anything as a drownable fluid.
     */
    private boolean canBreathe(LivingEntity entity, boolean matchesTag)
    {
        FluidType stateAtEyes = entity.getEyeInFluidType();
        //if (blockAtEyes.getBlock().getCollisionShape(blockAtEyes,entity.level,new BlockPos(entity.getX(), entity.getEyeY(), entity.getZ()), entity.collision)
        livingBreathingEvent.LivingCanBreatheFluidEvent event = new livingBreathingEvent.LivingCanBreatheFluidEvent(entity, entity.level().getFluidState(entity.blockPosition().offset(0, (int) entity.getEyeHeight(), 0)));
        MinecraftForge.EVENT_BUS.post(event);

        double d = .1d;
        AABB aabb = AABB.ofSize(entity.getEyePosition(), d, 1.0E-6D, d);
        boolean qsDepthTest = BlockPos.betweenClosedStream(aabb).anyMatch((p_201942_) -> {
            BlockState blockstate = entity.level().getBlockState(p_201942_);
            return !blockstate.isAir()
                    && Shapes.joinIsNotEmpty(blockstate.getShape(entity.level(), p_201942_).move((double)p_201942_.getX(), (double)p_201942_.getY(), (double)p_201942_.getZ()), Shapes.create(aabb), BooleanOp.AND);
        });



        boolean drowning = entity.canDrownInFluidType(stateAtEyes) || (matchesTag && qsDepthTest);

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


