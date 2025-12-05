package io.github.chakyl.cobbleworkers.utils;

import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class GuiUtils {
    public static final ResourceLocation ELEMENT_TEXTURE = new ResourceLocation("cobblemon:textures/gui/types_small.png");
    public static void renderPokemon(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, ElementalType pPrimaryType, ElementalType pSecondaryType) {
        renderPokemon( pGuiGraphics, pMouseX, pMouseY, pPrimaryType, pSecondaryType, 0);
    }
    public static void renderPokemon(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, ElementalType primaryType, ElementalType secondaryType, int descOffset) {
        if (primaryType == null) return;
        if (secondaryType != null) {
            int blitOffset2 = secondaryType.getTextureXMultiplier();
            pGuiGraphics.blit(ELEMENT_TEXTURE, pMouseX + 56, pMouseY + 14, 0, blitOffset2 * 18, 18, 18, 18, 324, 18);
            pGuiGraphics.blit(ELEMENT_TEXTURE, pMouseX + 86, pMouseY + 58 + descOffset, 0, blitOffset2 * 9, 18, 9, 9, 162, 9);
        }
        int blitOffset = primaryType.getTextureXMultiplier();
        pGuiGraphics.blit(ELEMENT_TEXTURE, pMouseX + 35, pMouseY + 14, 0, blitOffset * 18, 18, 18, 18, 324, 18);
        pGuiGraphics.blit(ELEMENT_TEXTURE, pMouseX + 86, pMouseY + 44 + descOffset, 0, blitOffset * 9, 18, 9, 9, 162, 9);
    }
}
