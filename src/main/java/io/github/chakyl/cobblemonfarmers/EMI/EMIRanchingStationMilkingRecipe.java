package io.github.chakyl.cobblemonfarmers.EMI;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Species;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.chakyl.cobbleemibackported.CobblemonStack;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.recipe.RanchingStationMilkingRecipe;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.utils.RanchingForage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.chakyl.cobblemonfarmers.EMI.CobblemonFarmersEMIPlugin.RANCHING_STATION_FORAGES;
import static io.github.chakyl.cobblemonfarmers.EMI.CobblemonFarmersEMIPlugin.RANCHING_STATION_MILKING;

public class EMIRanchingStationMilkingRecipe implements EmiRecipe {
    public static final ResourceLocation TEXTURE = new ResourceLocation(CobblemonFarmers.MODID, "textures/jei/ranching_station_forage.png");
    ResourceLocation id;

    List<EmiIngredient> input;
    List<EmiStack> output;
    List<EmiStack> allOutput;
    private int width = 175;
    private int height = 57;


    public EMIRanchingStationMilkingRecipe(RanchingStationMilkingRecipe recipe) {
        super();

        Species species = Objects.requireNonNull(PokemonSpecies.INSTANCE.getByName(recipe.getPokemon()));
        FormData form = species.getStandardForm();
        CobblemonStack cobblemonStack = new CobblemonStack(form);
        this.output = new ArrayList<>();
        this.id = new ResourceLocation(CobblemonFarmers.MODID, "ranching_station/milk/" + recipe.getPokemon()).withPrefix("/");
        this.input = new ArrayList<>();
        this.input.add(cobblemonStack);
        this.allOutput = new ArrayList<>();
        if (!recipe.getSmallMilk().isEmpty()) this.allOutput.add(EmiStack.of(recipe.getSmallMilk()));
        if (!recipe.getLargeMilk().isEmpty()) this.allOutput.add(EmiStack.of(recipe.getLargeMilk()));
        this.output = new ArrayList<>();
        this.output.add(this.allOutput.get(0));

    }

    @Override
    public EmiRecipeCategory getCategory() {
        return RANCHING_STATION_MILKING;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return input;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return allOutput;
    }

    @Override
    public int getDisplayWidth() {
        return this.width;
    }

    @Override
    public int getDisplayHeight() {
        return this.height;
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return List.of(EmiStack.of(CobblemonFarmersRegistery.BlockRegistry.RANCHING_STATION.get()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, this.width, this.height, 0, 0);
        widgets.addText(this.input.get(0).getEmiStacks().get(0).getName(), 24, 4, 0xFF4b3658, false);
        widgets.addSlot(input.get(0), 0, 0).drawBack(false);
        for (int i = 0; i < this.allOutput.size(); i++) {
            SlotWidget slot = widgets.addSlot(this.allOutput.get(i), i > 0 ? 23 : 5 , 20).drawBack(false);
            if (i == 0) {
                slot.appendTooltip(Component.translatable("jei.cobblemon_farmers.ranching_station.milk.affection.range",this.allOutput.size() > 1 ? "1-5" : "1-10").withStyle(ChatFormatting.LIGHT_PURPLE));
            } else {
                slot.appendTooltip(Component.translatable("jei.cobblemon_farmers.ranching_station.milk.affection.over", "6").withStyle(ChatFormatting.LIGHT_PURPLE));
            }
            slot.recipeContext(this);
        }
        widgets.addText(Component.translatable("jei.cobblemon_farmers.ranching_station.more_info"), 8, 46, 0xFFFFFFFF, false);
    }

}
