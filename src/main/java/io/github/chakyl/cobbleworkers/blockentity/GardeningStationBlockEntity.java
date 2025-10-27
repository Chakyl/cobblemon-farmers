package io.github.chakyl.cobbleworkers.blockentity;

import io.github.chakyl.cobbleworkers.CobbleWorkers;
import io.github.chakyl.cobbleworkers.registry.CobbleWorkersRegistery;
import io.github.chakyl.cobbleworkers.screen.GardeningStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class GardeningStationBlockEntity extends StationBaseBlockEntity implements MenuProvider {
    protected final ContainerData data;
    private int progress = 0;
    private int craftingTime;
    private ResourceLocation lastRecipeID;
    private boolean checkNewRecipe;

    private final ItemStackHandler inputInventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            checkNewRecipe = true;
            setChanged();
        }
    };

    private final ItemStackHandler pokemonInventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            checkNewRecipe = true;
            setChanged();
            initializeWorker();
        }
    };

    private final LazyOptional<ItemStackHandler> inputOptional = LazyOptional.of(() -> this.inputInventory);
    private final LazyOptional<ItemStackHandler> pokemonOptional = LazyOptional.of(() -> this.pokemonInventory);

    public GardeningStationBlockEntity(BlockPos pos, BlockState state) {
        super(CobbleWorkersRegistery.BlockEntityRegistry.GARDENING_STATION.get(), pos, state);
        this.checkNewRecipe = true;
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> GardeningStationBlockEntity.this.progress;
                    case 1 -> GardeningStationBlockEntity.this.craftingTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> GardeningStationBlockEntity.this.progress = pValue;
                    case 1 -> GardeningStationBlockEntity.this.craftingTime = pValue;
                }

            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public ItemStack getPokemonItem() {
        return this.pokemonInventory.getStackInSlot(0);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        boolean didInventoryChange = false;
        super.tick(level, pos, state);
        if (!level.isClientSide()) {
            // tick function
        }

    }

    @Override
    public boolean hasWorker() {
        return !this.pokemonInventory.getStackInSlot(0).isEmpty();
    }

    public boolean hasInput() {
        return !this.inputInventory.getStackInSlot(0).isEmpty();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.inputOptional.cast();
        }

        return super.getCapability(cap, side);
    }

    public <T> LazyOptional<T> getPokemonCapability(Capability<T> cap) {
        return this.pokemonOptional.cast();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.inputOptional.invalidate();
        this.pokemonOptional.invalidate();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null)
            this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public LazyOptional<ItemStackHandler> getInputOptional() {
        return this.inputOptional;
    }

    public LazyOptional<ItemStackHandler> getPokemonOptional() {
        return this.pokemonOptional;
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(2);
        inventory.setItem(0, inputInventory.getStackInSlot(0));
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new GardeningStationMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        CompoundTag data = new CompoundTag();
        data.put("InputInventory", this.inputInventory.serializeNBT());
        data.put("PokemonInventory", this.pokemonInventory.serializeNBT());
        data.putInt("CraftingTime", craftingTime);
        data.putInt("Progress", progress);
        tag.put(CobbleWorkers.MODID, data);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        CompoundTag data = pTag.getCompound(CobbleWorkers.MODID);
        if (data.contains("InputInventory", Tag.TAG_COMPOUND)) {
            this.inputInventory.deserializeNBT(data.getCompound("InputInventory"));
        }

        if (data.contains("PokemonInventory", Tag.TAG_COMPOUND)) {
            this.pokemonInventory.deserializeNBT(data.getCompound("PokemonInventory"));
            this.initializeWorker();
        }

        craftingTime = data.getInt("CraftingTime");
        progress = data.getInt("Progress");
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
    public Component getDisplayName() {
        return Component.translatable("block.cobble_workers.gardening_station");
    }

}
