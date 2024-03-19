package net.mokai.quicksandrehydrated.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.mokai.quicksandrehydrated.registry.ModEntityTypes;
import net.mokai.quicksandrehydrated.util.EasingHandler;

import javax.annotation.Nullable;


public class EntityBubble extends Entity {

    private BlockState blockState = Blocks.DIRT.defaultBlockState();
    private boolean init = true;
    public long startLife;
    public long endLife;
    public float scale;
    public float rotAngle;
    @Nullable
    public CompoundTag blockData;

    public EntityBubble(EntityType<? extends EntityBubble> type, Level world) {
        super(type, world);
        if (world.isClientSide()) {
            System.out.println("Clientside, WRONG constructor"); // <---- Only this is being called.
        } else {
            System.out.println("Serverside, WRONG constructor");
        }

    }

    public EntityBubble(EntityType<? extends EntityBubble> type, Level world, Vec3 pos, float pSize, int pLifetime, BlockState bs) {
        super(type, world);
        if (world.isClientSide()) {
            System.out.println("Clientside, right constructor!");
        } else {
            System.out.println("Serverside, right constructor!"); // <---- Only this is being called.
        }

        startprep(world);

        this.setPos(pos);
        blockState = bs;
    }

    public void startprep(Level world) {
        startLife = world.getGameTime();
        endLife = startLife + 60;
        scale = .4f;
        rotAngle = (float)Math.random()*360;
    }

    public static EntityBubble spawn(Level pLevel, Vec3 pPos, BlockState pBlockState) {
        EntityBubble bubble = new EntityBubble(ModEntityTypes.BUBBLE.get(), pLevel, pPos, 0, 0, Blocks.ACACIA_FENCE.defaultBlockState());
        pLevel.addFreshEntity(bubble);
        return bubble;
    }

    public void setBlockState(BlockState bs) {
        blockState = bs;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.put("BlockState", NbtUtils.writeBlockState(this.blockState));
    }

    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.blockState = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), pCompound.getCompound("BlockState"));
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
    protected void defineSynchedData() {}

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

    protected void spawnParticles() {
        for (int i=0; i<3; i++) {
            this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockState).setPos(this.getOnPos()), this.getX(), this.getY()+.5f, this.getZ(), (Math.random() - .5) * 2.0D, .25D, (Math.random() - .5) * 2.0D);
        }
    }

    public static AttributeSupplier.Builder getBubbleAttributes() {
        return LivingEntity.createLivingAttributes().add(ForgeMod.ENTITY_GRAVITY.get(), 0f);
    }

}