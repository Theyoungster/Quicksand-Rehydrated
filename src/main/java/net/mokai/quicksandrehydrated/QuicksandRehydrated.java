package net.mokai.quicksandrehydrated;

import net.mokai.quicksandrehydrated.util.compat.CuriosCompat;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.mokai.quicksandrehydrated.client.compat.CuriosClientCompat;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.mokai.quicksandrehydrated.entity.coverage.WashingSystem;
import net.mokai.quicksandrehydrated.loot.ModLootModifiers;
import net.mokai.quicksandrehydrated.networking.ModMessages;
import net.mokai.quicksandrehydrated.networking.EngulfMessages;
import net.mokai.quicksandrehydrated.registry.*;
import net.mokai.quicksandrehydrated.screen.MixerScreen;
import net.mokai.quicksandrehydrated.screen.ModMenuTypes;
import net.mokai.quicksandrehydrated.util.CameraBoxDimensions;
import net.mokai.quicksandrehydrated.worldgen.ModRegion;
import net.mokai.quicksandrehydrated.worldgen.ModSurfaceRuleData;
import net.mokai.quicksandrehydrated.worldgen.placement.ModPlacementModifierTypes;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;


import static net.mokai.quicksandrehydrated.util.ModTags.Blocks.QUICKSAND_DROWNABLE;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(QuicksandRehydrated.MOD_ID)
public class QuicksandRehydrated {

    public static final String MOD_ID = "qsrehydrated";

