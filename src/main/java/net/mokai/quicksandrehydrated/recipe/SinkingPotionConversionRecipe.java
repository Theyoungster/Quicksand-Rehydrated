package net.mokai.quicksandrehydrated.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
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
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
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
            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }
            return new SinkingPotionConversionRecipe(pRecipeId, output, inputs);

        }

        @Override
        public @Nullable SinkingPotionConversionRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int size = buf.readInt();
            NonNullList<Ingredient> inputs = NonNullList.withSize(size, Ingredient.EMPTY);
            
            for (int i = 0; i < size; i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            ItemStack output = buf.readItem();
            return new SinkingPotionConversionRecipe(id, output, inputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SinkingPotionConversionRecipe recipe) {

            buf.writeInt(recipe.getIngredients().size());

            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }

            buf.writeItemStack(recipe.output, false);
        }
    }
}
