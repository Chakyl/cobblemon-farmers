package io.github.chakyl.cobblemonfarmers.EMI;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.chakyl.cobbleemibackported.CobblemonStack;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.recipe.RanchingStationForageRecipe;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.utils.RanchingForage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;

import static io.github.chakyl.cobblemonfarmers.EMI.CobblemonFarmersEMIPlugin.RANCHING_STATION_FORAGES;

public class EMIRanchingStationForageRecipe implements EmiRecipe {
    public static final ResourceLocation TEXTURE = new ResourceLocation(CobblemonFarmers.MODID, "textures/jei/ranching_station_forage.png");
    ResourceLocation id;

    List<EmiIngredient> input;
    NonNullList<RanchingForage> forages;
    List<EmiStack> output;
    List<EmiStack> allOutput;
    private int width = 175;
    private int height = 57;


    public EMIRanchingStationForageRecipe(RanchingStationForageRecipe recipe) {
        super();
        Species species = Objects.requireNonNull(PokemonSpecies.INSTANCE.getByName(recipe.getPokemon()));
        CobblemonStack cobblemonStack;
        if ((recipe.getForm()).isEmpty()) {
            cobblemonStack = new CobblemonStack(species);
        } else {
            Set<String> recipeAspects = new HashSet<>();
            recipeAspects.add(recipe.getForm());
            cobblemonStack = new CobblemonStack(species, recipeAspects);
        }
        this.output = new ArrayList<>();
        this.id = new ResourceLocation(CobblemonFarmers.MODID, "ranching_station/forage/" + recipe.getPokemon() + (recipe.getForm().isEmpty() ? "" : "_" + recipe.getForm())).withPrefix("/");
        this.input = new ArrayList<>();
        this.input.add(cobblemonStack);
        this.output = new ArrayList<>();
        this.forages = recipe.getForages(null);
        this.output.add(EmiStack.of(recipe.getForages(null).get(0).getItem()));
        this.allOutput = new ArrayList<>();
        for (RanchingForage forage : this.forages) {
            this.allOutput.add(EmiStack.of(forage.getItem()));
        }

    }

    @Override
    public EmiRecipeCategory getCategory() {
        return RANCHING_STATION_FORAGES;
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
        int row = 0;
        int rowLength = 9;
        for (int i = 0; i < this.forages.toArray().length; i++) {
            if (i % rowLength == 0) row++;
            RanchingForage forage = this.forages.get(i);
            SlotWidget slot = widgets.addSlot(EmiStack.of(forage.getItem()), (16 * rowLength) + 23 + ((i - (row * rowLength)) * 18), 20 + ((18 * (i / rowLength)))).drawBack(false)
                    .appendTooltip(Component.translatable("jei.cobblemon_farmers.ranching_station.forage.affection.over", forage.getMinHearts()).withStyle(ChatFormatting.LIGHT_PURPLE))
                    .appendTooltip(Component.translatable("jei.cobblemon_farmers.ranching_station.forage.chance", Math.round(forage.getChance() * 1000) / 10.0 + "%").withStyle(ChatFormatting.GOLD));
            if (forage.hasQuality())
                slot.appendTooltip(Component.translatable("jei.cobblemon_farmers.ranching_station.forage.affection.quality").withStyle(ChatFormatting.GREEN));
            slot.recipeContext(this);
        }
        if (this.forages.size() <= 9)
            widgets.addText(Component.translatable("jei.cobblemon_farmers.ranching_station.more_info"), 8, 46, 0xFFFFFFFF, false);
    }

}
