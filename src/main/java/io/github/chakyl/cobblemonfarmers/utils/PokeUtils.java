package io.github.chakyl.cobblemonfarmers.utils;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.chakyl.cobblemonfarmers.block.CraftStationBlock;
import io.github.chakyl.cobblemonfarmers.entity.ClientSidePokemon;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.screen.helpers.WorkerSlot;
import io.github.chakyl.cobblemonfarmers.screen.helpers.WorkstationPartySlot;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class PokeUtils {

    public static ItemStack getPokemonItemForm(Pokemon pokemon) {
        if (pokemon == null) return new ItemStack(CobblemonFarmersRegistery.ItemRegistry.RETRIEVE_WORKER.get());
        ItemStack itemStack = PokemonItem.from(pokemon);
        CompoundTag tag = itemStack.getOrCreateTag();
        CompoundTag pokeTag = pokemon.saveToNBT(new CompoundTag());
        tag.put("pokeData", pokeTag);
        return itemStack;
    }

    public static boolean validWorkerType(ItemStack itemPokemon, ElementalType type, Level level) {
        if (!itemPokemon.hasTag()) return false;
        Pokemon pokemon = getItemFormPokemon(itemPokemon, level);
        for (ElementalType elementalType : pokemon.getTypes()) {
            if (elementalType.equals(type)) return true;
        }
        return false;
    }

    public static boolean priorityWorkerType(ItemStack itemPokemon, ElementalType type, Level level, boolean secondary) {
        if (!itemPokemon.hasTag()) return false;
        Pokemon pokemon = getItemFormPokemon(itemPokemon, level);
        if (secondary && pokemon.getSecondaryType() != null) return pokemon.getSecondaryType().equals(type);
        return pokemon.getPrimaryType().equals(type);
    }

    public static Pokemon getItemFormPokemon(ItemStack pokeItem, Level level) {
        CompoundTag tag = pokeItem.getTag();
        if (tag == null) throw new RuntimeException("Horrible thing happened! The Pokemon doesn't exist!!!!");
        if (level.isClientSide) {
            return new ClientSidePokemon().loadFromNBTClient(tag.getCompound("pokeData"));
        }
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

    public static boolean hasWorkerSlot(Player player) {
        return Mth.floor(player.getAttribute(CobblemonFarmersRegistery.AttributeRegistry.WORKERS_ASSIGNED.get()).getValue()) < Mth.floor(player.getAttribute(CobblemonFarmersRegistery.AttributeRegistry.WORKER_CAP.get()).getValue());
    }

    public static void handlePartySlot(Player player, Level level, PlayerPartyStore party, WorkstationPartySlot partySlot, WorkerSlot workerSlot) {
        if (level.isClientSide()) return;
        int difference = 0;
        int slotIndex = partySlot.index - 36;
        ItemStack newWorker = partySlot.getItem().copy();
        Pokemon newWorkerPokemon = null;
        ItemStack oldWorker = workerSlot.getItem().copy();
        boolean noWorker = oldWorker.isEmpty();
        if (noWorker && !hasWorkerSlot(player)) return;
        Pokemon oldWorkerPokemon = null;
        if (!oldWorker.isEmpty()) {
            oldWorkerPokemon = PokeUtils.getItemFormPokemon(oldWorker, level);
        }
        if (newWorker.is(CobblemonItems.POKEMON_MODEL)) {
            newWorkerPokemon = party.get(slotIndex);
        }
        if (newWorkerPokemon == null && noWorker) return;
        if (noWorker) {
            partySlot.set(CobblemonFarmersRegistery.ItemRegistry.RETRIEVE_WORKER.get().getDefaultInstance());
            party.remove(Objects.requireNonNull(newWorkerPokemon));
            difference--;
        } else {
            if (newWorkerPokemon != null && oldWorkerPokemon != null) {
                party.remove(Objects.requireNonNull(newWorkerPokemon));
                party.set(slotIndex, oldWorkerPokemon);
            }
            partySlot.set(oldWorker);
        }
        partySlot.setChanged();
        if (newWorker.is(CobblemonFarmersRegistery.ItemRegistry.RETRIEVE_WORKER.get())) {
            workerSlot.set(ItemStack.EMPTY);
            party.set(slotIndex, PokeUtils.getItemFormPokemon(oldWorker, level));
            difference++;
        } else if (newWorker.is(CobblemonItems.POKEMON_MODEL)) {
            workerSlot.set(newWorker);
        }
        if (difference != 0) {
            AttributeInstance attr = player.getAttribute(CobblemonFarmersRegistery.AttributeRegistry.WORKERS_ASSIGNED.get());
            attr.setBaseValue(attr.getValue() - difference);
        }
        level.playSound(null, player.getOnPos(), CobblemonSounds.GUI_CLICK, SoundSource.BLOCKS, 0.5F, 1.0F);
        workerSlot.setChanged();
    }
}
