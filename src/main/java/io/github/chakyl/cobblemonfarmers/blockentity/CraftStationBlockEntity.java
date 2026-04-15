package io.github.chakyl.cobblemonfarmers.blockentity;

import com.cobblemon.mod.common.api.types.ElementalTypes;
import io.github.chakyl.cobbleworkers.CobbleWorkers;
import io.github.chakyl.cobbleworkers.mixin.CWRecipeManagerAccessor;
import io.github.chakyl.cobbleworkers.recipe.CraftStationRecipe;
import io.github.chakyl.cobbleworkers.registry.CobbleWorkersRegistery;
import io.github.chakyl.cobbleworkers.screen.CraftStationMenu;
import io.github.chakyl.cobbleworkers.utils.PokeUtils;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.mixin.CWRecipeManagerAccessor;
import io.github.chakyl.cobblemonfarmers.recipe.CraftStationRecipe;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.screen.CraftStationMenu;
import io.github.chakyl.cobblemonfarmers.utils.PokeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class CraftStationBlockEntity extends StationBaseBlockEntity implements MenuProvider {
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
    private final ItemStackHandler outputInventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            checkNewRecipe = true;
            setChanged();
        }
    };
    private final ItemStackHandler pokemonInventory = new ItemStackHandler(1) {
        private ItemStack previousWorker;
        @Override
        protected void onContentsChanged(int slot) {
            ItemStack current = getStackInSlot(slot);
            if (previousWorker == null || !ItemStack.matches(current, previousWorker)) {
                this.previousWorker = current.copy();
                super.onContentsChanged(slot);
                checkNewRecipe = true;
                initializeWorker();
                setChanged();
            }
        }
    };

    private final LazyOptional<ItemStackHandler> inputOptional = LazyOptional.of(() -> this.inputInventory);
    private final LazyOptional<ItemStackHandler> outputOptional = LazyOptional.of(() -> this.outputInventory);
    private final LazyOptional<ItemStackHandler> pokemonOptional = LazyOptional.of(() -> this.pokemonInventory);

    public CraftStationBlockEntity(BlockPos pos, BlockState state) {
        super(CobblemonFarmersRegistery.BlockEntityRegistry.CRAFT_STATION.get(), pos, state);
        this.checkNewRecipe = true;
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> CraftStationBlockEntity.this.progress;
                    case 1 -> CraftStationBlockEntity.this.craftingTime;
                    case 2 -> Mth.floor(CraftStationBlockEntity.this.speedModifier * 100);
                    case 3 -> CraftStationBlockEntity.this.multChance;
                    case 4 -> CraftStationBlockEntity.this.swapPriority ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> CraftStationBlockEntity.this.progress = pValue;
                    case 1 -> CraftStationBlockEntity.this.craftingTime = pValue;
                    case 2 -> CraftStationBlockEntity.this.speedModifier = (double) pValue / 100;
                    case 3 -> CraftStationBlockEntity.this.multChance = pValue;
                    case 4 -> CraftStationBlockEntity.this.swapPriority = pValue == 1;
                }

            }

            @Override
            public int getCount() {
                return 5;
            }
        };
    }

    public List<ItemStack> getRenderItems() {
        List<ItemStack> stacks = new ArrayList<>(2);
        ItemStack inputStack = this.inputInventory.getStackInSlot(0);
        ItemStack outputStack = this.outputInventory.getStackInSlot(0);
        if (!inputStack.isEmpty()) stacks.add(inputStack);
        if (!outputStack.isEmpty()) stacks.add(outputStack);
        return stacks;
    }

    public void setPrioritySwapped() {
        this.swapPriority = !this.swapPriority;
        checkNewRecipe = true;
        setChanged();
    }

    @Override
    public ItemStack getPokemonItem() {
        return this.pokemonInventory.getStackInSlot(0);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        boolean hasWorker = this.hasWorker();
        boolean hasInput = !this.inputInventory.getStackInSlot(0).isEmpty();
        boolean didInventoryChange = false;
        super.tick(level, pos, state);
        if (!level.isClientSide()) {
            if (hasWorker && hasInput) {
                Optional<CraftStationRecipe> recipe = this.getMatchingRecipe(new RecipeWrapper(this.inputInventory));
                if (recipe.isPresent() && this.canProcess(recipe.get()) && PokeUtils.validWorkerType(this, recipe.get().getElementalType(), level)) {
                    didInventoryChange = this.processRecipe(recipe.get());
                    if (this.speedModifier <= 0) {
                        this.fetchSpeedModifier(recipe.get().getSpeedStat());
                        this.fetchMultChance(recipe.get().getMultStat());
                    }
                } else {
                    this.speedModifier = 0;
                    this.multChance = 0;
                    this.progress = Mth.clamp(this.progress - 2, 0, this.craftingTime);
                }
            } else if (this.progress > 0) {
                this.progress = Mth.clamp(this.progress - 2, 0, this.craftingTime);
                this.speedModifier = 0;
                this.multChance = 0;
            }
            if (didInventoryChange) {
                setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }

    }

    @Override
    public boolean hasWorker() {
        return !this.pokemonInventory.getStackInSlot(0).isEmpty() && this.primaryType != null;
    }

    private boolean processRecipe(CraftStationRecipe recipe) {
        if (level == null) return false;

        ++progress;
        craftingTime = recipe.getCraftingTime();
        if (Mth.floor(progress * this.getSpeedModifier()) < craftingTime) {
            return false;
        }
        ItemStack outputStack = outputInventory.getStackInSlot(0);
        ItemStack inputStack = inputInventory.getStackInSlot(0);
        ItemStack resultStack = recipe.getResultItem(this.level.registryAccess());
        ItemStack nbtResultStack = resultStack.copy();
        nbtResultStack.setTag(inputStack.getTag());
        if (!outputStack.isEmpty() && !ItemStack.isSameItemSameTags(outputStack, nbtResultStack)) return false;
        progress = 0;
        int mult = 1;
        int multChance = getMultChance();
        if (multChance > 0) {
            Random r = new Random();
            if (r.nextDouble() * 100 < multChance) mult = 2;
            if (r.nextDouble() * 100 < (multChance - 100)) mult = 3;
            if (r.nextDouble() * 100 < (multChance - 200)) mult = 4;
        }
        if (outputStack.isEmpty()) {
            ItemStack newResult = resultStack.copy();
            if (inputStack.getTag() != null) newResult.setTag(inputStack.getTag());
            newResult.setCount(newResult.getCount() * mult);
            outputInventory.setStackInSlot(0, newResult);
        } else if (ItemStack.isSameItem(outputStack, resultStack)) {
            for (int i = 0; i < mult; i++) {
                if (outputStack.getCount() < outputStack.getMaxStackSize()) outputStack.grow(resultStack.getCount());
            }
        }
        if (!inputStack.isEmpty()) inputStack.shrink(1);
        return true;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == Direction.DOWN) return this.outputOptional.cast();
            else return this.inputOptional.cast();
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
        this.outputOptional.invalidate();
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

    public LazyOptional<ItemStackHandler> getOutputOptional() {
        return this.outputOptional;
    }

    public LazyOptional<ItemStackHandler> getPokemonOptional() {
        return this.pokemonOptional;
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(2);
        inventory.setItem(0, inputInventory.getStackInSlot(0));
        inventory.setItem(1, outputInventory.getStackInSlot(0));
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CraftStationMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    private Optional<CraftStationRecipe> getMatchingRecipe(RecipeWrapper inventoryWrapper) {
        if (level == null) return Optional.empty();
        if (lastRecipeID != null) {
            Recipe<RecipeWrapper> recipe = ((CWRecipeManagerAccessor) level.getRecipeManager()).getRecipeMap(CraftStationRecipe.Type.INSTANCE).get(lastRecipeID);
            if (recipe instanceof CraftStationRecipe) {
                if (recipe.matches(inventoryWrapper, level) && PokeUtils.validWorkerType(this, ((CraftStationRecipe) recipe).getElementalType(), level)) {
                    return Optional.of((CraftStationRecipe) recipe);
                }
                if (ItemStack.isSameItem(recipe.getResultItem(this.level.registryAccess()), this.outputInventory.getStackInSlot(0))) {
                    return Optional.empty();
                }
            }
        }

        if (checkNewRecipe) {
            List<CraftStationRecipe> validRecipes = level.getRecipeManager().getRecipesFor(CraftStationRecipe.Type.INSTANCE, inventoryWrapper, level);
            CraftStationRecipe foundRecipe = null;
            for (CraftStationRecipe recipe : validRecipes) {
                if (PokeUtils.priorityWorkerType(this, recipe.getElementalType(), level, this.swapPriority)) {
                    foundRecipe = recipe;
                    break;
                }
            }
            if (foundRecipe == null) {
                for (CraftStationRecipe recipe : validRecipes) {
                    if (PokeUtils.priorityWorkerType(this, recipe.getElementalType(), level, !this.swapPriority)) {
                        foundRecipe = recipe;
                        break;
                    }
                }
            }
            if (foundRecipe != null) {
                ResourceLocation newRecipeID = foundRecipe.getId();
                if (lastRecipeID != null && !lastRecipeID.equals(newRecipeID)) {
                    craftingTime = 0;
                }
                lastRecipeID = newRecipeID;
                return Optional.of(foundRecipe);
            }
        }
        checkNewRecipe = false;
        return Optional.empty();
    }


    protected boolean canProcess(CraftStationRecipe recipe) {
        ItemStack resultStack = recipe.getResultItem(this.level.registryAccess());
        if (resultStack.isEmpty()) {
            return false;
        } else {
            int mult = 1;
            int multChance = getMultChance();
            if (multChance >= 100) mult = 2;
            if (multChance >= 200) mult = 3;
            return outputInventory.getStackInSlot(0).getCount() + (resultStack.getCount() * mult) <= resultStack.getMaxStackSize();
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        CompoundTag data = new CompoundTag();
        if (owner != null) data.putUUID("Owner", owner);
        data.put("InputInventory", this.inputInventory.serializeNBT());
        data.put("OutputInventory", this.outputInventory.serializeNBT());
        data.put("PokemonInventory", this.pokemonInventory.serializeNBT());
        data.putString("PrimaryType", this.primaryType != null ? this.primaryType.getName() : "");
        data.putString("SecondaryType", this.secondaryType != null ? this.secondaryType.getName() : "");
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

        if (data.contains("OutputInventory", Tag.TAG_COMPOUND)) {
            this.outputInventory.deserializeNBT(data.getCompound("OutputInventory"));
        }
        if (data.contains("PokemonInventory", Tag.TAG_COMPOUND)) {
            this.pokemonInventory.deserializeNBT(data.getCompound("PokemonInventory"));
            this.initializeWorker();
        }
        primaryType = ElementalTypes.INSTANCE.get(data.getString("PrimaryType"));
        secondaryType = ElementalTypes.INSTANCE.get(data.getString("SecondaryType"));
        craftingTime = data.getInt("CraftingTime");
        progress = data.getInt("Progress");
        swapPriority = data.getBoolean("SwapPriority");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblemon_farmers.craft_station");
    }

}
