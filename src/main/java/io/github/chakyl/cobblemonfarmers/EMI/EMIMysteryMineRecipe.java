package io.github.chakyl.cobblemonfarmers.EMI;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.recipe.MysteryMineRecipe;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.utils.ElementalTypeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static io.github.chakyl.cobblemonfarmers.EMI.CobblemonFarmersEMIPlugin.MYSTERY_MINE;
import static io.github.chakyl.cobblemonfarmers.utils.GuiUtils.emiWordWrap;

public class EMIMysteryMineRecipe implements EmiRecipe {
    public static final ResourceLocation TEXTURE = new ResourceLocation(CobblemonFarmers.MODID, "textures/jei/mystery_mine.png");
    ResourceLocation id;
    int recipeTime;
    Stats speedStat;
    Stats multStat;
    float consumeChance;
    ElementalType elementalType;
    List<EmiIngredient> input;
    List<EmiStack> output;
    List<EmiStack> allOutput;
    List<Integer> weights;
    private int width = 160;
    private int height = 112;


    public EMIMysteryMineRecipe(MysteryMineRecipe recipe) {
        super();

        this.output = new ArrayList<>();
        this.id = recipe.getId();
        this.speedStat = recipe.getSpeedStat();
        this.multStat = recipe.getMultStat();
        this.recipeTime = recipe.getCraftingTime();
        this.elementalType = recipe.getElementalType();
        this.consumeChance = recipe.getConsumeChance();
        this.input = new ArrayList<>();
        this.input.add(EmiIngredient.of(recipe.getIngredient()));
        this.input.add(EmiStack.of(ElementalTypeUtils.getItemFromElementalType(elementalType).getDefaultInstance()));
        this.output = new ArrayList<>();
        this.output.add(EmiStack.of(recipe.getResults(null).get(0)));
        this.allOutput = new ArrayList<>();
        this.weights = recipe.getWeights(null);
        for (ItemStack item : recipe.getResults(null)) {
            this.allOutput.add(EmiStack.of(item));
        }

    }

    @Override
    public EmiRecipeCategory getCategory() {
        return MYSTERY_MINE;
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
        return List.of(EmiStack.of(CobblemonFarmersRegistery.BlockRegistry.MYSTERY_MINE.get()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, this.width, this.height, 0, 0);
        widgets.addSlot(input.get(1), 22, 0).drawBack(false);
        widgets.addSlot(input.get(0), 0, 0).drawBack(false).appendTooltip(Component.translatable("jei.cobblemon_farmers.mystery_mine.consume_chance", Math.round(consumeChance * 100) + "%").withStyle(ChatFormatting.GREEN));
        int row = 0;
        int rowLength = 8;
        for (int i = 0; i < this.allOutput.toArray().length; i++) {
            if (i % rowLength == 0) row++;
            SlotWidget slot = widgets.addSlot(this.allOutput.get(i), (16 * rowLength) + 22 + ((i - (row * rowLength)) * 18), 52 + ((18 * (i / rowLength)))).drawBack(false);

            int weightTotal = 0;
            int currentWeight = this.weights.get(i);
            for (Integer weight : this.weights) weightTotal += weight;
            slot.appendTooltip(Component.translatable("jei.cobblemon_farmers.mystery_mine.chance", Math.round(((double) currentWeight / weightTotal) * 1000) / 10.0 + "%").withStyle(ChatFormatting.GREEN));
            slot.recipeContext(this);

        }
        widgets.addText(Component.translatable("jei.cobblemon_farmers.mystery_mine.elemental_type", elementalType.getDisplayName()), 42, 5, elementalType.getHue(), true);
        emiWordWrap(widgets, Component.translatable("jei.cobblemon_farmers.mystery_mine.speed_stat", this.speedStat.getDisplayName()), 0, 22, 0xFF4b3658, 80, false);
        if (this.multStat != null) {
            emiWordWrap(widgets, Component.translatable("jei.cobblemon_farmers.mystery_mine.mult_stat", this.multStat.getDisplayName()), 81, 22, 0xFF4b3658, 80, false);
        }
        widgets.addText(Component.translatable("jei.cobblemon_farmers.mystery_mine.crafting_time", this.recipeTime / 20), 92, 5, 0xFF4b3658, false);


    }

}
