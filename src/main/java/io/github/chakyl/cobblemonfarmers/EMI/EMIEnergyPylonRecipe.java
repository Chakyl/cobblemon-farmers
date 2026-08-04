package io.github.chakyl.cobblemonfarmers.EMI;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.blockentity.CrystalBallBlockEntity;
import io.github.chakyl.cobblemonfarmers.recipe.EnergyPylonRecipe;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.utils.ElementalTypeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static io.github.chakyl.cobblemonfarmers.EMI.CobblemonFarmersEMIPlugin.ENERGY_PYLON;

public class EMIEnergyPylonRecipe implements EmiRecipe {
    public static final ResourceLocation TEXTURE = new ResourceLocation(CobblemonFarmers.MODID, "textures/jei/energy_pylon.png");
    ResourceLocation id;
    int recipeTime;
    Stats speedStat = Stats.SPECIAL_ATTACK;
    Stats aoeStat = Stats.HP;
    ElementalType elementalType = ElementalTypes.INSTANCE.getELECTRIC();
    List<EmiIngredient> input;
    List<EmiStack> output;
    List<EmiStack> allOutput;
    double bonusSpeed;
    float consumeChance;
    private int width = 128;
    private int height = 85;


    public EMIEnergyPylonRecipe(EnergyPylonRecipe recipe) {
        super();

        this.output = new ArrayList<>();
        this.id = recipe.getId();
        this.bonusSpeed = recipe.getBonusSpeed();
        this.consumeChance = recipe.getConsumeChance();
        this.input = new ArrayList<>();
        this.input.add(EmiStack.of(recipe.getInputItem(null)));
        this.input.add(EmiStack.of(ElementalTypeUtils.getItemFromElementalType(elementalType).getDefaultInstance()));
        this.output = new ArrayList<>();
        this.output.add(EmiStack.of(recipe.getResultItem(null)));
        this.allOutput = new ArrayList<>(this.output);

    }

    @Override
    public EmiRecipeCategory getCategory() {
        return ENERGY_PYLON;
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
        return List.of(EmiStack.of(CobblemonFarmersRegistery.BlockRegistry.ENERGY_PYLON.get()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, this.width, this.height, 0, 0);
        widgets.addSlot(input.get(0), 25, 34).drawBack(false);
        widgets.addSlot(input.get(1), 0, 0).drawBack(false);
        widgets.addSlot(output.get(0), 85, 34).drawBack(false).recipeContext(this);
        widgets.addText(Component.translatable("jei.cobblemon_farmers.energy_pylon.elemental_type", elementalType.getDisplayName()), 22, 4, elementalType.getHue(), true);
        widgets.addTooltip(
                List.of(ClientTooltipComponent.create(Component.translatable("info.cobblemon_farmers.energy_pylon.type.electric", this.speedStat.getDisplayName()).withStyle(ChatFormatting.GRAY).getVisualOrderText()),
                        ClientTooltipComponent.create(Component.translatable("jei.cobblemon_farmers.energy_pylon.speed_stat", this.speedStat.getDisplayName()).withStyle(ChatFormatting.AQUA).getVisualOrderText()),
                        ClientTooltipComponent.create(Component.translatable("jei.cobblemon_farmers.energy_pylon.aoe_stat", this.aoeStat.getDisplayName()).withStyle(ChatFormatting.GOLD).getVisualOrderText())
                ),
                128 - 16, 0, 16, 16
        );
        widgets.addText(Component.translatable("jei.cobblemon_farmers.energy_pylon.bonus_speed", this.bonusSpeed), 6, 58, 0xFFFFFFFF, false);
        widgets.addText(Component.translatable("jei.cobblemon_farmers.energy_pylon.consume_chance", Math.round(this.consumeChance * 100) + "%"), 6, 68, 0xFFFFFFFF, false);

        widgets.addText(Component.translatable("jei.cobblemon_farmers.energy_pylon.crafting_time", CrystalBallBlockEntity.CRAFTING_TIME  / 20), 72, 17, 0xFF4b3658, false);


    }

}
