package net.mokai.quicksandrehydrated.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;
import net.mokai.quicksandrehydrated.block.QuicksandBase;
import net.mokai.quicksandrehydrated.util.EasingHandler;

import java.util.Random;


public class EntityBubble extends Entity {

    public String texture = "minecraft:textures/block/sand.png";
    private boolean init = true;
    public long startLife;
    public long endLife;
    public float scale;

    public EntityBubble(EntityType<? extends EntityBubble> type, Level world) {
        super(type, world);
        startLife = world.getGameTime();
        endLife = startLife + 60;
        scale = .4f;
    }

    public EntityBubble(EntityType<? extends EntityBubble> type, Level world, float pSize, int pLifetime) {
        super(type, world);
    }


    public double getSize() {
        long ct = this.level.getGameTime();
        return Math.sqrt(
                EasingHandler.reverse_interp(
                        startLife,
                        endLife,
                        ct + Minecraft.getInstance().getPartialTick())) * scale;
    }

    @Override
    public float getEyeHeight(Pose pose) {
        return this.getBbHeight() * 0.7F;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    public void tick() {
        if (init) {
            BlockPos pos = this.getOnPos();
            Level l = this.level;
            BlockState bs = l.getBlockState(pos);
            if (bs.getBlock() instanceof QuicksandBase) {
                QuicksandBase source = (QuicksandBase) bs.getBlock();
                texture = source.getTex();
            }
            init = false;
        }
        if(level.getGameTime() > endLife) {
            kill();
        }
    }

    @Override
    public void kill() {
        this.playSound(SoundEvents.LAVA_POP);
        this.playSound(SoundEvents.LAVA_POP, .5f+ ((float)Math.random()*.25f), .6f);
        this.playSound(SoundEvents.SLIME_ATTACK, .15f, .6f);
        super.kill();
    }

    public ResourceLocation getTexture() {
        return new ResourceLocation(texture);
    }

    public static AttributeSupplier.Builder getBubbleAttributes() {
        return LivingEntity.createLivingAttributes().add(ForgeMod.ENTITY_GRAVITY.get(), 0f);
    }

}