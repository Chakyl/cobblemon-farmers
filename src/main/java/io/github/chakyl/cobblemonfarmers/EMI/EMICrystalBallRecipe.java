package io.github.chakyl.cobblemonfarmers.EMI;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.blockentity.CrystalBallBlockEntity;
import io.github.chakyl.cobblemonfarmers.recipe.CrystalBallRecipe;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.utils.ElementalTypeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static io.github.chakyl.cobblemonfarmers.EMI.CobblemonFarmersEMIPlugin.CRYSTAL_BALL;
import static io.github.chakyl.cobblemonfarmers.utils.GuiUtils.emiWordWrap;

public class EMICrystalBallRecipe implements EmiRecipe {
    public static final ResourceLocation TEXTURE = new ResourceLocation(CobblemonFarmers.MODID, "textures/jei/crystal_ball.png");
    ResourceLocation id;
    Stats speedStat = Stats.SPECIAL_ATTACK;
    Stats aoeStat = Stats.HP;
    ElementalType elementalType = ElementalTypes.INSTANCE.getPSYCHIC();
    List<ElementalType> affectedTypes;
    List<EmiIngredient> input;;
    List<EmiStack> output;
    List<EmiStack> allOutput;
    float bonusChance;
    float consumeChance;
    private int width = 168;
    private int height = 104;


    public EMICrystalBallRecipe(CrystalBallRecipe recipe) {
        super();
        this.id = recipe.getId();
        this.affectedTypes = recipe.getAffectedTypes();
        this.bonusChance = recipe.getBonusChance();
        this.consumeChance = recipe.getConsumeChance();
        this.input = new ArrayList<>();
        this.input.add(EmiStack.of(recipe.getIngredient().getItems()[0]));
        this.input.add(EmiStack.of(ElementalTypeUtils.getItemFromElementalType(elementalType).getDefaultInstance()));
        this.output = new ArrayList<>();
        this.allOutput = new ArrayList<>();
        for (ElementalType type : this.affectedTypes.isEmpty() ? ElementalTypes.INSTANCE.all() : this.affectedTypes) {
            if (this.output.isEmpty()) this.output.add(EmiStack.of(ElementalTypeUtils.getItemFromElementalType(type).getDefaultInstance()));
            this.allOutput.add(EmiStack.of(ElementalTypeUtils.getItemFromElementalType(type).getDefaultInstance()));
        }
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return CRYSTAL_BALL;
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
    public int getDisplayWidth() {
        return this.width;
    }

    @Override
    public int getDisplayHeight() {
        return this.height;
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return List.of(EmiStack.of(CobblemonFarmersRegistery.BlockRegistry.CRYSTAL_BALL.get()));
    }

    @Override
    public List<EmiStack> getOutputs() {
        return allOutput;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, this.width, this.height, 0, 0);
        widgets.addSlot(input.get(0), 8, 26).drawBack(false);
        widgets.addSlot(input.get(1), 0, 0).drawBack(false);
        widgets.addTooltip(
                List.of(ClientTooltipComponent.create(Component.translatable("info.cobblemon_farmers.crystal_ball.type.psychic", this.speedStat.getDisplayName()).withStyle(ChatFormatting.GRAY).getVisualOrderText()),
                        ClientTooltipComponent.create(Component.translatable("jei.cobblemon_farmers.crystal_ball.speed_stat", this.speedStat.getDisplayName()).withStyle(ChatFormatting.AQUA).getVisualOrderText()),
                        ClientTooltipComponent.create(Component.translatable("jei.cobblemon_farmers.crystal_ball.aoe_stat", this.aoeStat.getDisplayName()).withStyle(ChatFormatting.GOLD).getVisualOrderText())
                ),
                168 - 16, 0, 16, 16
        );
        widgets.addText(Component.translatable("jei.cobblemon_farmers.crystal_ball.elemental_type", elementalType.getDisplayName()), 22, 4, elementalType.getHue(), true);
        widgets.addText(Component.translatable("jei.cobblemon_farmers.crystal_ball.bonus_chance", Math.round(this.bonusChance * 100) + "%"), 32, 26, 0xFFFFFFFF, false);
        widgets.addText(Component.translatable("jei.cobblemon_farmers.crystal_ball.consume_chance", Math.round(this.consumeChance * 100) + "%"), 32, 36, 0xFFFFFFFF, false);

        widgets.addText(Component.translatable("jei.cobblemon_farmers.crystal_ball.crafting_time", CrystalBallBlockEntity.CRAFTING_TIME / 20), 112, 55, 0xFF4b3658, false);
        widgets.addText(Component.translatable("jei.cobblemon_farmers.crystal_ball.affected_types"), 2, 55, 0xFF4b3658, false);
        int i = 0;
        int row = 0;
        int rowLength = 9;
        for (ElementalType type : this.affectedTypes.isEmpty() ? ElementalTypes.INSTANCE.all() : this.affectedTypes) {
            if (i % rowLength == 0) row++;
            SlotWidget slot = widgets.addSlot(EmiStack.of(ElementalTypeUtils.getItemFromElementalType(type).getDefaultInstance()), (16 * rowLength) + 21 + ((i - (row * rowLength)) * 18), 66 + ((18 * (i / rowLength)))).drawBack(false);
            i++;
            slot.recipeContext(this);
        }

    }

}
