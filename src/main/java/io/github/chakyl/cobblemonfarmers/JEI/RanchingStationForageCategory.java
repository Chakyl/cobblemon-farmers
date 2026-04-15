package io.github.chakyl.cobblemonfarmers.JEI;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.recipe.RanchingStationForageRecipe;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.utils.RanchingForage;
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
import net.minecraft.core.NonNullList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class RanchingStationForageCategory implements IRecipeCategory<RanchingStationForageRecipe> {
    public static final RecipeType<RanchingStationForageRecipe> TYPE = RecipeType.create(CobblemonFarmers.MODID, "ranching_station/forge", RanchingStationForageRecipe.class);
    public static final ResourceLocation TEXTURE = new ResourceLocation(CobblemonFarmers.MODID, "textures/jei/ranching_station_forage.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final Component name;

    private int ticks = 0;
    private long lastTickTime = 0;

    public RanchingStationForageCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 175, 57);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CobblemonFarmersRegistery.BlockRegistry.RANCHING_STATION.get()));
        this.name = Component.translatable("jei.cobblemon_farmers.ranching_station.category.foraging");
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
    public RecipeType<RanchingStationForageRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RanchingStationForageRecipe recipe, IFocusGroup focuses) {
        Species species = Objects.requireNonNull(PokemonSpecies.INSTANCE.getByName(recipe.getPokemon()));
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 1).addItemStack(PokemonItem.from(species));
        int row = 0;
        int rowLength = 9;
        NonNullList<RanchingForage> forages = recipe.getForages(null);
        for (int i = 0; i < forages.toArray().length; i++) {
            if (i % rowLength == 0) row++;
            RanchingForage forage = forages.get(i);
            builder.addSlot(RecipeIngredientRole.OUTPUT, (16 * rowLength) + 23 + ((i - (row * rowLength)) * 18), 20 + ((18 * (i / rowLength)))).addItemStack(forage.getItem()).addRichTooltipCallback((view, tooltip) -> {
                tooltip.add(Component.translatable("jei.cobblemon_farmers.ranching_station.forage.affection.over", forage.getMinHearts()).withStyle(ChatFormatting.LIGHT_PURPLE));
                tooltip.add(Component.translatable("jei.cobblemon_farmers.ranching_station.forage.chance", Math.round(forage.getChance() * 1000) / 10.0 + "%").withStyle(ChatFormatting.GOLD));
                if (forage.hasQuality()) tooltip.add(Component.translatable("jei.cobblemon_farmers.ranching_station.forage.affection.quality").withStyle(ChatFormatting.GREEN));
            });
        }
    }

    @Override
    public void draw(RanchingStationForageRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        Species species = Objects.requireNonNull(PokemonSpecies.INSTANCE.getByName(recipe.getPokemon()));
        guiGraphics.drawString(Minecraft.getInstance().font, Language.getInstance().getVisualOrder(FormattedText.of(species.getName())), 24, 4, 0xFF4b3658, false);
        if (recipe.getForages(null).size() <= 9) guiGraphics.drawString(Minecraft.getInstance().font, Language.getInstance().getVisualOrder(Component.translatable("jei.cobblemon_farmers.ranching_station.more_info")), 8, 46, 0xFFFFFFFF, false);
    }

}