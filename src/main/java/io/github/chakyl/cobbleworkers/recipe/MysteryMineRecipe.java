package io.github.chakyl.cobbleworkers.recipe;


import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.chakyl.cobbleworkers.CobbleWorkers;
import io.github.chakyl.cobbleworkers.registry.CobbleWorkersRegistery;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MysteryMineRecipe implements Recipe<RecipeWrapper> {
    private final Ingredient ingredient;
    private final NonNullList<ItemStack> results;
    private final List<Integer> weights;
    private final int craftingTime;
    private final ElementalType elementalType;
    private final Stats speedStat;
    private final Stats multStat;
    private final float consumeChance;
    private final ResourceLocation id;

    public MysteryMineRecipe(Ingredient ingredient, int craftingTime, ElementalType elementalType, Stats speedStat, Stats multStat, NonNullList<ItemStack> results, List<Integer> weights, float consumeChance, ResourceLocation id) {
        this.ingredient = ingredient;
        this.results = results;
        this.weights = weights;
        this.craftingTime = craftingTime;
        this.elementalType = elementalType;
        this.speedStat = speedStat;
        this.multStat = multStat;
        this.consumeChance = consumeChance;
        this.id = id;
    }

    @Override
    public boolean matches(RecipeWrapper recipeWrapper, Level level) {
        return ingredient.test(recipeWrapper.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeWrapper recipeWrapper, RegistryAccess registryAccess) {
        return results.get(0).copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return results.get(0).copy();
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getCraftingTime() {
        return craftingTime;
    }

    public ElementalType getElementalType() {
        return elementalType;
    }

    public Stats getSpeedStat() {
        return speedStat;
    }

    public Stats getMultStat() {
        return multStat;
    }

    public float getConsumeChance() {
        return consumeChance;
    }

    public NonNullList<ItemStack> getResults(RegistryAccess pRegistryAccess) {
        return results;
    }
    public List<Integer> getWeights(RegistryAccess pRegistryAccess) {
        return weights;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CobbleWorkersRegistery.RecipeRegistry.MYSTERY_MINE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<MysteryMineRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "mystery_mine";
    }

    public static class Serializer implements RecipeSerializer<MysteryMineRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(CobbleWorkers.MODID, "mystery_mine");

        @Override
        public MysteryMineRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            JsonArray resultJson = GsonHelper.getAsJsonArray(pSerializedRecipe, "results");
            NonNullList<ItemStack> results = NonNullList.withSize(resultJson.size(), Items.AIR.getDefaultInstance());
            List<Integer> weights = new ArrayList<>();

            for (int i = 0; i < resultJson.size(); i++) {
                JsonObject itemStackJson = new JsonObject();
                JsonObject thisJson = resultJson.get(i).getAsJsonObject();
                itemStackJson.add("item", thisJson.get("item"));
                itemStackJson.add("count", thisJson.get("count"));
                results.set(i,ShapedRecipe.itemStackFromJson(itemStackJson));
                if (thisJson.has("weight")) {
                    weights.add(thisJson.get("weight").getAsInt());
                } else {
                    weights.add(1);
                }
            }
            int craftingTime = GsonHelper.getAsInt(pSerializedRecipe, "crafting_time");
            ElementalType elementalType = ElementalTypes.INSTANCE.get(GsonHelper.getAsString(pSerializedRecipe, "elemental_type"));

            Stats speedStat = Stats.valueOf(GsonHelper.getAsString(pSerializedRecipe, "speed_stat"));

            Stats multStat = null;
            if (pSerializedRecipe.has("mult_stat")) {
                multStat = Stats.valueOf(GsonHelper.getAsString(pSerializedRecipe, "mult_stat"));
            }
            Ingredient ingredientItem = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "ingredient"));
            float consumeChance = GsonHelper.getAsFloat(pSerializedRecipe, "consume_chance");
            return new MysteryMineRecipe(ingredientItem, craftingTime, elementalType, speedStat, multStat, results, weights, consumeChance, pRecipeId);
        }

        @Override
        public @Nullable MysteryMineRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient recipeIngredient = Ingredient.fromNetwork(pBuffer);
            NonNullList<ItemStack> results = NonNullList.withSize(pBuffer.readVarInt(), Items.AIR.getDefaultInstance());

            List<Integer> weights = new ArrayList<>();

            for (int i = 0; i < results.size(); i++) {
                results.set(i, pBuffer.readItem());
            }
            for (int i = 0; i < results.size(); i++) {
                weights.add(pBuffer.readVarInt());
            }
            int craftingTime = pBuffer.readVarInt();
            ElementalType elementalType = ElementalTypes.INSTANCE.get(pBuffer.readUtf(128));

            Stats speedStat = pBuffer.readEnum(Stats.class);
            Stats multStat = null;
            if (pBuffer.readBoolean()) {
                multStat = pBuffer.readEnum(Stats.class);
            }
            float consumeChance = pBuffer.readFloat();
            return new MysteryMineRecipe(recipeIngredient, craftingTime, elementalType, speedStat, multStat, results, weights, consumeChance, pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, MysteryMineRecipe pRecipe) {
            pRecipe.ingredient.toNetwork(pBuffer);
            for (ItemStack result : pRecipe.getResults(null)) {
                pBuffer.writeItemStack(result, false);
            }
            for (Integer weight : pRecipe.getWeights(null)) {
                pBuffer.writeVarInt(weight);
            }
            pBuffer.writeVarInt(pRecipe.getCraftingTime());
            pBuffer.writeUtf(pRecipe.getElementalType().getName(), 128);
            pBuffer.writeEnum(pRecipe.getSpeedStat());
            pBuffer.writeBoolean(pRecipe.getMultStat() != null);
            if (pRecipe.getMultStat() != null) {
                pBuffer.writeEnum(pRecipe.getMultStat());
            }
            pBuffer.writeFloat(pRecipe.getConsumeChance());
        }
    }
}