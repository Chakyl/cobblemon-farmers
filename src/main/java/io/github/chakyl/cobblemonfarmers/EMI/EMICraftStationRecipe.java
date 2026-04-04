package io.github.chakyl.cobblemonfarmers.EMI;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.recipe.CraftStationRecipe;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.utils.ElementalTypeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static io.github.chakyl.cobblemonfarmers.EMI.CobblemonFarmersEMIPlugin.CRAFT_STATION;
import static io.github.chakyl.cobblemonfarmers.EMI.CobblemonFarmersEMIPlugin.GARDENING_STATION;
import static io.github.chakyl.cobblemonfarmers.utils.GuiUtils.emiWordWrap;

public class EMICraftStationRecipe implements EmiRecipe {
    public static final ResourceLocation TEXTURE = new ResourceLocation(CobblemonFarmers.MODID, "textures/jei/craft_station.png");
    ResourceLocation id;
    int recipeTime;
    Stats speedStat;
    Stats multStat;
    ElementalType elementalType;
    List<EmiIngredient> input;
    List<EmiStack> output;
    List<EmiStack> allOutput;
    private int width = 168;
    private int height = 64;


    public EMICraftStationRecipe(CraftStationRecipe recipe) {
        super();

        this.output = new ArrayList<>();
        this.id = recipe.getId();
        this.speedStat = recipe.getSpeedStat();
        this.multStat = recipe.getMultStat();
        this.recipeTime = recipe.getCraftingTime();
        this.elementalType = recipe.getElementalType();
        this.input = new ArrayList<>();
        this.input.add(EmiStack.of(recipe.getInputItem(null)));
        this.input.add(EmiStack.of(ElementalTypeUtils.getItemFromElementalType(elementalType).getDefaultInstance()));
        this.output = new ArrayList<>();
        this.output.add(EmiStack.of(recipe.getResultItem(null)));
        this.allOutput = new ArrayList<>(this.output);

    }

    @Override
    public EmiRecipeCategory getCategory() {
        return CRAFT_STATION;
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
        return List.of(EmiStack.of(CobblemonFarmersRegistery.BlockRegistry.CRAFT_STATION.get()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, this.width, this.height, 0, 0);
        widgets.addSlot(input.get(0), 89, 8).drawBack(false);
        widgets.addSlot(input.get(1), 0, 0).drawBack(false);
        widgets.addSlot(output.get(0), 141, 8).drawBack(false).recipeContext(this);
        widgets.addText(Component.translatable("jei.cobblemon_farmers.craft_station.elemental_type", elementalType.getDisplayName()), 22, 4, elementalType.getHue(), true);
        emiWordWrap(widgets, Component.translatable("jei.cobblemon_farmers.craft_station.speed_stat", this.speedStat.getDisplayName()), 0, 22, 0xFF4b3658, 84, false);
        if (this.multStat != null) {
            emiWordWrap(widgets, Component.translatable("jei.cobblemon_farmers.craft_station.mult_stat", this.multStat.getDisplayName()), 0, 44, 0xFF4b3658, 84, false);
        }
        widgets.addText(Component.translatable("jei.cobblemon_farmers.craft_station.crafting_time", this.recipeTime / 20), 86, 36, 0xFFFFFFFF, true);


    }

}
