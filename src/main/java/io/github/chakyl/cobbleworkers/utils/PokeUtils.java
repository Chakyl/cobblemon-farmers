package io.github.chakyl.cobbleworkers.utils;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.chakyl.cobbleworkers.block.CraftStationBlock;
import io.github.chakyl.cobbleworkers.registry.CobbleWorkersRegistery;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PokeUtils {

    public static ItemStack getPokemonItemForm(Pokemon pokemon) {
        ItemStack itemStack = new ItemStack(CobblemonItems.POKEMON_MODEL.asItem());
        if (pokemon == null) return new ItemStack(CobbleWorkersRegistery.ItemRegistry.RETRIEVE_WORKER.get());
        CompoundTag tag = itemStack.getOrCreateTag();
        CompoundTag pokeTag = pokemon.saveToNBT(new CompoundTag());
        tag.put("species", pokeTag.get("Species"));
        tag.put("pokeData", pokeTag);
        return itemStack;
    }

    public static boolean validWorkerType(ItemStack itemPokemon, ElementalType type) {
        if (!itemPokemon.hasTag()) return false;
        String speciesTag = itemPokemon.getTag().getString("species");
        if (speciesTag.isEmpty()) return false;
        Species pokeSpecies = PokemonSpecies.INSTANCE.getByIdentifier(new ResourceLocation(speciesTag));
        for (ElementalType elementalType : pokeSpecies.getTypes()) {
            if (elementalType.equals(type)) return true;
        }
        return false;
    }

    public static Pokemon getItemFormPokemon(ItemStack oldWorker, Level level) {
        CompoundTag tag = oldWorker.getTag();
        if (tag == null) throw new RuntimeException("Horrible thing happened! The Pokemon doesn't exist!!!!");
        return Pokemon.Companion.loadFromNBT(tag.getCompound("pokeData"));
    }

    public static float getPokemonRotation(BlockState blockState) {
        Direction facingDirection = blockState.getValue(CraftStationBlock.FACING).getOpposite();
        float rotation;
        switch (facingDirection) {
            case SOUTH -> rotation = 180f;
            case EAST -> rotation = 90f;
            case WEST -> rotation = 270f;
            default -> rotation = 0f;
        }
        return rotation;
    }

    public static float getPokemonOffset(BlockState blockState, boolean xOffset) {
        Direction facingDirection = blockState.getValue(CraftStationBlock.FACING);
        float offset;
        switch (facingDirection) {
            case SOUTH -> offset = xOffset ? 0.5f : -0.5f;
            case EAST -> offset = xOffset ? -0.5f : 0.5f;
            case WEST -> offset = xOffset ? 1.5f : 0.5f;
            default -> offset = xOffset ? 0.5f : 1.5f;
        }
        return offset;
    }
}
