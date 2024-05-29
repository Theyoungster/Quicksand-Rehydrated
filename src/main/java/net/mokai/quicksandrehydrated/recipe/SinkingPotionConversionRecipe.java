package net.mokai.quicksandrehydrated.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Decoder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import org.jetbrains.annotations.Nullable;

public class SinkingPotionConversionRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;

    public SinkingPotionConversionRecipe(ResourceLocation id, ItemStack output,
                             NonNullList<Ingredient> recipeItems) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if(pLevel.isClientSide()) {
            return false;
        }

        return recipeItems.get(0).test(pContainer.getItem(0));
    }


    @Override
    public NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SinkingPotionConversionRecipe.Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return SinkingPotionConversionRecipe.Type.INSTANCE;
    }

    public static class Type implements RecipeType<SinkingPotionConversionRecipe> {
        private Type() { }
        public static final SinkingPotionConversionRecipe.Type INSTANCE = new SinkingPotionConversionRecipe.Type();
        public static final String ID = "sinking_potion_conversion";
    }


    public static class Serializer implements RecipeSerializer<SinkingPotionConversionRecipe> {
        public static final SinkingPotionConversionRecipe.Serializer INSTANCE = new SinkingPotionConversionRecipe.Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(QuicksandRehydrated.MOD_ID, "sinking_potion_conversion");


        @Override
        public SinkingPotionConversionRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {

            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> input = NonNullList.withSize(1, Ingredient.EMPTY);
            input.set(0, Ingredient.fromJson(ingredients.get(0)));
            return new SinkingPotionConversionRecipe(pRecipeId, output, input);

        }

        @Override
        public @Nullable SinkingPotionConversionRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            return null;
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, SinkingPotionConversionRecipe pRecipe) {

        }
    }
}
