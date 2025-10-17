package io.github.chakyl.cobbleworkers.screen;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.PokemonStoreManager;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.chakyl.cobbleworkers.blockentity.CraftStationBlockEntity;
import io.github.chakyl.cobbleworkers.registry.CobbleWorkersRegistery;
import io.github.chakyl.cobbleworkers.screen.helpers.WorkerSlot;
import io.github.chakyl.cobbleworkers.utils.PokeUtils;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static io.github.chakyl.cobbleworkers.utils.PokeUtils.getPokemonItemForm;

public class AbstractWorkerMenu extends AbstractContainerMenu {
    public final BlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    private final Player player;
    private final PlayerPartyStore party;
    private final SimpleContainer partyContainer = new SimpleContainer(6);
    private final ArrayList<Slot> partySlots = new ArrayList<>(6);

    public AbstractWorkerMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pMenuType, pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    public AbstractWorkerMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(pMenuType, pContainerId);
        checkContainerSize(inv, 2);
        blockEntity = entity;
        this.level = inv.player.level();
        this.data = data;
        this.player = inv.player;
        PokemonStoreManager storage = Cobblemon.INSTANCE.getStorage();
        try {
            this.party = storage.getParty(inv.player.getUUID());
        } catch (NoPokemonStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }


    public Pokemon getWorkerPokemon() {
        WorkerSlot workerSlot = (WorkerSlot) this.slots.get(this.slots.size() - 1);
        if (workerSlot.getItem().isEmpty()) return null;
        return PokeUtils.getItemFormPokemon(workerSlot.getItem(), this.level);
    }

    protected void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    protected void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
    protected void getPartySlots() {
        for (int i = 0; i < 6; ++i) {
            this.partySlots.add(this.addSlot(new PartySlot(this.partyContainer, i, i % 2 == 0 ? 186 : 217, (((i / 2) * 31) + (i % 2 == 0 ? 20 : 28)))));
            this.partyContainer.addItem(getPokemonItemForm(this.party.get(i)));
        }
    }
    ArrayList<Integer> getPartyLevels() {
        ArrayList<Integer> partyLevels = new ArrayList<>(6);
        for (int i = 0; i < 6; ++i) {
            Pokemon partMon = this.party.get(i);
            if (partMon != null) partyLevels.add(partMon.getLevel());
            else partyLevels.add(-1);
        }
        return partyLevels;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return null;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return false;
    }

    private class PartySlot extends Slot {
        public PartySlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean allowModification(Player pPlayer) {
            return false;
        }

        @Override
        public Optional<ItemStack> tryRemove(int pCount, int pDecrement, Player pPlayer) {
            AbstractWorkerMenu.this.transferFromPartyToWorkerSlot(player, this);
            return Optional.empty();
        }
    }



    private void transferFromPartyToWorkerSlot(Player player, PartySlot partySlot) {
        if (this.level.isClientSide()) return;
        WorkerSlot workerSlot = (WorkerSlot) this.slots.get(this.slots.size() - 1);
        ItemStack newWorker = partySlot.getItem().copy();
        Pokemon newWorkerPokemon = null;
        ItemStack oldWorker = workerSlot.getItem().copy();

        if (newWorker.is(CobblemonItems.POKEMON_MODEL)) {
            newWorkerPokemon = this.party.get(partySlot.index);
        }
        if (newWorkerPokemon == null && oldWorker.isEmpty()) return;
        if (oldWorker.isEmpty()) {
            partySlot.set(CobbleWorkersRegistery.ItemRegistry.RETRIEVE_WORKER.get().getDefaultInstance());
            this.party.remove(Objects.requireNonNull(newWorkerPokemon));
        } else {
            if (newWorkerPokemon != null) {
                this.party.remove(Objects.requireNonNull(newWorkerPokemon));
                this.party.set(partySlot.index, PokeUtils.getItemFormPokemon(oldWorker, this.level));
            }
            partySlot.set(oldWorker);
        }
        partySlot.setChanged();
        if (newWorker.is(CobbleWorkersRegistery.ItemRegistry.RETRIEVE_WORKER.get())) {
            workerSlot.set(ItemStack.EMPTY);
            this.party.set(partySlot.index, PokeUtils.getItemFormPokemon(oldWorker, this.level));
        } else if (newWorker.is(CobblemonItems.POKEMON_MODEL)) {
            workerSlot.set(newWorker);
        }
        workerSlot.setChanged();
    }

}