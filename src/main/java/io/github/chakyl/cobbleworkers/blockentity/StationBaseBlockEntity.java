package io.github.chakyl.cobbleworkers.blockentity;

import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.chakyl.cobbleworkers.CobbleWorkers;
import io.github.chakyl.cobbleworkers.recipe.CraftStationRecipe;
import io.github.chakyl.cobbleworkers.registry.CobbleWorkersRegistery;
import io.github.chakyl.cobbleworkers.screen.CraftStationMenu;
import io.github.chakyl.cobbleworkers.utils.PokeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nullable;
import java.util.Optional;

import static io.github.chakyl.cobbleworkers.utils.PokeUtils.getItemFormPokemon;
import static io.github.chakyl.cobbleworkers.utils.PokeUtils.getPokemonRotation;

public class StationBaseBlockEntity extends BlockEntity {
    private PokemonEntity workerEntity;

    public StationBaseBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    public PokemonEntity getWorkerEntity() {
        return this.workerEntity;
    }

    public void initializeWorker() {
        ItemStack pokemonStack = getPokemonItem();
        if (!pokemonStack.isEmpty() && this.getLevel() != null) {
            Pokemon pokemon = getItemFormPokemon(pokemonStack, this.level);
            float rotation = getPokemonRotation(this.getBlockState());
            this.workerEntity = new PokemonEntity(this.getLevel(), pokemon, CobblemonEntities.POKEMON);
            this.workerEntity.setNoAi(true);
            this.workerEntity.setYHeadRot(rotation);
            this.workerEntity.setYBodyRot(rotation);
        } else {
            this.workerEntity = null;
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            if (this.workerEntity != null) {
                this.workerEntity.tick();

            }
        }
    }

    public boolean hasWorker() {
        return false;
    }

    public double getSpeedModifier(Stats scalingStat) {
        ItemStack pokemonItem = getPokemonItem();
        if (!pokemonItem.isEmpty() && scalingStat != null) {
            Pokemon pokemon = getItemFormPokemon(pokemonItem, this.level);
            return (double) Mth.floor(((double) pokemon.getStat(scalingStat) / (255.0 / 2.0)) * 100) / 100;
        }
        return 0.0;
    }

    public int getMultChance(Stats scalingStat) {
        ItemStack pokemonItem = getPokemonItem();
        if (!pokemonItem.isEmpty() && scalingStat != null) {
            Pokemon pokemon = getItemFormPokemon(pokemonItem, this.level);
            return Mth.floor((pokemon.getStat(scalingStat) / (255.0 / 2.0)) * 100);
        }
        return 0;
    }

    public ItemStack getPokemonItem() {
        return null;
    }
}
