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
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
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
    private int width = 160;
    private int height = 72;


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
        emiWordWrap(widgets, Component.translatable("jei.cobblemon_farmers.gardening_station.type." + elementalType.getName()), 6, 38, 0xFFFFFFFF, 152, false);
        widgets.addText(Component.translatable("jei.cobblemon_farmers.gardening_station.elemental_type", elementalType.getDisplayName()), 22, 4, elementalType.getHue(), true);
        widgets.addText(Component.translatable("jei.cobblemon_farmers.gardening_station.action_time", this.recipeTime / 20), 100, 22, 0xFF4b3658, false);
        widgets.addTooltip(
                List.of(ClientTooltipComponent.create(Component.translatable("jei.cobblemon_farmers.gardening_station.speed_stat", this.stat.getDisplayName()).withStyle(ChatFormatting.AQUA).getVisualOrderText()),
                        ClientTooltipComponent.create(Component.translatable("jei.cobblemon_farmers.gardening_station.level_scaling." + elementalType.getName()).withStyle(ChatFormatting.GOLD).getVisualOrderText())
                ),
                160 - 16, 0, 16, 16
        );
    }

}
