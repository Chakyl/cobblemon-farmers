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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class GardeningStationCategory implements IRecipeCategory<GardeningStationRecipe> {
    public static final RecipeType<GardeningStationRecipe> TYPE = RecipeType.create(CobblemonFarmers.MODID, "gardening_station", GardeningStationRecipe.class);
    public static final ResourceLocation TEXTURE = new ResourceLocation(CobblemonFarmers.MODID, "textures/jei/gardening_station.png");
    private final IDrawable background;
    private final IDrawable icon;
    private final Component name;

    private int ticks = 0;
    private long lastTickTime = 0;

    public GardeningStationCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 168, 84);
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
    public RecipeType<GardeningStationRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GardeningStationRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 0).addItemStack(ElementalTypeUtils.getItemFromElementalType(recipe.type).getDefaultInstance());
    }

    @Override
    public void draw(GardeningStationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        ElementalType elementalType = recipe.type;
//        guiGraphics.blit(ELEMENT_TEXTURE, 0, 0, 0, elementalType.getTextureXMultiplier() * 18, 18, 18, 18, 324, 18);
        guiGraphics.drawWordWrap(Minecraft.getInstance().font, FormattedText.of(Component.translatable("info.cobblemon_farmers.gardening_station.type." + elementalType.getName()).getString()), 86, 6, 84, 0xFFFFFFFF);
        guiGraphics.drawString(Minecraft.getInstance().font, Language.getInstance().getVisualOrder(Component.translatable("jei.cobblemon_farmers.gardening_station.elemental_type", elementalType.getDisplayName())), 22, 4, elementalType.getHue(), true);
        guiGraphics.drawWordWrap(Minecraft.getInstance().font, FormattedText.of(Component.translatable("jei.cobblemon_farmers.gardening_station.speed_stat", recipe.speedStat.getDisplayName()).getString()), 0, 22, 84, 0xFF4b3658);
        guiGraphics.drawString(Minecraft.getInstance().font, Language.getInstance().getVisualOrder(Component.translatable("jei.cobblemon_farmers.gardening_station.action_time", recipe.actionTime / 20)), 86, 32, 0xFFFFFFFF, false);
        guiGraphics.drawWordWrap(Minecraft.getInstance().font, FormattedText.of(Component.translatable("jei.cobblemon_farmers.gardening_station.level_scaling." + elementalType.getName()).getString()), 0, 54, 156, 0xFF4b3658);
    }

}