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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CraftStationCategory implements IRecipeCategory<CraftStationRecipe> {
    public static final RecipeType<CraftStationRecipe> TYPE = RecipeType.create(CobblemonFarmers.MODID, "craft_station", CraftStationRecipe.class);
    public static final ResourceLocation TEXTURE = new ResourceLocation(CobblemonFarmers.MODID, "textures/jei/craft_station.png");
    public static final ResourceLocation ELEMENT_TEXTURE = new ResourceLocation("cobblemon:textures/gui/types_small.png");
    private final IDrawable background;
    private final IDrawable icon;
    private final Component name;

    private int ticks = 0;
    private long lastTickTime = 0;

    public CraftStationCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 168, 64);
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
        builder.addSlot(RecipeIngredientRole.INPUT, 90, 9).addItemStack(recipe.getInputItem(null));
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 0).addItemStack(ElementalTypeUtils.getItemFromElementalType(recipe.getElementalType()).getDefaultInstance());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 142, 9).addItemStack(recipe.getResultItem(null));
    }

    @Override
    public void draw(CraftStationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        ElementalType elementalType = recipe.getElementalType();
//        guiGraphics.blit(ELEMENT_TEXTURE, 0, 0, 0, elementalType.getTextureXMultiplier() * 18, 18, 18, 18, 324, 18);
        guiGraphics.drawString(Minecraft.getInstance().font, Language.getInstance().getVisualOrder(Component.translatable("jei.cobblemon_farmers.craft_station.elemental_type", elementalType.getDisplayName())), 22, 4, elementalType.getHue(), true);
        guiGraphics.drawWordWrap(Minecraft.getInstance().font, FormattedText.of(Component.translatable("jei.cobblemon_farmers.craft_station.speed_stat", recipe.getSpeedStat().getDisplayName()).getString()), 0, 22, 84, 0xFF4b3658);
        if (recipe.getMultStat() != null) {
            guiGraphics.drawWordWrap(Minecraft.getInstance().font, FormattedText.of(Component.translatable("jei.cobblemon_farmers.craft_station.mult_stat", recipe.getMultStat().getDisplayName()).getString()), 0, 44, 84, 0xFF4b3658);
        }
        guiGraphics.drawString(Minecraft.getInstance().font, Language.getInstance().getVisualOrder(Component.translatable("jei.cobblemon_farmers.craft_station.crafting_time", recipe.getCraftingTime() / 20)), 86, 36, 0xFFFFFFFF, false);
    }

}