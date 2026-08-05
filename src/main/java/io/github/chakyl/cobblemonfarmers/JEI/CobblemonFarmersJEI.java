package io.github.chakyl.cobblemonfarmers.JEI;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.recipe.*;
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
        reg.addRecipeCategories(new JEICraftStationCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new JEIMysteryMineCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new JEIRanchingStationForageCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new JEIRanchingStationMilkingCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new JEIGardeningStationCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new JEICrystalBallCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new JEIEnergyPylonCategory(reg.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<CraftStationRecipe> craftStationRecipes = recipeManager.getAllRecipesFor(CraftStationRecipe.Type.INSTANCE);
        registration.addRecipes(JEICraftStationCategory.TYPE, craftStationRecipes);

        List<MysteryMineRecipe> mysteryMineRecipes = recipeManager.getAllRecipesFor(MysteryMineRecipe.Type.INSTANCE);
        registration.addRecipes(JEIMysteryMineCategory.TYPE, mysteryMineRecipes);
        List<CrystalBallRecipe> crystalBallRecipes = recipeManager.getAllRecipesFor(CrystalBallRecipe.Type.INSTANCE);
        registration.addRecipes(JEICrystalBallCategory.TYPE, crystalBallRecipes);
        List<EnergyPylonRecipe> energyPylonRecipes = recipeManager.getAllRecipesFor(EnergyPylonRecipe.Type.INSTANCE);
        registration.addRecipes(JEIEnergyPylonCategory.TYPE, energyPylonRecipes);

        List<RanchingStationForageRecipe> ranchingStationForageRecipes = recipeManager.getAllRecipesFor(RanchingStationForageRecipe.Type.INSTANCE);
        registration.addRecipes(JEIRanchingStationForageCategory.TYPE, ranchingStationForageRecipes);

        List<RanchingStationMilkingRecipe> ranchingStationMilkingRecipes = recipeManager.getAllRecipesFor(RanchingStationMilkingRecipe.Type.INSTANCE);
        registration.addRecipes(JEIRanchingStationMilkingCategory.TYPE, ranchingStationMilkingRecipes);

        // Gardening Station hardcoded recipes
        List<JEIGardeningStationRecipe> gardeningRecipes = new ArrayList<>();
        gardeningRecipes.add(new JEIGardeningStationRecipe(ElementalTypes.INSTANCE.getGRASS(), Stats.SPEED, 24000));
        gardeningRecipes.add(new JEIGardeningStationRecipe(ElementalTypes.INSTANCE.getWATER(), Stats.SPECIAL_ATTACK, 600));
        gardeningRecipes.add(new JEIGardeningStationRecipe(ElementalTypes.INSTANCE.getDARK(), Stats.HP, 20000));
        gardeningRecipes.add(new JEIGardeningStationRecipe(ElementalTypes.INSTANCE.getNORMAL(), Stats.SPEED, 300));
        gardeningRecipes.add(new JEIGardeningStationRecipe(ElementalTypes.INSTANCE.getFLYING(), Stats.SPECIAL_DEFENCE, 200));
        gardeningRecipes.add(new JEIGardeningStationRecipe(ElementalTypes.INSTANCE.getFAIRY(), Stats.SPECIAL_ATTACK, 800));
        registration.addRecipes(JEIGardeningStationCategory.TYPE, gardeningRecipes);
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration reg) {
        reg.addRecipeCatalyst(new ItemStack(CobblemonFarmersRegistery.BlockRegistry.CRAFT_STATION.get()), JEICraftStationCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(CobblemonFarmersRegistery.BlockRegistry.MYSTERY_MINE.get()), JEIMysteryMineCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(CobblemonFarmersRegistery.BlockRegistry.RANCHING_STATION.get()), JEIRanchingStationForageCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(CobblemonFarmersRegistery.BlockRegistry.RANCHING_STATION.get()), JEIRanchingStationMilkingCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(CobblemonFarmersRegistery.BlockRegistry.GARDENING_STATION.get()), JEIGardeningStationCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(CobblemonFarmersRegistery.BlockRegistry.CRYSTAL_BALL.get()), JEICrystalBallCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(CobblemonFarmersRegistery.BlockRegistry.ENERGY_PYLON.get()), JEIEnergyPylonCategory.TYPE);
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