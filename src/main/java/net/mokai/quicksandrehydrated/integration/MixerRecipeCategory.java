package net.mokai.quicksandrehydrated.integration;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.block.ModBlocks;
import net.mokai.quicksandrehydrated.recipe.FluidMixerRecipes;
import net.minecraft.network.chat.Component;
/*
public class MixerRecipeCategory { implements IRecipeCategory<FluidMixerRecipes> {
    public final static ResourceLocation UID = new ResourceLocation(QuicksandRehydrated.MOD_ID, "mixer");
    public final static ResourceLocation TEXTURE =
            new ResourceLocation(QuicksandRehydrated.MOD_ID, "textures/gui/mixer_gui.png");

    private final IDrawable background;
    private final IDrawable icon;

    public MixerRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.MIXER.get()));
    }

    @Override
    public RecipeType<FluidMixerRecipes> getRecipeType() {
        return JEIQuicksandPlugin.MIXER_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Fluid Mixer");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FluidMixerRecipes recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 86, 15).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 55, 15)
                        .addIngredients(ForgeTypes.FLUID_STACK, List.of(recipe.getFluid()))
                        .setFluidRenderer(64000, false, 16, 61);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 86, 60).addItemStack(recipe.getResultItem());
    }
}
*/