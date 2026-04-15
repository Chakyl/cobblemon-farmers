package io.github.chakyl.cobblemonfarmers.screen;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.PokemonStoreManager;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import io.github.chakyl.cobblemonfarmers.blockentity.RanchingStationBlockEntity;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.screen.helpers.WorkerSlot;
import io.github.chakyl.cobblemonfarmers.screen.helpers.WorkstationPartySlot;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static io.github.chakyl.cobblemonfarmers.utils.PokeUtils.getPokemonItemForm;
import static io.github.chakyl.cobblemonfarmers.utils.PokeUtils.handlePartySlot;

public class RanchingStationMenu extends AbstractWorkerMenu {
    public final RanchingStationBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    private final Player player;
    private final PlayerPartyStore party;
    private final SimpleContainer partyContainer = new SimpleContainer(6);
    private final ArrayList<Slot> partySlots = new ArrayList<>(6);

    public RanchingStationMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    public RanchingStationMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(CobblemonFarmersRegistery.MenuRegistry.RANCHING_STATION.get(), pContainerId, inv, entity, data);
        checkContainerSize(inv, 2);
        blockEntity = ((RanchingStationBlockEntity) entity);
        this.level = inv.player.level();
        this.data = data;
        this.player = inv.player;
        if (!inv.player.level().isClientSide) {
            PokemonStoreManager storage = Cobblemon.INSTANCE.getStorage();
            try {
                this.party = storage.getParty(inv.player.getUUID());
            } catch (NoPokemonStoreException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.party = new PlayerPartyStore(inv.player.getUUID());
        }
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        getPartySlots();
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).ifPresent(iItemHandler -> {
            this.addSlot(new SlotItemHandler(iItemHandler, 0, 91, 20));
        });
        this.blockEntity.getPokemonCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            this.addSlot(new WorkerSlot(iItemHandler, 0, 11, 19));
        });
        addDataSlots(data);
    }

    protected void getPartySlots() {
        for (int i = 0; i < 6; ++i) {
            this.partySlots.add(this.addSlot(new PartySlot(this.partyContainer, i, i % 2 == 0 ? 186 : 217, (((i / 2) * 31) + (i % 2 == 0 ? 20 : 28)))));
            this.partyContainer.addItem(getPokemonItemForm(this.party.get(i)));
        }
    }


    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int PARTY_SLOT_COUNT = 6;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT + PARTY_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 1;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem() || (pIndex >= TE_INVENTORY_FIRST_SLOT_INDEX - PARTY_SLOT_COUNT && pIndex < TE_INVENTORY_FIRST_SLOT_INDEX) || pIndex == slots.size() - 1)
            return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();
        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    public int getRanchingPower() {
        return this.blockEntity.getRanchingPower();
    }

    public int getFriendshipHearts() { return this.blockEntity.getFriendshipHearts(); }

    public int getHPHearts() {
        return this.blockEntity.getHPHearts();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, CobblemonFarmersRegistery.BlockRegistry.RANCHING_STATION.get());
    }

    public boolean getCanMilk() { return this.blockEntity.hasMilkingRecipe();  }

    public boolean getCanShear() {
        return this.blockEntity.hasMagicShearDrops();
    }

    public boolean getCanForage() {
        return this.blockEntity.hasForageRecipe();
    }

    private class PartySlot extends WorkstationPartySlot {
        public PartySlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public Optional<ItemStack> tryRemove(int pCount, int pDecrement, Player pPlayer) {
            RanchingStationMenu.this.transferFromPartyToWorkerSlot(player, this);
            return Optional.empty();
        }
    }

    private void transferFromPartyToWorkerSlot(Player player, RanchingStationMenu.PartySlot partySlot) {
        handlePartySlot(player, this.level, this.party, partySlot, (WorkerSlot) this.slots.get(this.slots.size() - 1));
        if (!Objects.requireNonNull(this.blockEntity.getLevel()).isClientSide) this.blockEntity.initializeDayData(this.blockEntity.getLevel());
    }
}