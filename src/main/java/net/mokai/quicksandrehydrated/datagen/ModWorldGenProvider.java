package net.mokai.quicksandrehydrated.datagen;

import net.mokai.quicksandrehydrated.QuicksandRehydrated;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class ModWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder();

    public ModWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Collections.singleton(QuicksandRehydrated.MOD_ID));
    }
}
