package io.github.chakyl.cobblemonfarmers.blockentity;

import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.mixin.CWRecipeManagerAccessor;
import io.github.chakyl.cobblemonfarmers.recipe.CraftStationRecipe;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.screen.RanchingStationMenu;
import io.github.chakyl.cobblemonfarmers.utils.PokeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RanchingStationBlockEntity extends StationBaseBlockEntity implements MenuProvider {
    protected final ContainerData data;
    private int progress = 0;
    private int craftingTime;
    private ResourceLocation lastRecipeID;
    private boolean checkNewRecipe;
    private boolean swapPriority = false;

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

    public RanchingStationBlockEntity(BlockPos pos, BlockState state) {
        super(CobblemonFarmersRegistery.BlockEntityRegistry.RANCHING_STATION.get(), pos, state);
        this.checkNewRecipe = true;
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> RanchingStationBlockEntity.this.progress;
                    case 1 -> RanchingStationBlockEntity.this.craftingTime;
                    case 2 -> RanchingStationBlockEntity.this.swapPriority ?  1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> RanchingStationBlockEntity.this.progress = pValue;
                    case 1 -> RanchingStationBlockEntity.this.craftingTime = pValue;
                    case 2 -> RanchingStationBlockEntity.this.swapPriority = pValue == 1;
                }

            }

            @Override
            public int getCount() {
                return 3;
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
            if (this.hasWorker() && this.hasInput()) {
            }
            if (didInventoryChange) {
                setChanged();
            }
        }

    }

    public void setPrioritySwapped() {
        this.swapPriority = !this.swapPriority;
        checkNewRecipe = true;
        setChanged();
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
        return new RanchingStationMenu(pContainerId, pPlayerInventory, this, this.data);
    }


    public double getSpeedModifier() {
//        Optional<MysteryMineRecipe> recipe = this.getCurrentRecipe(new RecipeWrapper(this.inputInventory));
//        return recipe.map(mysteryMineRecipe -> super.getSpeedModifier(mysteryMineRecipe.getSpeedStat())).orElse(0.0);
        return 0;
    }

    public int getMultChance() {
        return 0;
//        Optional<MysteryMineRecipe> recipe = this.getCurrentRecipe(new RecipeWrapper(this.inputInventory));
//        return recipe.map(mysteryMineRecipe -> super.getMultChance(mysteryMineRecipe.getMultStat())).orElse(0);
    }


    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        CompoundTag data = new CompoundTag();
        if (owner != null) data.putUUID("Owner", owner);
        data.put("InputInventory", this.inputInventory.serializeNBT());
        data.put("PokemonInventory", this.pokemonInventory.serializeNBT());
        data.putInt("CraftingTime", craftingTime);
        data.putInt("Progress", progress);
        data.putBoolean("SwapPriority", swapPriority);
        tag.put(CobblemonFarmers.MODID, data);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        CompoundTag data = pTag.getCompound(CobblemonFarmers.MODID);
        owner = data.hasUUID("Owner") ? data.getUUID("Owner") : null;
        if (data.contains("InputInventory", Tag.TAG_COMPOUND)) {
            this.inputInventory.deserializeNBT(data.getCompound("InputInventory"));
        }

        if (data.contains("PokemonInventory", Tag.TAG_COMPOUND)) {
            this.pokemonInventory.deserializeNBT(data.getCompound("PokemonInventory"));
            this.initializeWorker();
        }

        craftingTime = data.getInt("CraftingTime");
        progress = data.getInt("Progress");
        swapPriority = data.getBoolean("SwapPriority");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblemon_farmers.ranching_station");
    }

}
