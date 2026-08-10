package net.mokai.quicksandrehydrated.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.networking.EngulfMessages;
import net.mokai.quicksandrehydrated.networking.packet.EngulfStateSyncS2CPacket;

import java.util.HashMap;
import java.util.List;

@Mod.EventBusSubscriber(modid = QuicksandRehydrated.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EngulfEvents {

    private static final String K_ENGULFED = "slimeengulf.engulfed";
    private static final String K_TICKS = "slimeengulf.ticks";
    private static final String K_DMG_TIMER = "slimeengulf.dmg_timer";
    private static final String K_SNEAK_PREV = "slimeengulf.prev_sneak";
    private static final String K_PROGRESS = "slimeengulf.escape_progress";
    private static final String K_OWNER_ID = "slimeengulf.slime_id";
    private static final String K_COOLDOWN = "slimeengulf.cd";
    private static final String K_IMMUNITY = "slimeengulf.immunity";
    private static final String K_SINK_PROG = "slimeengulf.sink_prog";

    private static final float MIN_SIZE = 2.0f;
    private static final int COOLDOWN_TICKS = 40;
    private static final int IMMUNITY_AFTER_ESCAPE = 100;

    private static final int DMG_INTERVAL = 20;
    private static final float DMG_BASE = 0.5f;
    private static final int RAMP_STEP_TICKS = 60;
    private static final int RAMP_MAX_STAGE = 5;
    private static final float DEPTH_DMG_BONUS = 0.80f;

    private static final double SINK_START_FACTOR = 0.94;
    private static final double INSIDE_OFFSET_FACTOR = 0.26;
    private static final double VERTICAL_BLEND = 0.18;
    private static final double CENTER_PULL_SCALE = 0.12;
    private static final double MOTION_DAMP = 0.38;
    private static final double SINK_RATE = 0.0085;
    private static final double SINK_RATE_ACCEL = 0.0025;

    private static final int REQUIRED_TOGGLES = 12;
    private static final int PROGRESS_DECAY_TPS = 40;

    private static int TICK;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase == TickEvent.Phase.END) TICK++;
    }

    @SubscribeEvent
    public static void onSlimeHitsPlayer(LivingAttackEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (p.level().isClientSide) return;
        Entity source = e.getSource().getEntity();
        if (!(source instanceof Slime slime)) return;
        if (!slime.isAlive() || slime.getBbWidth() < MIN_SIZE) return;
        var n = p.getPersistentData();
        if (n.getInt(K_IMMUNITY) > 0) return;
        if (n.getInt(K_COOLDOWN) > 0) return;
        if (n.getBoolean(K_ENGULFED)) {
            e.setCanceled(true);
            return;
        }
        startEngulf(p, slime);
        e.setCanceled(true);
        n.putInt(K_COOLDOWN, COOLDOWN_TICKS);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        Player p = e.player;
        if (p.level().isClientSide) return;
        var n = p.getPersistentData();
        int cd = n.getInt(K_COOLDOWN);
        if (cd > 0) n.putInt(K_COOLDOWN, cd - 1);
        int im = n.getInt(K_IMMUNITY);
        if (im > 0) n.putInt(K_IMMUNITY, im - 1);
        if (!n.getBoolean(K_ENGULFED)) {
            if (p.noPhysics) p.noPhysics = false;
            return;
        }
        tickEngulf(p);
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent e) {
        if (e.getEntity().level().isClientSide) return;
        if (e.getEntity() instanceof Player p) {
            if (isEngulfed(p)) endEngulf(p);
        } else if (e.getEntity() instanceof Slime s) {
            if (!(s.level() instanceof ServerLevel server)) return;
            for (ServerPlayer sp : server.players()) {
                var n = sp.getPersistentData();
                if (n.getBoolean(K_ENGULFED) && n.getInt(K_OWNER_ID) == s.getId()) {
                    endEngulf(sp);
                    sp.getPersistentData().putInt(K_IMMUNITY, IMMUNITY_AFTER_ESCAPE);
                }
            }
        }
    }

    public static void handleClientSneakChange(ServerPlayer p, boolean sneakDown) {
        var n = p.getPersistentData();
        if (!n.getBoolean(K_ENGULFED)) {
            return;
        }

        boolean prev = n.getBoolean(K_SNEAK_PREV);
        if (sneakDown == prev) {
            return;
        }

        n.putBoolean(K_SNEAK_PREV, sneakDown);
        int progTog = Math.min(REQUIRED_TOGGLES, n.getInt(K_PROGRESS) + 1);
        n.putInt(K_PROGRESS, progTog);
        p.level().playSound(null, p.blockPosition(), SoundEvents.MAGMA_CUBE_JUMP, SoundSource.PLAYERS, 0.6f, 0.5f);
        syncClientState(p, true, progTog);
        if (progTog >= REQUIRED_TOGGLES) {
            endEngulf(p);
            n.putInt(K_IMMUNITY, IMMUNITY_AFTER_ESCAPE);
        }
    }

    private static void startEngulf(Player p, Slime s) {
        var n = p.getPersistentData();
        n.putBoolean(K_ENGULFED, true);
        n.putInt(K_TICKS, 0);
        n.putInt(K_DMG_TIMER, 0);
        n.putBoolean(K_SNEAK_PREV, p.isShiftKeyDown());
        n.putInt(K_PROGRESS, 0);
        n.putFloat(K_SINK_PROG, 0f);
        n.putInt(K_OWNER_ID, s.getId());
        p.level().playSound(null, p.blockPosition(), SoundEvents.SLIME_ATTACK, SoundSource.HOSTILE, 1.0f, 0.5f);
        p.noPhysics = true;
        double topY = s.getY() + s.getBbHeight() * SINK_START_FACTOR;
        p.setPos(s.getX(), topY, s.getZ());
        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 7, true, false));
        p.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 2, true, false));
        p.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1, true, false));
        syncClientState(p, true, 0);
    }

    private static void tickEngulf(Player p) {
        var n = p.getPersistentData();
        Slime s = owner(p);
        if (s == null || !s.isAlive()) {
            endEngulf(p);
            return;
        }
        if (p.distanceToSqr(s) > 12 * 12) {
            endEngulf(p);
            n.putInt(K_IMMUNITY, IMMUNITY_AFTER_ESCAPE);
            return;
        }
        dampenSlime(s);
        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 7, true, false));
        p.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 10, 2, true, false));
        p.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10, 1, true, false));
        int ticks = n.getInt(K_TICKS) + 1;
        n.putInt(K_TICKS, ticks);
        int rateStage = Math.min(RAMP_MAX_STAGE, ticks / RAMP_STEP_TICKS);
        float prog = n.getFloat(K_SINK_PROG);
        prog += (float) (SINK_RATE + SINK_RATE_ACCEL * rateStage);
        if (prog > 1f) prog = 1f;
        n.putFloat(K_SINK_PROG, prog);
        double topY = s.getY() + s.getBbHeight() * SINK_START_FACTOR;
        double endY = s.getY() + s.getBbHeight() * INSIDE_OFFSET_FACTOR;
        double targetY = topY + (endY - topY) * prog;
        Vec3 center = s.position();
        Vec3 pull = center.subtract(p.position()).scale(CENTER_PULL_SCALE);
        double dy = (targetY - p.getY()) * VERTICAL_BLEND;
        p.setDeltaMovement(p.getDeltaMovement().scale(MOTION_DAMP).add(pull.x, dy, pull.z));
        p.hurtMarked = true;
        p.setPos(p.getX(), targetY, p.getZ());
        int stage = Math.min(RAMP_MAX_STAGE, ticks / RAMP_STEP_TICKS);
        float base = DMG_BASE * (1 + stage);
        float finalDmg = base * (1.0f + prog * DEPTH_DMG_BONUS);
        int t = n.getInt(K_DMG_TIMER) + 1;
        if (t >= DMG_INTERVAL) {
            t = 0;
            boolean magma = s instanceof MagmaCube;
            DamageSource ds = magma ? p.damageSources().lava() : p.damageSources().drown();
            p.hurt(ds, finalDmg);
            p.level().playSound(null, p.blockPosition(),
                    magma ? SoundEvents.MAGMA_CUBE_SQUISH : SoundEvents.SLIME_SQUISH,
                    SoundSource.HOSTILE, 0.8f, 0.5f);
        }
        n.putInt(K_DMG_TIMER, t);
        int progTog = n.getInt(K_PROGRESS);
        if (TICK % PROGRESS_DECAY_TPS == 0 && progTog > 0) {
            progTog -= 1;
            n.putInt(K_PROGRESS, progTog);
            syncClientState(p, true, progTog);
        }
    }

    private static void endEngulf(Player p) {
        if (p.isPassenger()) p.stopRiding();
        p.noPhysics = false;
        clearState(p);
        syncClientState(p, false, 0);
        p.level().playSound(null, p.blockPosition(), SoundEvents.SLIME_SQUISH, SoundSource.PLAYERS, 1.0f, 1.3f);
    }

    private static boolean isEngulfed(Player p) {
        return p.getPersistentData().getBoolean(K_ENGULFED);
    }

    private static void clearState(Player p) {
        var n = p.getPersistentData();
        n.putBoolean(K_ENGULFED, false);
        n.remove(K_TICKS);
        n.remove(K_DMG_TIMER);
        n.remove(K_SNEAK_PREV);
        n.remove(K_PROGRESS);
        n.remove(K_OWNER_ID);
        n.remove(K_SINK_PROG);
    }

    private static Slime owner(Player p) {
        int id = p.getPersistentData().getInt(K_OWNER_ID);
        if (id == 0 || !(p.level() instanceof ServerLevel s)) return null;
        List<Slime> list = s.getEntitiesOfClass(Slime.class, p.getBoundingBox().inflate(18.0));
        for (Slime x : list) if (x.getId() == id) return x;
        return null;
    }

    private static void dampenSlime(Slime s) {
        if (!(s.level() instanceof ServerLevel)) return;
        s.getNavigation().stop();
        Vec3 m = s.getDeltaMovement();
        s.setDeltaMovement(m.x * 0.35, Math.min(0.0, m.y), m.z * 0.35);
        s.hasImpulse = true;
    }

    private static void syncClientState(Player p, boolean engulfed, int progress) {
        if (p instanceof ServerPlayer sp) {
            EngulfMessages.sendToPlayer(new EngulfStateSyncS2CPacket(engulfed, progress), sp);
        }
    }
}
