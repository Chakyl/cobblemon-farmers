package io.github.chakyl.cobblemonfarmers.recipe;


import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.utils.RanchingForage;
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

public class RanchingStationForageRecipe implements Recipe<RecipeWrapper> {
    private final String pokemon;
    private final NonNullList<RanchingForage> forages;
    private final ResourceLocation id;

    public RanchingStationForageRecipe(String pokemon, NonNullList<RanchingForage> forages, ResourceLocation id) {
        this.pokemon = pokemon;
        this.forages = forages;
        this.id = id;
    }

    @Override
    public boolean matches(RecipeWrapper pContainer, Level pLevel) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeWrapper pContainer, RegistryAccess pRegistryAccess) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return null;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CobblemonFarmersRegistery.RecipeRegistry.RANCHING_STATION_FORAGE_SERIALIZER.get();
    }

    public String getPokemon() {
        return pokemon;
    }


    public NonNullList<RanchingForage> getForages(RegistryAccess pRegistryAccess) {
        return this.forages;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<RanchingStationForageRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "ranching_station/forage";
    }

    public static class Serializer implements RecipeSerializer<RanchingStationForageRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(CobblemonFarmers.MODID, "ranching_station/forage");

        @Override
        public RanchingStationForageRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            String pokemon = GsonHelper.getAsString(pSerializedRecipe, "pokemon");
            JsonArray foragesJson = GsonHelper.getAsJsonArray(pSerializedRecipe, "forages");
            NonNullList<RanchingForage> forages = NonNullList.withSize(foragesJson.size(), RanchingForage.getDefaultInstance());

            for (int i = 0; i < foragesJson.size(); i++) {
                JsonObject itemStackJson = new JsonObject();
                JsonObject thisJson = foragesJson.get(i).getAsJsonObject();
                itemStackJson.add("item", thisJson.get("item"));
                itemStackJson.add("count", thisJson.get("count"));
                double chance = 0;
                int minHearts = 0;
                boolean hasQuality = false;
                if (thisJson.has("chance")) chance = thisJson.get("chance").getAsDouble();
                if (thisJson.has("min_hearts")) minHearts = thisJson.get("min_hearts").getAsInt();
                if (thisJson.has("has_quality")) hasQuality = thisJson.get("has_quality").getAsBoolean();
                forages.set(i, new RanchingForage(ShapedRecipe.itemStackFromJson(itemStackJson), chance, hasQuality, minHearts));
            }
            return new RanchingStationForageRecipe(pokemon, forages, pRecipeId);
        }

        @Override
        public @Nullable RanchingStationForageRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            NonNullList<RanchingForage> forages = NonNullList.withSize(pBuffer.readVarInt(), RanchingForage.getDefaultInstance());
            String pokemon = pBuffer.readUtf();
            for (int i = 0; i < forages.size(); i++) {
                double chance = pBuffer.readDouble();
                int minHearts = pBuffer.readInt();
                boolean hasQuality = pBuffer.readBoolean();
                ItemStack item = pBuffer.readItem();
                forages.set(i, new RanchingForage(item, chance, hasQuality, minHearts));
            }
            return new RanchingStationForageRecipe(pokemon, forages, pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, RanchingStationForageRecipe pRecipe) {
            pBuffer.writeUtf(pRecipe.getPokemon());
            pBuffer.writeVarInt(pRecipe.getForages(null).size());
            for (RanchingForage forage : pRecipe.getForages(null)) {
                pBuffer.writeDouble(forage.getChance());
                pBuffer.writeInt(forage.getMinHearts());
                pBuffer.writeBoolean(forage.hasQuality());
                pBuffer.writeItemStack(forage.getItem(), false);
            }
        }
    }
}