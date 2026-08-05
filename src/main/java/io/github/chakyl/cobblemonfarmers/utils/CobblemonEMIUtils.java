package io.github.chakyl.cobblemonfarmers.utils;

import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.chakyl.cobbleemibackported.CobblemonStack;
import io.github.chakyl.cobblemonfarmers.screen.RanchingStationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CobblemonEMIUtils {
    public static CobblemonStack getEmiPokemon(Pokemon pokemon) {
        return new CobblemonStack(pokemon.getForm());
    }


    public String getAspectsStringFromFormData(FormData formData) {
        Set<String> aspects = new HashSet<>(formData.getAspects());
        if (!aspects.isEmpty()) {
            return "-" + aspects.stream().sorted().collect(Collectors.joining("-")).toLowerCase().replace("?", "question").replace("!", "exclamation").replaceAll("[^a-z0-9/._-]", "");
        }
        return "-";
    }

    public static Button addEmiViewDropsButton(RanchingStationScreen screen, int leftPos, int topPos) {
        return new RanchingStationScreen.ViewRecipesButton(
                leftPos + 122, topPos + 19, 42, 16,
                Component.translatable("gui.cobblemon_farmers.view_recipes"),
                (button) -> {
                    if (screen.getMenu().getWorkerPokemon() != null) {
                        CobblemonStack stack = getEmiPokemon(screen.getMenu().getWorkerPokemon());
                        EmiApi.displayUses(stack);
                    }
                }
        );
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
