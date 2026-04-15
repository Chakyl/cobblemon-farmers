package io.github.chakyl.cobblemonfarmers.JEI;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.recipe.RanchingStationMilkingRecipe;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
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

import java.util.Objects;

public class RanchingStationMilkingCategory implements IRecipeCategory<RanchingStationMilkingRecipe> {
    public static final RecipeType<RanchingStationMilkingRecipe> TYPE = RecipeType.create(CobblemonFarmers.MODID, "ranching_station/milk", RanchingStationMilkingRecipe.class);
    public static final ResourceLocation TEXTURE = new ResourceLocation(CobblemonFarmers.MODID, "textures/jei/ranching_station_forage.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final Component name;

    private int ticks = 0;
    private long lastTickTime = 0;

    public RanchingStationMilkingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 175, 57);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CobblemonFarmersRegistery.BlockRegistry.RANCHING_STATION.get()));
        this.name = Component.translatable("jei.cobblemon_farmers.ranching_station.category.milking");
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
    public RecipeType<RanchingStationMilkingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RanchingStationMilkingRecipe recipe, IFocusGroup focuses) {
        Species species = Objects.requireNonNull(PokemonSpecies.INSTANCE.getByName(recipe.getPokemon()));
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 1).addItemStack(PokemonItem.from(species));
        if (!recipe.getSmallMilk().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 5, 20).addItemStack(recipe.getSmallMilk()).addRichTooltipCallback((view, tooltip) -> {
                tooltip.add(Component.translatable("jei.cobblemon_farmers.ranching_station.milk.affection.range", recipe.milkIsSized() ? "1-5" : "1-10").withStyle(ChatFormatting.LIGHT_PURPLE));
            });
        }
        if (!recipe.getLargeMilk().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 5 + (recipe.milkIsSized() ? 18 : 0), 20).addItemStack(recipe.getLargeMilk()).addRichTooltipCallback((view, tooltip) -> {
                if (recipe.milkIsSized()) tooltip.add(Component.translatable("jei.cobblemon_farmers.ranching_station.milk.affection.over", "6").withStyle(ChatFormatting.LIGHT_PURPLE));
                else tooltip.add(Component.translatable("jei.cobblemon_farmers.ranching_station.milk.affection.range", "1-10").withStyle(ChatFormatting.LIGHT_PURPLE));
            });
        }
    }

    @Override
    public void draw(RanchingStationMilkingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        Species species = Objects.requireNonNull(PokemonSpecies.INSTANCE.getByName(recipe.getPokemon()));
        guiGraphics.drawString(Minecraft.getInstance().font, Language.getInstance().getVisualOrder(FormattedText.of(species.getName())), 24, 4, 0xFF4b3658, false);
        guiGraphics.drawString(Minecraft.getInstance().font, Language.getInstance().getVisualOrder(Component.translatable("jei.cobblemon_farmers.ranching_station.more_info")), 8, 46, 0xFFFFFFFF, false);
    }

}