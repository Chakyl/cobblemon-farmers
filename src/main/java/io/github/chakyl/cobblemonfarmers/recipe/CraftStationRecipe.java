package io.github.chakyl.cobblemonfarmers.recipe;


import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
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

public class CraftStationRecipe implements Recipe<RecipeWrapper> {
    private final Ingredient ingredient;
    private final ItemStack input;
    private final ItemStack result;
    private final int craftingTime;
    private final ElementalType elementalType;
    private final Stats speedStat;
    private final Stats multStat;
    private final ResourceLocation id;

    public CraftStationRecipe(Ingredient ingredient, ItemStack input, int craftingTime, ElementalType elementalType, Stats speedStat, Stats multStat, ItemStack result, ResourceLocation id) {
        this.ingredient = ingredient;
        this.input = input;
        this.result = result;
        this.craftingTime = craftingTime;
        this.elementalType = elementalType;
        this.speedStat = speedStat;
        this.multStat = multStat;
        this.id = id;
    }

    @Override
    public boolean matches(RecipeWrapper recipeWrapper, Level level) {
        return ingredient.test(recipeWrapper.getItem(0));
    }

    private boolean greaterThanOrEquals(ItemStack self, ItemStack other) {
        if (self.isEmpty()) return other.isEmpty();
        else
            return !other.isEmpty() && self.getCount() <= other.getCount() && self.getItem() == other.getItem() && self.areShareTagsEqual(other);
    }

    @Override
    public ItemStack assemble(RecipeWrapper recipeWrapper, RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    public int getCraftingTime() { return craftingTime; }
    public ElementalType getElementalType() { return elementalType; }
    public Stats getSpeedStat() { return speedStat; }
    public Stats getMultStat() { return multStat; }

    public ItemStack getInputItem(RegistryAccess pRegistryAccess) {
        return input.copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return result.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CobblemonFarmersRegistery.RecipeRegistry.CRAFT_STATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<CraftStationRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "craft_station";
    }

    public static class Serializer implements RecipeSerializer<CraftStationRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(CobblemonFarmers.MODID, "craft_station");

        @Override
        public CraftStationRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "result"));
            int craftingTime = GsonHelper.getAsInt(pSerializedRecipe, "crafting_time");
            ElementalType elementalType = ElementalTypes.INSTANCE.get(GsonHelper.getAsString(pSerializedRecipe, "elemental_type"));

            Stats speedStat = Stats.valueOf(GsonHelper.getAsString(pSerializedRecipe, "speed_stat"));

            Stats multStat = null;
            if (pSerializedRecipe.has("mult_stat")) {
                multStat = Stats.valueOf(GsonHelper.getAsString(pSerializedRecipe, "mult_stat"));
            }
            ItemStack inputItem = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "ingredient"));
            Ingredient ingredientItem = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "ingredient"));
            return new CraftStationRecipe(ingredientItem, inputItem, craftingTime, elementalType, speedStat, multStat, result, pRecipeId);
        }

        @Override
        public @Nullable CraftStationRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient recipeIngredient = Ingredient.fromNetwork(pBuffer);
            int craftingTime = pBuffer.readVarInt();
            ElementalType elementalType = ElementalTypes.INSTANCE.get(pBuffer.readUtf(128));

            Stats speedStat = pBuffer.readEnum(Stats.class);
            Stats multStat = null;
            if (pBuffer.readBoolean()) {
                multStat = pBuffer.readEnum(Stats.class);
            }

            ItemStack input = pBuffer.readItem();
            ItemStack result = pBuffer.readItem();
            return new CraftStationRecipe(recipeIngredient, input, craftingTime, elementalType, speedStat, multStat, result, pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, CraftStationRecipe pRecipe) {
            pRecipe.ingredient.toNetwork(pBuffer);
            pBuffer.writeVarInt(pRecipe.getCraftingTime());
            pBuffer.writeUtf(pRecipe.getElementalType().getName(), 128);
            pBuffer.writeEnum(pRecipe.getSpeedStat());
            pBuffer.writeBoolean(pRecipe.getMultStat() != null);
            if (pRecipe.getMultStat() != null) {
                pBuffer.writeEnum(pRecipe.getMultStat());
            }
            pBuffer.writeItemStack(pRecipe.getInputItem(null), false);
            pBuffer.writeItemStack(pRecipe.getResultItem(null), false);
        }
    }
}