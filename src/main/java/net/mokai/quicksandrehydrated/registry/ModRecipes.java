package net.mokai.quicksandrehydrated.registry;

import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mokai.quicksandrehydrated.recipe.FluidMixerRecipes;
import net.mokai.quicksandrehydrated.recipe.SinkingPotionConversionRecipe;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, QuicksandRehydrated.MOD_ID);

    public static final RegistryObject<RecipeSerializer<FluidMixerRecipes>> FLUID_MIXER_SERIALIZER =
            SERIALIZERS.register("fluid_mixer", () -> FluidMixerRecipes.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<SinkingPotionConversionRecipe>> SINKING_POTION_CONVERSION_SERALIZER =
            SERIALIZERS.register("sinking_potion_conversion", () -> SinkingPotionConversionRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
