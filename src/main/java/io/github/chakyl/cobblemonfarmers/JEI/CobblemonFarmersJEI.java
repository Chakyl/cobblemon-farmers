package io.github.chakyl.cobblemonfarmers.JEI;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.recipe.CraftStationRecipe;
import io.github.chakyl.cobblemonfarmers.recipe.MysteryMineRecipe;
import io.github.chakyl.cobblemonfarmers.recipe.RanchingStationForageRecipe;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;


@JeiPlugin
public class CobblemonFarmersJEI implements IModPlugin {

    public static final ResourceLocation UID = new ResourceLocation(CobblemonFarmers.MODID, "plugin");
    @Override
    public void registerItemSubtypes(ISubtypeRegistration reg) {
        reg.registerSubtypeInterpreter(CobblemonItems.POKEMON_MODEL, new ModelSubtypes());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration reg) {
        reg.addRecipeCategories(new CraftStationCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new MysteryMineCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new RanchingStationForageCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new GardeningStationCategory(reg.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<CraftStationRecipe> craftStationRecipes = recipeManager.getAllRecipesFor(CraftStationRecipe.Type.INSTANCE);
        registration.addRecipes(CraftStationCategory.TYPE, craftStationRecipes);

        List<MysteryMineRecipe> mysteryMineRecipes = recipeManager.getAllRecipesFor(MysteryMineRecipe.Type.INSTANCE);
        registration.addRecipes(MysteryMineCategory.TYPE, mysteryMineRecipes);

        List<RanchingStationForageRecipe> ranchingStationForageRecipes = recipeManager.getAllRecipesFor(RanchingStationForageRecipe.Type.INSTANCE);
        registration.addRecipes(RanchingStationForageCategory.TYPE, ranchingStationForageRecipes);

        // Gardening Station hardcoded recipes
        List<GardeningStationRecipe> gardeningRecipes = new ArrayList<>();
        gardeningRecipes.add(new GardeningStationRecipe(ElementalTypes.INSTANCE.getGRASS(), Stats.SPEED, 24000));
        gardeningRecipes.add(new GardeningStationRecipe(ElementalTypes.INSTANCE.getWATER(), Stats.SPECIAL_ATTACK, 600));
        gardeningRecipes.add(new GardeningStationRecipe(ElementalTypes.INSTANCE.getDARK(), Stats.HP, 20000));
        registration.addRecipes(GardeningStationCategory.TYPE, gardeningRecipes);
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration reg) {
        reg.addRecipeCatalyst(new ItemStack(CobblemonFarmersRegistery.BlockRegistry.CRAFT_STATION.get()), CraftStationCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(CobblemonFarmersRegistery.BlockRegistry.MYSTERY_MINE.get()), MysteryMineCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(CobblemonFarmersRegistery.BlockRegistry.RANCHING_STATION.get()), RanchingStationForageCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(CobblemonFarmersRegistery.BlockRegistry.GARDENING_STATION.get()), GardeningStationCategory.TYPE);
    }

    private static class ModelSubtypes implements IIngredientSubtypeInterpreter<ItemStack> {

        @Override
        public String apply(ItemStack stack, UidContext context) {
            String speciesString = "NULL";
            if (stack.getItem() == CobblemonItems.POKEMON_MODEL) {
                speciesString = stack.getOrCreateTag().getString("species");
            }
            return speciesString;
        }

    }
    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

}