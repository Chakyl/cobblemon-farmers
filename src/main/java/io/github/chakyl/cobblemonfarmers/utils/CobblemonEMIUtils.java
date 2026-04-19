package io.github.chakyl.cobblemonfarmers.utils;

import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.chakyl.cobbleemibackported.CobblemonStack;

import java.util.HashSet;
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
}
