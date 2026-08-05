package io.github.chakyl.cobblemonfarmers.JEI;


import com.cobblemon.mod.common.api.types.ElementalType;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
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
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class JEIGardeningStationCategory implements IRecipeCategory<JEIGardeningStationRecipe> {
    public static final RecipeType<JEIGardeningStationRecipe> TYPE = RecipeType.create(CobblemonFarmers.MODID, "gardening_station", JEIGardeningStationRecipe.class);
    public static final ResourceLocation TEXTURE = new ResourceLocation(CobblemonFarmers.MODID, "textures/jei/gardening_station.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final Component name;

    private final int displayWidth = 160;
    private final int displayHeight = 72;

    public JEIGardeningStationCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, displayWidth, displayHeight);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CobblemonFarmersRegistery.BlockRegistry.GARDENING_STATION.get()));
        this.name = Component.translatable(CobblemonFarmersRegistery.BlockRegistry.GARDENING_STATION.get().getDescriptionId());
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
    public RecipeType<JEIGardeningStationRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, JEIGardeningStationRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 0, 0).addItemStack(ElementalTypeUtils.getItemFromElementalType(recipe.type).getDefaultInstance());
    }

    @Override
    public void draw(JEIGardeningStationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        ElementalType elementalType = recipe.type;
        guiGraphics.drawString(Minecraft.getInstance().font,Language.getInstance().getVisualOrder(Component.translatable("jei.cobblemon_farmers.gardening_station.elemental_type", elementalType.getDisplayName())),22, 4, elementalType.getHue(), true);
        guiGraphics.drawString(Minecraft.getInstance().font,Language.getInstance().getVisualOrder(Component.translatable("jei.cobblemon_farmers.gardening_station.action_time", recipe.actionTime / 20)),100, 22, 0xFF4b3658, false);
        guiGraphics.drawWordWrap(Minecraft.getInstance().font, FormattedText.of(Component.translatable("jei.cobblemon_farmers.gardening_station.type." + elementalType.getName()).getString()), 6, 38, 152, 0xFFFFFFFF);
        if (mouseX >= displayWidth - 16 && mouseX <= displayWidth && mouseY >= 0 && mouseY <= 16) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("jei.cobblemon_farmers.gardening_station.speed_stat", recipe.speedStat.getDisplayName()).withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.translatable("jei.cobblemon_farmers.gardening_station.level_scaling." + elementalType.getName()).withStyle(ChatFormatting.GOLD));

            guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltip, java.util.Optional.empty(), (int) mouseX, (int) mouseY);
        }
    }
}