package io.github.chakyl.cobblemonfarmers.JEI;

import com.cobblemon.mod.common.api.types.ElementalType;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.recipe.CraftStationRecipe;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.utils.ElementalTypeUtils;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class JEICraftStationCategory implements IRecipeCategory<CraftStationRecipe> {
    public static final RecipeType<CraftStationRecipe> TYPE = RecipeType.create(CobblemonFarmers.MODID, "craft_station", CraftStationRecipe.class);
    public static final ResourceLocation TEXTURE = new ResourceLocation(CobblemonFarmers.MODID, "textures/jei/craft_station.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final Component name;

    private final int displayWidth = 96;
    private final int displayHeight = 60;

    public JEICraftStationCategory(IGuiHelper guiHelper) {
        // Create background matching your EMI display width/height
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, displayWidth, displayHeight);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CobblemonFarmersRegistery.BlockRegistry.CRAFT_STATION.get()));
        this.name = Component.translatable(CobblemonFarmersRegistery.BlockRegistry.CRAFT_STATION.get().getDescriptionId());
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
    public RecipeType<CraftStationRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CraftStationRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 0, 0).addItemStack(ElementalTypeUtils.getItemFromElementalType(recipe.getElementalType()).getDefaultInstance());
        builder.addSlot(RecipeIngredientRole.INPUT, 9, 27).addItemStack(recipe.getInputItem(null));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 69, 27).addItemStack(recipe.getResultItem(null));
    }

    @Override
    public void draw(CraftStationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        ElementalType elementalType = recipe.getElementalType();
        guiGraphics.drawString(Minecraft.getInstance().font,Language.getInstance().getVisualOrder(Component.translatable("jei.cobblemon_farmers.craft_station.elemental_type", elementalType.getDisplayName())), 22, 4, elementalType.getHue(), true);
        guiGraphics.drawString(Minecraft.getInstance().font,  Language.getInstance().getVisualOrder(Component.translatable("jei.cobblemon_farmers.craft_station.crafting_time", recipe.getCraftingTime() / 20)), 8, 49, 0xFFFFFFFF, false);

        if (mouseX >= displayWidth - 16 && mouseX <= displayWidth && mouseY >= 0 && mouseY <= 16) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("info.cobblemon_farmers.craft_station.type." + elementalType.getName()).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("jei.cobblemon_farmers.craft_station.speed_stat", recipe.getSpeedStat().getDisplayName()).withStyle(ChatFormatting.AQUA));
            if (recipe.getMultStat() != null) {
                tooltip.add(Component.translatable("jei.cobblemon_farmers.craft_station.mult_stat", recipe.getMultStat().getDisplayName()).withStyle(ChatFormatting.GREEN));
            }
            guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltip, java.util.Optional.empty(), (int) mouseX, (int) mouseY);
        }
    }
}