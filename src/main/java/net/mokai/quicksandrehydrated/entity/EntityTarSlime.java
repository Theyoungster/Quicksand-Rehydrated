package net.mokai.quicksandrehydrated.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class EntityTarSlime extends Slime implements PredatoryMob {

    private static final int TARGET_SIZE = 4;
    private boolean suppressSplitCheck = false;
    private boolean suppressNextJumpSound = false;

    public EntityTarSlime(EntityType<? extends Slime> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
        this.xpReward = 3;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.20D);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                        net.minecraft.world.DifficultyInstance diff,
                                        MobSpawnType reason,
                                        @Nullable SpawnGroupData data,
                                        @Nullable CompoundTag dataTag) {
        SpawnGroupData d = super.finalizeSpawn(level, diff, reason, data, dataTag);
        if (this.getSize() != TARGET_SIZE) this.setSize(TARGET_SIZE, true);
        return d;
    }

    @Override
    protected void dealDamage(LivingEntity target) {
        super.dealDamage(target);
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 8, 2));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,        20 * 8, 0));
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide) suppressSplitCheck = true;
        try { super.die(source); }
        finally { suppressSplitCheck = false; }
    }

    @Override
    public void remove(RemovalReason reason) {
        boolean guard = !this.level().isClientSide && reason == RemovalReason.KILLED;
        if (guard) suppressSplitCheck = true;
        try { super.remove(reason); }
        finally { suppressSplitCheck = false; }
    }

    @Override
    public int getSize() {
        return suppressSplitCheck ? 1 : super.getSize();
    }

    @Override
    protected int getJumpDelay() {
        return Math.max(1, super.getJumpDelay() + 10);
    }

    /** Use honey slide SFX for jump. */
    @Override protected SoundEvent getJumpSound() { return SoundEvents.HONEY_BLOCK_SLIDE; }

    @Override
    protected void jumpFromGround() {
        suppressNextJumpSound = true;
        float vol = super.getSoundVolume();
        super.jumpFromGround();
        suppressNextJumpSound = false;
        this.playSound(this.getJumpSound(), vol, 0.5F);
    }

    @Override
    protected float getSoundVolume() {
        return suppressNextJumpSound ? 0.0F : super.getSoundVolume();
    }

    @Override public boolean causeFallDamage(float d, float m, DamageSource s) { return false; }
    @Override protected ParticleOptions getParticleType() { return ParticleTypes.SQUID_INK; }
}
