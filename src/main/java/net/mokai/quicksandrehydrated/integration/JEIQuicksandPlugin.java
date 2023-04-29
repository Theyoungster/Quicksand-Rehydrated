package net.mokai.quicksandrehydrated.integration;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.recipe.FluidMixerRecipes;

import java.util.List;
import java.util.Objects;
/*
@JeiPlugin
public class JEIQuicksandPlugin { //implements IModPlugin {
    public static RecipeType<FluidMixerRecipes> MIXER_TYPE =
            new RecipeType<>(MixerRecipeCategory.UID, FluidMixerRecipes.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(QuicksandRehydrated.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new
                MixerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        List<FluidMixerRecipes> recipes = rm.getAllRecipesFor(FluidMixerRecipes.Type.INSTANCE);
        registration.addRecipes(MIXER_TYPE, recipes);
    }
}
*/