    public QuicksandRehydrated() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        QuicksandRegistry.register(modEventBus);
        ModFluids.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModParticles.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        ModSounds.register(modEventBus);
        ModCreativeModeTab.register(modEventBus);
        ModFeatures.register(modEventBus);
        ModPlacementModifierTypes.register(modEventBus);


        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::enqueueImc);

        MinecraftForge.EVENT_BUS.register(this);

        //modEventBus.addListener(this::addCreative);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModMessages.register();
            EngulfMessages.register();
            // Register world generation for quicksand pits
            ModFeatures.registerWorldGeneration();
            Regions.register(new ModRegion(new ResourceLocation(MOD_ID, "overworld_1"), 2));
            SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MOD_ID, ModSurfaceRuleData.makeRules());
        });
    }

    private void enqueueImc(final InterModEnqueueEvent event) {
        if (ModList.get().isLoaded("curios")) {
            CuriosCompat.registerSlotTypes();
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

            //ItemBlockRenderTypes.setRenderLayer(ModFluids.DRY_QUICKSAND.get(), RenderType.solid());
            MenuScreens.register(ModMenuTypes.MIXER_MENU.get(), MixerScreen::new);

            if (ModList.get().isLoaded("curios")) {
                CuriosClientCompat.registerRenderers();
            }
        }
    }


    // Used to check whether the *first-person camera* is inside a block of quicksand
    // We use this as the main mechanism to determine if we're drowning or not
    private static boolean boxOverlapsQuicksandBlock(Level level, AABB box) {
        BlockPos minXYZ = BlockPos.containing(box.minX, box.minY, box.minZ);
        BlockPos maxXYZ = BlockPos.containing(box.maxX, box.maxY, box.maxZ);
        for (BlockPos blockPos: BlockPos.betweenClosed(minXYZ, maxXYZ)) {
            BlockState blockState = level.getBlockState(blockPos);
            // Previously also had an "instanceof QuicksandBase" check, but I'm not sure if we need that
            if (!blockState.isAir() && blockState.is(QUICKSAND_DROWNABLE)) {
                // Intentionally use the visual bounds of the block, rather than the colission mark.
                // Assumes the quicksand shape is some kindof cube / rectangular prism
                AABB boundingBox = blockState.getShape(level, blockPos).bounds().move(blockPos);
                if (boundingBox.intersects(box)) {
                    return true;
                }
            }
        }
        return false;
    }


    // Used to check for "bonus" air such as the snorkel and reed
    private static boolean hasBonusAirAt(LivingEntity entity, double yOffset) {
        var level = entity.level();
        Vec3 position = entity.getEyePosition().relative(Direction.UP, yOffset);
        BlockPos blockPos = BlockPos.containing(position);
        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.isAir()) {
            return true;
        }
        // Previously also had an "instanceof QuicksandBase" check, but I'm not sure if we need that
        if (blockState.is(QUICKSAND_DROWNABLE)) {
            // Assumes the quicksand is a box
            AABB boundingBox = blockState.getShape(level, blockPos).bounds().move(blockPos);
            return !boundingBox.contains(position);
        }
        FluidState fluidState = blockState.getFluidState();
        if (fluidState.getFluidType().canDrownIn(entity)) {
            // Can breathe if we're above the surface of the fluid, otherwise not
            return position.y > blockPos.getY() + fluidState.getOwnHeight();
        }
        // For everything else, we can breathe in non-solid blocks like signs
        return !blockState.isSolid();
    }

    private static boolean entityIsHoldingReed(LivingEntity entity) {
        Iterable<ItemStack> handitems = entity.getHandSlots();
        for (ItemStack item : handitems) {
            if (item.getItem().equals(ModItems.BREATHING_REED.get())) {
                return true;
            }
        }
        return false;
    }

    private static boolean entityIsWearingSnorkel(LivingEntity entity) {
        Iterable<ItemStack> armorItems = entity.getArmorSlots();
        for (ItemStack item : armorItems) {
            // TODO: Check if this accounts for baubles. Also, add Baubles compatibility in the first place-
            if (item.getItem().equals(ModItems.SNORKEL_MASK.get())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set entity to drown if their breathing point is in quicksand. This position is usually the eye position, but
     * the Snorkel and Breathing Reed increase this by +1.5 and +2 blocks respectively.
     */
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class GameModEvents {
        @SubscribeEvent
        public static void onLivingBreatheEvent(LivingBreatheEvent event) {
            LivingEntity entity = event.getEntity();

            double reedHeight = 2;
            double snorkelHeight = 1.5; // SET TO BE CONFIGURABLE

            boolean canBreatheThroughReed = entityIsHoldingReed(entity) && hasBonusAirAt(entity, reedHeight);
            boolean canBreatheThroughSnokel = entityIsWearingSnorkel(entity) && hasBonusAirAt(entity, snorkelHeight);

            if (canBreatheThroughReed || canBreatheThroughSnokel) {
                // If we're certain we have a source of air, we can breathe
                event.setCanBreathe(true);
                event.setCanRefillAir(true);
            } else {
                double w = CameraBoxDimensions.FULL_WIDTH;
                double h = CameraBoxDimensions.FULL_HEIGHT;
                AABB cameraBox = AABB.ofSize(entity.getEyePosition(), w, h, w);
                if (boxOverlapsQuicksandBlock(entity.level(), cameraBox)) {
                    // We're definitely suffocating, mrrrrhf~
                    event.setCanBreathe(false);
                    event.setCanRefillAir(false);
                }
            }

            // We're in some other situation. Don't modify the event at all and assume something else handles it
            // (e.g. drowning in water is already handled by the game)
        }
        
        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            // Process at the end of the tick
            if (event.phase == TickEvent.Phase.END) {
                Player player = event.player;
                Level level = player.level();
                
                // Apply washing effect if player is in water
                // This needs to run on both client and server sides
                // - Server side: for actual coverage removal logic
                // - Client side: for particle effects
                WashingSystem.applyWashingEffect(player, level);
            }
        }

        // Let's temporarily comment out this event to see if it is the cause of the problem.
        /*
        @SubscribeEvent
        public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
            // Your custom logic goes here
            LivingEntity entity = event.getEntity();

            if (entity.level().isClientSide()) {
                System.out.print("[C] ");
            }
            else {
                System.out.print("[S] ");
            }
            System.out.println(entity.getName() + " jumped!");

            // Check whether the entity is actually on a block of quicksand.
            // Use the current position of the entity instead of getOnPosLegacy()
            BlockPos entityPos = entity.blockPosition().below();
            BlockState blockState = entity.level().getBlockState(entityPos);

            if (blockState.getBlock() instanceof QuicksandBase) {
                QuicksandBase quicksand = (QuicksandBase) blockState.getBlock();
                quicksand.sinkableJumpOff(blockState, entity.level(), entityPos, entity);
            }
        }
        */

    }






}
