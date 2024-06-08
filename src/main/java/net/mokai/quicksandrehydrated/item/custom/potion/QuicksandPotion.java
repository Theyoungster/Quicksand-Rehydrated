package net.mokai.quicksandrehydrated.item.custom.potion;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.mokai.quicksandrehydrated.recipe.SinkingPotionConversionRecipe;

import java.util.Optional;

public class QuicksandPotion extends PotionItem {

    public QuicksandPotion(Properties pProperties) {
        super(pProperties);
    }


    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        Player player = pEntityLiving instanceof Player ? (Player)pEntityLiving : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, pStack);
        }

        if (!pLevel.isClientSide) {
            MobEffectInstance MEI = new MobEffectInstance(MobEffect.byId(2));
            System.out.println(MobEffect.byId(2));
            MEI.getEffect().applyInstantenousEffect(player, player, pEntityLiving, MEI.getAmplifier(), 1);
            /*
            for(MobEffectInstance mobeffectinstance : PotionUtils.getMobEffects(pStack)) {
                if (mobeffectinstance.getEffect().isInstantenous()) {
                    mobeffectinstance.getEffect().applyInstantenousEffect(player, player, pEntityLiving, mobeffectinstance.getAmplifier(), 1.0D);

                } else {
                    pEntityLiving.addEffect(new MobEffectInstance(mobeffectinstance));
                }
            }*/
        }

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                pStack.shrink(1);
            }
        }

        if (player == null || !player.getAbilities().instabuild) {
            if (pStack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (player != null) {
                player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        pEntityLiving.gameEvent(GameEvent.DRINK);
        return pStack;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        Player player = pContext.getPlayer();
        ItemStack itemstack = pContext.getItemInHand();
        BlockState blockstate = level.getBlockState(blockpos);

        ItemStack BlockToItemstack = blockstate.getBlock().asItem().getDefaultInstance();
        SimpleContainer inventory = new SimpleContainer(1);
        inventory.addItem(BlockToItemstack);

        Optional<SinkingPotionConversionRecipe> recipe = level.getRecipeManager().getRecipeFor(SinkingPotionConversionRecipe.Type.INSTANCE, inventory, level);

        if (recipe.isPresent()) {

            RegistryAccess ra = level.registryAccess();
            Item teststack = recipe.get().getResultItem(ra).getItem();

            if (teststack instanceof BlockItem) {
                level.playSound(null, blockpos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
                player.setItemInHand(pContext.getHand(), ItemUtils.createFilledResult(itemstack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.awardStat(Stats.ITEM_USED.get(itemstack.getItem()));

                if (!level.isClientSide) {
                    ServerLevel serverlevel = (ServerLevel) level;

                    for (int i = 0; i < 5; ++i) {
                        serverlevel.sendParticles(ParticleTypes.SPLASH, (double) blockpos.getX() + level.random.nextDouble(), (double) (blockpos.getY() + 1), (double) blockpos.getZ() + level.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                    }
                }
                // TODO: We actually, probably don't need Potions of Sinking to work like this.
                player.swing(pContext.getHand(), true);

                level.playSound(null, blockpos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PLACE, blockpos);
                level.setBlockAndUpdate(blockpos, ((BlockItem) teststack).getBlock().defaultBlockState());

            } else {
                level.setBlockAndUpdate(blockpos, Blocks.OAK_SIGN.defaultBlockState());

            }
            return InteractionResult.sidedSuccess(level.isClientSide);

        } else {
            return InteractionResult.PASS;
        }

    }

    @Override
    public boolean isFoil(ItemStack pStack) {return true;}
}
