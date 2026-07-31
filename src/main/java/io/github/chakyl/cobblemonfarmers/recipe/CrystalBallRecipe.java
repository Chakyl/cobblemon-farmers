package io.github.chakyl.cobblemonfarmers.recipe;


import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CrystalBallRecipe implements Recipe<RecipeWrapper> {
    private final Ingredient ingredient;
    private final List<ElementalType> affectedTypes;
    private final float consumeChance;
    private final float bonusChance;
    private final ResourceLocation id;

    public CrystalBallRecipe(Ingredient ingredient, List<ElementalType> affectedTypes, float consumeChance, float bonusChance, ResourceLocation id) {
        this.ingredient = ingredient;
        this.affectedTypes = affectedTypes;
        this.consumeChance = consumeChance;
        this.bonusChance = bonusChance;
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
        return ItemStack.EMPTY;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public List<ElementalType> getAffectedTypes() {
        return affectedTypes;
    }

    public boolean canAffectType(ElementalType type) {
        return affectedTypes.isEmpty() || affectedTypes.contains(type);
    }
    public float getConsumeChance() {
        return consumeChance;
    }

    public float getBonusChance() {
        return bonusChance;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CobblemonFarmersRegistery.RecipeRegistry.CRYSTAL_BALL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<CrystalBallRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "crystal_ball";
    }

    public static class Serializer implements RecipeSerializer<CrystalBallRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(CobblemonFarmers.MODID, "crystal_ball");

        @Override
        public CrystalBallRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            Ingredient ingredientItem = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "ingredient"));

            JsonArray typesArray = GsonHelper.getAsJsonArray(pSerializedRecipe, "affected_types");
            List<ElementalType> elementalTypes = new ArrayList<>();
            for (int i = 0; i < typesArray.size(); i++) {
                elementalTypes.add(ElementalTypes.INSTANCE.get(typesArray.get(i).getAsString()));
            }

            float consumeChance = GsonHelper.getAsFloat(pSerializedRecipe, "consume_chance");
            float bonusChance = GsonHelper.getAsFloat(pSerializedRecipe, "bonus_chance");

            return new CrystalBallRecipe(ingredientItem, elementalTypes, consumeChance, bonusChance, pRecipeId);
        }

        @Override
        public @Nullable CrystalBallRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient recipeIngredient = Ingredient.fromNetwork(pBuffer);

            int size = pBuffer.readVarInt();
            List<ElementalType> elementalTypes = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                elementalTypes.add(ElementalTypes.INSTANCE.get(pBuffer.readUtf(128)));
            }

            float consumeChance = pBuffer.readFloat();
            float bonusChance = pBuffer.readFloat();

            return new CrystalBallRecipe(recipeIngredient, elementalTypes, consumeChance, bonusChance, pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, CrystalBallRecipe pRecipe) {
            pRecipe.ingredient.toNetwork(pBuffer);

            pBuffer.writeVarInt(pRecipe.getAffectedTypes().size());
            for (ElementalType type : pRecipe.getAffectedTypes()) {
                pBuffer.writeUtf(type.getName(), 128);
            }

            pBuffer.writeFloat(pRecipe.getConsumeChance());
            pBuffer.writeFloat(pRecipe.getBonusChance());
        }
    }
}