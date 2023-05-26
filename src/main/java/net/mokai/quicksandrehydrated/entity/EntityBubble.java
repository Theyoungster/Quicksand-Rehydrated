package net.mokai.quicksandrehydrated.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.mokai.quicksandrehydrated.block.QuicksandBase;
import net.mokai.quicksandrehydrated.block.QuicksandInterface;
import net.mokai.quicksandrehydrated.fluid.FluidQuicksandBase;
import net.mokai.quicksandrehydrated.util.EasingHandler;

import java.util.Random;


public class EntityBubble extends Entity {

    public String texture = "minecraft:textures/block/sand.png";
    private boolean init = true;
    public long startLife;
    public long endLife;
    public float scale;
    public float rotAngle;

    public EntityBubble(EntityType<? extends EntityBubble> type, Level world) {
        super(type, world);
        startLife = world.getGameTime();
        endLife = startLife + 60;
        scale = .4f;
        rotAngle = (float)Math.random()*360;
    }

    public EntityBubble(EntityType<? extends EntityBubble> type, Level world, Vec3 pos ) {
        this(type, world);
        this.setPos(pos);
    }

    public EntityBubble(EntityType<? extends EntityBubble> type, Level world, float pSize, int pLifetime) {
        super(type, world);
    }

    public void initForRender() {
      // this is EXTREMELY UGLY. I'm pretty sure this should be illegal tbh.
        if (init) {
            this.setYRot(rotAngle);
            BlockPos pos = this.getOnPos();
            Block thisBlock = this.level.getBlockState(pos).getBlock();

            if (thisBlock instanceof QuicksandInterface) {
                texture = ((QuicksandInterface)thisBlock).getTex();
            }

            init = false;
        }
    }

    @Override
    protected boolean repositionEntityAfterLoad() {
        return false;
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

        if(this.level.getGameTime() > endLife) { this.kill(); }
        if(this.level.getGameTime()+1 > endLife) { spawnParticles(); } // NGL, no idea why particles can't spawn on the same tick the entity dies on. Thankfully, we can just... get the tick before.

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


    protected void spawnParticles() {
        int i = Mth.floor(this.getX());
        int j = Mth.floor(this.getY() - (double)0.2F);
        int k = Mth.floor(this.getZ());
        BlockPos blockpos = new BlockPos(i, j, k);
        BlockState blockstate = this.level.getBlockState(blockpos);
        System.out.println(blockstate.getBlock());
        for (i=0; i<3; i++) {
            this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(blockpos), this.getX(), this.getY() + .6D, this.getZ(), (Math.random() - .5) * 2.0D, .25D, (Math.random() - .5) * 2.0D);
        }
    }

    public static AttributeSupplier.Builder getBubbleAttributes() {
        return LivingEntity.createLivingAttributes().add(ForgeMod.ENTITY_GRAVITY.get(), 0f);
    }

}