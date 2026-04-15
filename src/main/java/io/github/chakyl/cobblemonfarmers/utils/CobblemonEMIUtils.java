package io.github.chakyl.cobblemonfarmers.utils;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.chakyl.cobbleemibackported.CobblemonStack;

import java.util.Objects;

public class CobblemonEMIUtils {
    public static CobblemonStack getEmiPokemon(String pokemon) {
        Species species = Objects.requireNonNull(PokemonSpecies.INSTANCE.getByName(pokemon));
        FormData form = species.getStandardForm();
        return new CobblemonStack(form);
    }
}
