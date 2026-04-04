package io.github.chakyl.cobblemonfarmers.EMI;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.utils.ElementalTypeUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static io.github.chakyl.cobblemonfarmers.EMI.CobblemonFarmersEMIPlugin.GARDENING_STATION;
import static io.github.chakyl.cobblemonfarmers.utils.GuiUtils.emiWordWrap;

public class EMIGardeningStationRecipe implements EmiRecipe {
    public static final ResourceLocation TEXTURE = new ResourceLocation(CobblemonFarmers.MODID, "textures/jei/gardening_station.png");
    ResourceLocation id;
    int recipeTime;
    Stats stat;
    ElementalType elementalType;
    List<EmiIngredient> input;
    List<EmiStack> output;
    List<EmiStack> allOutput;
    private int width = 168;
    private int height = 84;


    public EMIGardeningStationRecipe(ElementalType elementalType, Stats stat, int recipeTime) {
        super();

        this.output = new ArrayList<>();
        this.id = new ResourceLocation(CobblemonFarmers.MODID, "gardening_station/" + elementalType.getTextureXMultiplier()).withPrefix("/");
        this.stat = stat;
        this.recipeTime = recipeTime;
        this.elementalType = elementalType;
        this.input = new ArrayList<>();
        this.input.add(EmiStack.of(ElementalTypeUtils.getItemFromElementalType(elementalType).getDefaultInstance()));
        this.allOutput = new ArrayList<>();
        this.output = new ArrayList<>();

    }

    @Override
    public EmiRecipeCategory getCategory() {
        return GARDENING_STATION;
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
        return List.of(EmiStack.of(CobblemonFarmersRegistery.BlockRegistry.GARDENING_STATION.get()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, this.width, this.height, 0, 0);
        widgets.addSlot(input.get(0), 0, 0).drawBack(false);
        emiWordWrap(widgets, Component.translatable("info.cobblemon_farmers.gardening_station.type." + elementalType.getName()), 86, 6, 0xFFFFFFFF, 84, false);
        widgets.addText(Component.translatable("jei.cobblemon_farmers.gardening_station.elemental_type", elementalType.getDisplayName()), 22, 4, elementalType.getHue(), true);
        emiWordWrap(widgets, Component.translatable("jei.cobblemon_farmers.gardening_station.speed_stat", this.stat.getDisplayName()), 0, 22, 0xFF4b3658, 84, false);
        widgets.addText(Component.translatable("jei.cobblemon_farmers.gardening_station.action_time", this.recipeTime / 20), 86, 32, 0xFFFFFFFF, false);
        emiWordWrap(widgets, Component.translatable("jei.cobblemon_farmers.gardening_station.level_scaling." + elementalType.getName()), 0, 54, 0xFF4b3658, 156, false);
    }

}
