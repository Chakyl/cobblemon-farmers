package io.github.chakyl.cobblemonfarmers.recipe;


import com.cobblemon.mod.common.CobblemonItems;
import com.google.gson.JsonObject;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

import static io.github.chakyl.cobblemonfarmers.utils.RanchingStationUtils.*;

public class RanchingStationMilkingRecipe implements Recipe<RecipeWrapper> {
    private final String pokemon;
    private final String form;
    private final boolean consumeBucket;
    private final ItemStack smallMilk;
    private final ItemStack largeMilk;
    private final ResourceLocation id;

    public RanchingStationMilkingRecipe(String pokemon, String form, boolean consumeBucket, ItemStack smallMilk, ItemStack largeMilk, ResourceLocation id) {
        this.pokemon = pokemon;
        this.form = form;
        this.consumeBucket = consumeBucket;
        this.smallMilk = smallMilk;
        this.largeMilk = largeMilk;
        this.id = id;
    }

    @Override
    public boolean matches(RecipeWrapper pContainer, Level pLevel) {
        return ranchingRecipeMatches(pContainer, pokemon, form);
    }

    @Override
    public ItemStack assemble(RecipeWrapper pContainer, RegistryAccess pRegistryAccess) {
        return this.smallMilk.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return this.smallMilk.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CobblemonFarmersRegistery.RecipeRegistry.RANCHING_STATION_MILK_SERIALIZER.get();
    }

    public boolean milkIsSized() {
        return !this.smallMilk.isEmpty() && !this.largeMilk.isEmpty();
    }

    public ItemStack getMilk(int hearts) {
        ItemStack result = ItemStack.EMPTY;
        if (this.smallMilk.isEmpty()) result = this.largeMilk.copy();
        if (this.largeMilk.isEmpty()) result = this.smallMilk.copy();
        if (this.milkIsSized()) {
            if (hearts > 5) {
                result = this.largeMilk.copy();
            } else {
                result = this.smallMilk.copy();
            }
        }
        if (!result.isEmpty()) {
            result = milkIsSized() ? applyMilkQuality(result, hearts) : applyQuality(result, hearts);
        }
        return result;
    }

    public String getPokemon() { return pokemon; }

    public String getForm() { return form; }

    public boolean getIsBucketConsumed() {
        return this.consumeBucket;
    }

    public ItemStack getSmallMilk() {
        return this.smallMilk;
    }

    public ItemStack getLargeMilk() {
        return this.largeMilk;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<RanchingStationMilkingRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "ranching_station/milk";
    }

    public static class Serializer implements RecipeSerializer<RanchingStationMilkingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(CobblemonFarmers.MODID, "ranching_station/milk");

        @Override
        public RanchingStationMilkingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            String pokemon = GsonHelper.getAsString(pSerializedRecipe, "pokemon");
            String form = "";
            if (pSerializedRecipe.has("form")) {
                form = GsonHelper.getAsString(pSerializedRecipe, "form");
            }
            boolean consumeBucket = GsonHelper.getAsBoolean(pSerializedRecipe, "consume_bucket");

            ItemStack smallMilk = ItemStack.EMPTY;
            ItemStack largeMilk = ItemStack.EMPTY;
            if (pSerializedRecipe.has("milk")) {
                JsonObject milkObject = GsonHelper.getAsJsonObject(pSerializedRecipe, "milk");
                if (milkObject.has("sm")) {
                    JsonObject itemStackSmJson = new JsonObject();
                    JsonObject thisJson = milkObject.getAsJsonObject("sm");
                    itemStackSmJson.add("item", thisJson.get("item"));
                    itemStackSmJson.add("count", thisJson.get("count"));
                    smallMilk = ShapedRecipe.itemStackFromJson(itemStackSmJson);
                }
                if (milkObject.has("lg")) {
                    JsonObject itemStackLgJson = new JsonObject();
                    JsonObject thisJson = milkObject.getAsJsonObject("lg");
                    itemStackLgJson.add("item", thisJson.get("item"));
                    itemStackLgJson.add("count", thisJson.get("count"));
                    largeMilk = ShapedRecipe.itemStackFromJson(itemStackLgJson);
                }
            }
            return new RanchingStationMilkingRecipe(pokemon, form, consumeBucket, smallMilk, largeMilk, pRecipeId);
        }

        @Override
        public @Nullable RanchingStationMilkingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            String pokemon = pBuffer.readUtf();
            String form = pBuffer.readUtf();
            boolean consumeBucket = pBuffer.readBoolean();
            ItemStack smallMilk = pBuffer.readItem();
            ItemStack largeMilk = pBuffer.readItem();
            return new RanchingStationMilkingRecipe(pokemon, form, consumeBucket, smallMilk, largeMilk, pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, RanchingStationMilkingRecipe pRecipe) {
            pBuffer.writeUtf(pRecipe.getPokemon());
            pBuffer.writeUtf(pRecipe.getForm());
            pBuffer.writeBoolean(pRecipe.getIsBucketConsumed());
            pBuffer.writeItemStack(pRecipe.getSmallMilk(), false);
            pBuffer.writeItemStack(pRecipe.getLargeMilk(), false);
        }
    }
}