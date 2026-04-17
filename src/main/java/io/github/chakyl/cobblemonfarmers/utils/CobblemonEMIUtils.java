package io.github.chakyl.cobblemonfarmers.utils;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.chakyl.cobbleemibackported.CobblemonStack;

import java.util.Objects;

public class CobblemonEMIUtils {
    public static CobblemonStack getEmiPokemon(Pokemon pokemon) {
        return new CobblemonStack(pokemon.getForm());
    }
}
