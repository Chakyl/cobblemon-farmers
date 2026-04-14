package io.github.chakyl.cobblemonfarmers.EMI;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import io.github.chakyl.cobblemonfarmers.recipe.CraftStationRecipe;
import io.github.chakyl.cobblemonfarmers.recipe.MysteryMineRecipe;
import io.github.chakyl.cobblemonfarmers.recipe.RanchingStationForageRecipe;
import io.github.chakyl.cobblemonfarmers.recipe.RanchingStationMilkingRecipe;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@EmiEntrypoint
public class CobblemonFarmersEMIPlugin implements EmiPlugin {
    public static final EmiRecipeCategory CRAFT_STATION = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath("cobblemon_farmers", "emi/craft_station"), EmiStack.of(CobblemonFarmersRegistery.BlockRegistry.CRAFT_STATION.get()));
    public static final EmiRecipeCategory GARDENING_STATION = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath("cobblemon_farmers", "emi/gardening_station"), EmiStack.of(CobblemonFarmersRegistery.BlockRegistry.GARDENING_STATION.get()));
    public static final EmiRecipeCategory MYSTERY_MINE = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath("cobblemon_farmers", "emi/mystery_mine"), EmiStack.of(CobblemonFarmersRegistery.BlockRegistry.MYSTERY_MINE.get()));
    public static final EmiRecipeCategory RANCHING_STATION_FORAGES = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath("cobblemon_farmers", "emi/ranching_station_forages"), EmiStack.of(CobblemonFarmersRegistery.BlockRegistry.RANCHING_STATION.get()));
    public static final EmiRecipeCategory RANCHING_STATION_MILKING = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath("cobblemon_farmers", "emi/ranching_station_milking"), EmiStack.of(CobblemonFarmersRegistery.BlockRegistry.RANCHING_STATION.get()));

    @Override
    public void register(EmiRegistry registry) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        registry.addCategory(CRAFT_STATION);
        registry.addCategory(GARDENING_STATION);
        registry.addCategory(MYSTERY_MINE);
        registry.addCategory(RANCHING_STATION_FORAGES);
        registry.addCategory(RANCHING_STATION_MILKING);
        List<CraftStationRecipe> craftStationRecipes = recipeManager.getAllRecipesFor(CraftStationRecipe.Type.INSTANCE);
        for (CraftStationRecipe recipe : craftStationRecipes) {
            registry.addRecipe(new EMICraftStationRecipe(recipe));
        }
        List<MysteryMineRecipe> mysteryMineRecipes = recipeManager.getAllRecipesFor(MysteryMineRecipe.Type.INSTANCE);
        for (MysteryMineRecipe recipe : mysteryMineRecipes) {
            registry.addRecipe(new EMIMysteryMineRecipe(recipe));
        }
        registry.addRecipe(new EMIGardeningStationRecipe(ElementalTypes.INSTANCE.getGRASS(), Stats.SPEED, 24000));
        registry.addRecipe(new EMIGardeningStationRecipe(ElementalTypes.INSTANCE.getWATER(), Stats.SPECIAL_ATTACK, 600));
        registry.addRecipe(new EMIGardeningStationRecipe(ElementalTypes.INSTANCE.getDARK(), Stats.HP, 20000));
        registry.addRecipe(new EMIGardeningStationRecipe(ElementalTypes.INSTANCE.getNORMAL(), Stats.SPEED, 300));
        registry.addRecipe(new EMIGardeningStationRecipe(ElementalTypes.INSTANCE.getFAIRY(), Stats.SPECIAL_ATTACK, 800));
        registry.addRecipe(new EMIGardeningStationRecipe(ElementalTypes.INSTANCE.getFLYING(), Stats.SPECIAL_DEFENCE, 200));

        List<RanchingStationForageRecipe> ranchingStationForageRecipes = recipeManager.getAllRecipesFor(RanchingStationForageRecipe.Type.INSTANCE);
        for (RanchingStationForageRecipe recipe : ranchingStationForageRecipes) {
            registry.addRecipe(new EMIRanchingStationForageRecipe(recipe));
        }
        List<RanchingStationMilkingRecipe> ranchingStationMilkingRecipes = recipeManager.getAllRecipesFor(RanchingStationMilkingRecipe.Type.INSTANCE);
        for (RanchingStationMilkingRecipe recipe : ranchingStationMilkingRecipes) {
            registry.addRecipe(new EMIRanchingStationMilkingRecipe(recipe));
        }
    }
}
