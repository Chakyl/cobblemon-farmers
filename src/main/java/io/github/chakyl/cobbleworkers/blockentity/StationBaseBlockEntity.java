package io.github.chakyl.cobbleworkers.blockentity;

import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.chakyl.cobbleworkers.CobbleWorkers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;

import static io.github.chakyl.cobbleworkers.utils.PokeUtils.getItemFormPokemon;
import static io.github.chakyl.cobbleworkers.utils.PokeUtils.getPokemonRotation;

public class StationBaseBlockEntity extends BlockEntity {
    protected UUID owner;
    private PokemonEntity workerEntity;
    Set<String> workerAspects;
    ElementalType primaryType;
    ElementalType secondaryType;
    double speedModifier;
    int multChance;

    public StationBaseBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    public ElementalType getPrimaryType() {
        return this.primaryType;
    }

    public ElementalType getSecondaryType() {
        return this.secondaryType;
    }

    public PokemonEntity getWorkerEntity() {
        return this.workerEntity;
    }

    public Set<String> getWorkerAspects() {
        return this.workerAspects;
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
            this.workerAspects = pokemon.getAspects();
            this.primaryType = pokemon.getPrimaryType();
            this.secondaryType = pokemon.getSecondaryType();
        } else {
            this.workerEntity = null;
            this.primaryType = null;
            this.secondaryType = null;
        }
        this.speedModifier = 0;
        this.multChance = 0;
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

    public void fetchSpeedModifier(Stats scalingStat) {
        ItemStack pokemonItem = getPokemonItem();
        if (!pokemonItem.isEmpty() && scalingStat != null) {
            Pokemon pokemon = getItemFormPokemon(pokemonItem, this.level);
            this.speedModifier = (double) Mth.floor(((double) pokemon.getStat(scalingStat) / (255.0 / 2.0)) * 100) / 100;
        } else {
            this.speedModifier = 0.0;
        }
    }

    public void fetchMultChance(Stats scalingStat) {
        ItemStack pokemonItem = getPokemonItem();
        if (!pokemonItem.isEmpty() && scalingStat != null) {
            Pokemon pokemon = getItemFormPokemon(pokemonItem, this.level);
            this.multChance = Mth.floor((pokemon.getStat(scalingStat) / (255.0 / 2.0)) * 100);
        } else {
            this.multChance = 0;
        }
    }

    public double getSpeedModifier() {
        return this.speedModifier;
    }

    public int getMultChance() {
        return this.multChance;
    }

    public ItemStack getPokemonItem() {
        return null;
    }

    public void setOwner(UUID uuid) {
        this.owner = uuid;
    }

    public boolean validateOwner(Player player) {
        if (BattleRegistry.INSTANCE.getBattleByParticipatingPlayer((ServerPlayer) player) != null) {
            player.sendSystemMessage(Component.translatable("message.cobble_workers.in_battle").withStyle(ChatFormatting.RED));
            return false;
        }
        if (owner == null || !owner.equals(player.getUUID())) {
            player.sendSystemMessage(Component.translatable("message.cobble_workers.not_owned").withStyle(ChatFormatting.RED));
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        load(pkt.getTag());
    }
}
