package io.github.chakyl.cobbleworkers.JEI;

import com.cobblemon.mod.common.api.types.ElementalType;
import io.github.chakyl.cobbleworkers.CobbleWorkers;
import io.github.chakyl.cobbleworkers.recipe.MysteryMineRecipe;
import io.github.chakyl.cobbleworkers.registry.CobbleWorkersRegistery;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class MysteryMineCategory implements IRecipeCategory<MysteryMineRecipe> {
    public static final RecipeType<MysteryMineRecipe> TYPE = RecipeType.create(CobbleWorkers.MODID, "mystery_mine", MysteryMineRecipe.class);
    public static final ResourceLocation TEXTURE = new ResourceLocation(CobbleWorkers.MODID, "textures/jei/mystery_mine.png");
    public static final ResourceLocation ELEMENT_TEXTURE = new ResourceLocation("cobblemon:textures/gui/types_small.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final Component name;

    private int ticks = 0;
    private long lastTickTime = 0;

    public MysteryMineCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 160, 112);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CobbleWorkersRegistery.BlockRegistry.MYSTERY_MINE.get()));
        this.name = Component.translatable(CobbleWorkersRegistery.BlockRegistry.MYSTERY_MINE.get().getDescriptionId());
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }


    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public Component getTitle() {
        return this.name;
    }

    @Override
    public RecipeType<MysteryMineRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MysteryMineRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 0, 0).addIngredients(recipe.getIngredient()).addRichTooltipCallback((view, tooltip) -> {
            tooltip.add(Component.translatable("jei.cobble_workers.mystery_mine.consume_chance", Math.round(recipe.getConsumeChance() * 100) + "%").withStyle(ChatFormatting.GREEN));
        });
        int row = 0;
        int rowLength = 8;
        for (int i = 0; i < recipe.getResults(null).toArray().length; i++) {
            if (i % rowLength == 0) row++;
            int finalI1 = i;
            builder.addSlot(RecipeIngredientRole.OUTPUT, (16 * rowLength) + 26 + ((i - (row * rowLength)) * 18), 54 + ((18 * (i / rowLength)))).addItemStack(recipe.getResults(null).get(i)).addRichTooltipCallback((view, tooltip) -> {
                List<Integer> weights = recipe.getWeights(null);
                int weightTotal = 0;
                int currentWeight = weights.get(finalI1);
                for (Integer weight : weights) weightTotal += weight;
                tooltip.add(Component.translatable("jei.cobble_workers.mystery_mine.chance", Math.round(((double) currentWeight / weightTotal) * 1000) / 10.0 + "%").withStyle(ChatFormatting.GREEN));
            });
        }
    }

    @Override
    public void draw(MysteryMineRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        ElementalType elementalType = recipe.getElementalType();
        guiGraphics.blit(ELEMENT_TEXTURE, 20, 0, 0, elementalType.getTextureXMultiplier() * 18, 18, 18, 18, 324, 18);
        guiGraphics.drawString(Minecraft.getInstance().font, Language.getInstance().getVisualOrder(Component.translatable("jei.cobble_workers.craft_station.elemental_type", elementalType.getDisplayName())), 42, 5, elementalType.getHue(), true);
        guiGraphics.drawString(Minecraft.getInstance().font, Language.getInstance().getVisualOrder(Component.translatable("jei.cobble_workers.craft_station.crafting_time", recipe.getCraftingTime() / 20)), 92, 5, 0xFF4b3658, false);

        guiGraphics.drawWordWrap(Minecraft.getInstance().font, FormattedText.of(Component.translatable("jei.cobble_workers.craft_station.speed_stat", recipe.getSpeedStat().getDisplayName()).getString()), 0, 22, 78, 0xFF4b3658);
        if (recipe.getMultStat() != null) {
            guiGraphics.drawWordWrap(Minecraft.getInstance().font, FormattedText.of(Component.translatable("jei.cobble_workers.craft_station.mult_stat", recipe.getMultStat().getDisplayName()).getString()), 82, 22, 78, 0xFF4b3658);
        }
    }

}