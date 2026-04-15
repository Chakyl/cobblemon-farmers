package io.github.chakyl.cobblemonfarmers.utils;

import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class GuiUtils {
    public static final ResourceLocation ELEMENT_TEXTURE = new ResourceLocation("cobblemon:textures/gui/types_small.png");

    public static void renderPokemon(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, Pokemon pokemon) {
        renderPokemon(pGuiGraphics, pMouseX, pMouseY, pokemon, 0);
    }

    public static void renderPokemon(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, ElementalType primaryType, ElementalType secondaryElement) {
        renderPokemon(pGuiGraphics, pMouseX, pMouseY, primaryType, secondaryElement, 0);
    }
    
    public static void renderPokemon(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, ElementalType primaryType, ElementalType secondaryElement, int descOffset) {
        if (secondaryElement != null) {
            int blitOffset2 = secondaryElement.getTextureXMultiplier();
            pGuiGraphics.blit(ELEMENT_TEXTURE, pMouseX + 56, pMouseY + 14, 0, blitOffset2 * 18, 18, 18, 18, 324, 18);
            pGuiGraphics.blit(ELEMENT_TEXTURE, pMouseX + 86, pMouseY + 58 + descOffset, 0, blitOffset2 * 9, 18, 9, 9, 162, 9);
        }
        int blitOffset = primaryType.getTextureXMultiplier();
        pGuiGraphics.blit(ELEMENT_TEXTURE, pMouseX + 35, pMouseY + 14, 0, blitOffset * 18, 18, 18, 18, 324, 18);
        pGuiGraphics.blit(ELEMENT_TEXTURE, pMouseX + 86, pMouseY + 44 + descOffset, 0, blitOffset * 9, 18, 9, 9, 162, 9);
    }

    public static void renderPokemon(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, Pokemon pokemon, int descOffset) {
        if (pokemon == null) return;
        renderPokemon(pGuiGraphics, pMouseX, pMouseY, pokemon.getPrimaryType(), pokemon.getSecondaryType(), descOffset);
    }

    public static void renderPokemonTypesOnly(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, Pokemon pokemon) {
        if (pokemon == null) return;
        ElementalType secondaryElement = pokemon.getSecondaryType();
        if (secondaryElement != null) {
            int blitOffset2 = secondaryElement.getTextureXMultiplier();
            pGuiGraphics.blit(ELEMENT_TEXTURE, pMouseX + 56, pMouseY + 14, 0, blitOffset2 * 18, 18, 18, 18, 324, 18);
        }
        int blitOffset = pokemon.getPrimaryType().getTextureXMultiplier();
        pGuiGraphics.blit(ELEMENT_TEXTURE, pMouseX + 35, pMouseY + 14, 0, blitOffset * 18, 18, 18, 18, 324, 18);
    }

    public static void emiWordWrap(WidgetHolder widgets, Component text, int x, int y, int color, int maxWidth, boolean shadow) {
        Font font = Minecraft.getInstance().font;
        List<FormattedCharSequence> lines = font.split(text, maxWidth);

        int yOffset = y;
        for (var line : lines) {
            widgets.addText(line, x, yOffset, color, shadow);
            yOffset += font.lineHeight + 1;
        }

    }
}
