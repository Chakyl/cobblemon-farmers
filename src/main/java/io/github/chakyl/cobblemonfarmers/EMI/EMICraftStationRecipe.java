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
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static io.github.chakyl.cobblemonfarmers.EMI.CobblemonFarmersEMIPlugin.CRAFT_STATION;

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
    private int width = 96;
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
        List<ClientTooltipComponent> tooltipComponents = new ArrayList<>(List.of(
                ClientTooltipComponent.create(Component.translatable("info.cobblemon_farmers.craft_station.type." + elementalType.getDisplayName(), this.speedStat.getDisplayName()).withStyle(ChatFormatting.GRAY).getVisualOrderText()),
                ClientTooltipComponent.create(Component.translatable("jei.cobblemon_farmers.craft_station.speed_stat", this.speedStat.getDisplayName()).withStyle(ChatFormatting.AQUA).getVisualOrderText())
        ));

        if (this.multStat != null) {
            tooltipComponents.add(ClientTooltipComponent.create(Component.translatable("jei.cobblemon_farmers.craft_station.mult_stat", this.multStat.getDisplayName()).withStyle(ChatFormatting.GREEN).getVisualOrderText()));
        }
        widgets.addTooltip(tooltipComponents, 0, 0, 16, 16);
        widgets.addText(Component.translatable("jei.cobblemon_farmers.craft_station.crafting_time", this.recipeTime / 20), 86, 36, 0xFFFFFFFF, true);


    }

}
