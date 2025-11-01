package io.github.chakyl.cobbleworkers.entity;

import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.DataKeys;
import io.github.chakyl.cobbleworkers.CobbleWorkers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class ClientSidePokemon extends Pokemon {
    public ClientSidePokemon() {
        super();
    }

    @Override
    public Ability rollAbility() {
        return Abilities.INSTANCE.getDUMMY().create(true);
    }

    public ClientSidePokemon loadFromNBTClient(CompoundTag nbt) {
        if (nbt.contains(DataKeys.POKEMON_SPECIES_IDENTIFIER)) {
            String pokemonSpeciesIdentifier = nbt.getString(DataKeys.POKEMON_SPECIES_IDENTIFIER);
            this.setSpecies(Objects.requireNonNull(PokemonSpecies.INSTANCE.getByName(pokemonSpeciesIdentifier.substring(pokemonSpeciesIdentifier.indexOf(":") + 1))));
        }

        if (nbt.contains(DataKeys.POKEMON_FORM_ID)) {
            FormData resolvedForm = this.getSpecies().getStandardForm();
            for (FormData form : this.getSpecies().getForms()) {
                if (form.formOnlyShowdownId().equals(nbt.getString(DataKeys.POKEMON_FORM_ID))) {
                    resolvedForm = form;
                }
            }
            this.setForm(resolvedForm);
        }

        if (nbt.contains(DataKeys.POKEMON_LEVEL)) {
            this.setLevel(nbt.getShort(DataKeys.POKEMON_LEVEL));
        }

        if (nbt.contains(DataKeys.POKEMON_HEALTH)) {
            this.setCurrentHealth(nbt.getShort(DataKeys.POKEMON_HEALTH));
        }

        if (nbt.contains(DataKeys.POKEMON_EVS)) {
            this.getEvs().loadFromNBT(nbt.getCompound(DataKeys.POKEMON_EVS));
        }

        if (nbt.contains(DataKeys.POKEMON_IVS)) {
            CompoundTag pokemonIvs = nbt.getCompound(DataKeys.POKEMON_IVS);
            this.getIvs().set(Stats.ATTACK, pokemonIvs.getShort(Stats.ATTACK.getIdentifier().getPath()));
            this.getIvs().set(Stats.SPECIAL_ATTACK, pokemonIvs.getShort(Stats.SPECIAL_ATTACK.getIdentifier().getPath()));
            this.getIvs().set(Stats.DEFENCE, pokemonIvs.getShort(Stats.DEFENCE.getIdentifier().getPath()));
            this.getIvs().set(Stats.SPECIAL_DEFENCE, pokemonIvs.getShort(Stats.SPECIAL_DEFENCE.getIdentifier().getPath()));
            this.getIvs().set(Stats.SPEED, pokemonIvs.getShort(Stats.SPEED.getIdentifier().getPath()));
            this.getIvs().set(Stats.HP, pokemonIvs.getShort(Stats.HP.getIdentifier().getPath()));
        }

        if (nbt.contains(DataKeys.POKEMON_NATURE)) {
            String pokemonNature = nbt.getString(DataKeys.POKEMON_NATURE);
            if (!pokemonNature.isEmpty()) {
                this.setNature(Objects.requireNonNull(Natures.INSTANCE.getNature(new ResourceLocation(pokemonNature))));
            }
        }
        
        if (nbt.contains(DataKeys.POKEMON_MINTED_NATURE)) {
            String pokemonMintedNature = nbt.getString(DataKeys.POKEMON_MINTED_NATURE);
            if (!pokemonMintedNature.isEmpty()) {
                this.setMintedNature(Objects.requireNonNull(Natures.INSTANCE.getNature(new ResourceLocation(pokemonMintedNature))));
            }
        }

        this.updateAspects();
        return this;
    }

}
