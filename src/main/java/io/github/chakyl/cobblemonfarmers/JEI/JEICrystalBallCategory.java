package io.github.chakyl.cobblemonfarmers.JEI;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.blockentity.CrystalBallBlockEntity;
import io.github.chakyl.cobblemonfarmers.recipe.CraftStationRecipe;
import io.github.chakyl.cobblemonfarmers.recipe.CrystalBallRecipe;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.utils.ElementalTypeUtils;
import mezz.jei.api.recipe.category.IRecipeCategory;

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

public class JEICrystalBallCategory implements IRecipeCategory<CrystalBallRecipe> {
    public static final RecipeType<CrystalBallRecipe> TYPE = RecipeType.create(CobblemonFarmers.MODID, "crystal_ball", CrystalBallRecipe.class);
    public static final ResourceLocation TEXTURE = new ResourceLocation(CobblemonFarmers.MODID, "textures/jei/crystal_ball.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final Component name;

    private final int displayWidth = 168;
    private final int displayHeight = 104;

    public JEICrystalBallCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, displayWidth, displayHeight);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CobblemonFarmersRegistery.BlockRegistry.CRYSTAL_BALL.get()));
        this.name = Component.translatable(CobblemonFarmersRegistery.BlockRegistry.CRYSTAL_BALL.get().getDescriptionId());
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
    public RecipeType<CrystalBallRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrystalBallRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 0, 0).addItemStack(ElementalTypeUtils.getItemFromElementalType(ElementalTypes.INSTANCE.getPSYCHIC()).getDefaultInstance());
        builder.addSlot(RecipeIngredientRole.INPUT, 8, 26).addIngredients(recipe.getIngredient());
        int i = 0;
        int row = 0;
        int rowLength = 9;
        for (ElementalType type : recipe.getAffectedTypes().isEmpty() ? ElementalTypes.INSTANCE.all() : recipe.getAffectedTypes()) {
            if (i % rowLength == 0) row++;
            builder.addSlot(RecipeIngredientRole.OUTPUT, (16 * rowLength) + 21 + ((i - (row * rowLength)) * 18), 66 + ((18 * (i / rowLength)))).addItemStack(ElementalTypeUtils.getItemFromElementalType(type).getDefaultInstance());
            i++;
        }
    }

    @Override
    public void draw(CrystalBallRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        ElementalType elementalType = ElementalTypes.INSTANCE.getPSYCHIC();
        guiGraphics.drawString(Minecraft.getInstance().font,Language.getInstance().getVisualOrder(Component.translatable("jei.cobblemon_farmers.crystal_ball.elemental_type", elementalType.getDisplayName())),22, 4, elementalType.getHue(), true);
        guiGraphics.drawString(Minecraft.getInstance().font,Language.getInstance().getVisualOrder(Component.translatable("jei.cobblemon_farmers.crystal_ball.bonus_chance", Math.round(recipe.getBonusChance() * 100) + "%")),32, 26, 0xFFFFFFFF, false);
        guiGraphics.drawString(Minecraft.getInstance().font,Language.getInstance().getVisualOrder(Component.translatable("jei.cobblemon_farmers.crystal_ball.consume_chance", Math.round(recipe.getConsumeChance() * 100) + "%")),32, 36, 0xFFFFFFFF, false);
        guiGraphics.drawString(Minecraft.getInstance().font,Language.getInstance().getVisualOrder(Component.translatable("jei.cobblemon_farmers.crystal_ball.crafting_time", CrystalBallBlockEntity.CRAFTING_TIME / 20)),112, 55, 0xFF4b3658, false);
        guiGraphics.drawString(Minecraft.getInstance().font,Language.getInstance().getVisualOrder(Component.translatable("jei.cobblemon_farmers.crystal_ball.affected_types")),2, 55, 0xFF4b3658, false);
        if (mouseX >= displayWidth - 16 && mouseX <= displayWidth && mouseY >= 0 && mouseY <= 16) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("info.cobblemon_farmers.crystal_ball.type.psychic", Stats.SPECIAL_ATTACK.getDisplayName()).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("jei.cobblemon_farmers.crystal_ball.speed_stat", Stats.SPECIAL_ATTACK.getDisplayName()).withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.translatable("jei.cobblemon_farmers.crystal_ball.aoe_stat", Stats.HP.getDisplayName()).withStyle(ChatFormatting.GOLD));

            guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltip, java.util.Optional.empty(), (int) mouseX, (int) mouseY);
        }
    }
}