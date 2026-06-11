package net.mokai.quicksandrehydrated.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.worldgen.feature.QuicksandPitConfiguration;


public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> QUICKSAND_PIT_KEY = registerKey("quicksand_pit");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        // Create a default configuration for the quicksand pit feature
        // This will be overridden by the JSON configuration if provided
        QuicksandPitConfiguration defaultConfig = new QuicksandPitConfiguration(
                QuicksandPitConfiguration.DEFAULT_BLOCK,
                QuicksandPitConfiguration.DEFAULT_MIN_RADIUS,
                QuicksandPitConfiguration.DEFAULT_MAX_RADIUS,
                QuicksandPitConfiguration.DEFAULT_MIN_DEPTH,
                QuicksandPitConfiguration.DEFAULT_MAX_DEPTH,
                QuicksandPitConfiguration.DEFAULT_IRREGULARITY,
                QuicksandPitConfiguration.DEFAULT_HAS_BORDER,
                QuicksandPitConfiguration.DEFAULT_HAS_SURFACE,
                java.util.Optional.ofNullable(QuicksandPitConfiguration.DEFAULT_BORDER_BLOCK),
                java.util.Optional.ofNullable(QuicksandPitConfiguration.DEFAULT_SURFACE_BLOCK),
                java.util.Optional.of(QuicksandPitConfiguration.DEFAULT_REPLACEABLE_BLOCKS),
                QuicksandPitConfiguration.DEFAULT_MIN_HEIGHT,
                QuicksandPitConfiguration.DEFAULT_MAX_HEIGHT
        );
        
        // Register the quicksand pit feature with the default configuration
        register(context, QUICKSAND_PIT_KEY, ModFeatures.QUICKSAND_PIT.get(), defaultConfig);
    }


    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(QuicksandRehydrated.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
