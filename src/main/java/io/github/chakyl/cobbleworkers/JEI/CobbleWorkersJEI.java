package io.github.chakyl.cobbleworkers.JEI;

import io.github.chakyl.cobbleworkers.CobbleWorkers;
import io.github.chakyl.cobbleworkers.recipe.CraftStationRecipe;
import io.github.chakyl.cobbleworkers.recipe.MysteryMineRecipe;
import io.github.chakyl.cobbleworkers.registry.CobbleWorkersRegistery;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
        import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;


@JeiPlugin
public class CobbleWorkersJEI implements IModPlugin {

    public static final ResourceLocation UID = new ResourceLocation(CobbleWorkers.MODID, "plugin");

    @Override
    public void registerCategories(IRecipeCategoryRegistration reg) {
        reg.addRecipeCategories(new CraftStationCategory(reg.getJeiHelpers().getGuiHelper()));
        reg.addRecipeCategories(new MysteryMineCategory(reg.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<CraftStationRecipe> craftStationRecipes = recipeManager.getAllRecipesFor(CraftStationRecipe.Type.INSTANCE);
        registration.addRecipes(CraftStationCategory.TYPE, craftStationRecipes);

        List<MysteryMineRecipe> mysteryMineRecipes = recipeManager.getAllRecipesFor(MysteryMineRecipe.Type.INSTANCE);
        registration.addRecipes(MysteryMineCategory.TYPE, mysteryMineRecipes);
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration reg) {
        reg.addRecipeCatalyst(new ItemStack(CobbleWorkersRegistery.BlockRegistry.CRAFT_STATION.get()), CraftStationCategory.TYPE);
        reg.addRecipeCatalyst(new ItemStack(CobbleWorkersRegistery.BlockRegistry.MYSTERY_MINE.get()), MysteryMineCategory.TYPE);
    }

//    @Override
//    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
//        registration.addRecipeClickArea(PlortPressScreen.class, 80, 26, 20, 30,
//                PlortPressingCategory.TYPE);
//    }

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

}