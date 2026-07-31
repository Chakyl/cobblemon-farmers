package io.github.chakyl.cobblemonfarmers.recipe;


import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EnergyPylonRecipe implements Recipe<RecipeWrapper> {
    private final Ingredient ingredient;
    private final ItemStack input;
    private final ItemStack result;
    private final float consumeChance;
    private final float bonusSpeed;
    private final ResourceLocation id;

    public EnergyPylonRecipe(Ingredient ingredient, ItemStack input, float consumeChance, float bonusSpeed,  ItemStack result, ResourceLocation id) {
        this.ingredient = ingredient;
        this.input = input;
        this.result = result;
        this.consumeChance = consumeChance;
        this.bonusSpeed = bonusSpeed;
        this.id = id;
    }

    @Override
    public boolean matches(RecipeWrapper recipeWrapper, Level level) {
        return ingredient.test(recipeWrapper.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeWrapper recipeWrapper, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return result.copy();
    }


    public ItemStack getInputItem(RegistryAccess pRegistryAccess) {
        return input.copy();
    }
    public Ingredient getIngredient() {
        return ingredient;
    }
    public float getConsumeChance() {
        return consumeChance;
    }

    public float getBonusSpeed() {
        return bonusSpeed;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CobblemonFarmersRegistery.RecipeRegistry.ENERGY_PYLON_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<EnergyPylonRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "energy_pylon";
    }

    public static class Serializer implements RecipeSerializer<EnergyPylonRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(CobblemonFarmers.MODID, "energy_pylon");

        @Override
        public EnergyPylonRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "result"));
            float consumeChance = GsonHelper.getAsFloat(pSerializedRecipe, "consume_chance");
            float bonusSpeed = GsonHelper.getAsFloat(pSerializedRecipe, "bonus_speed");

            ItemStack inputItem = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "ingredient"));
            Ingredient ingredientItem = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "ingredient"));

            return new EnergyPylonRecipe(ingredientItem, inputItem, consumeChance, bonusSpeed, result, pRecipeId);
        }

        @Override
        public @Nullable EnergyPylonRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient recipeIngredient = Ingredient.fromNetwork(pBuffer);
            float consumeChance = pBuffer.readFloat();
            float bonusSpeed = pBuffer.readFloat();
            ItemStack input = pBuffer.readItem();
            ItemStack result = pBuffer.readItem();

            return new EnergyPylonRecipe(recipeIngredient, input, consumeChance, bonusSpeed,result, pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, EnergyPylonRecipe pRecipe) {
            pRecipe.ingredient.toNetwork(pBuffer);

            pBuffer.writeFloat(pRecipe.getConsumeChance());
            pBuffer.writeFloat(pRecipe.getBonusSpeed());
            pBuffer.writeItemStack(pRecipe.getInputItem(null), false);
            pBuffer.writeItemStack(pRecipe.getResultItem(null), false);
        }
    }
}