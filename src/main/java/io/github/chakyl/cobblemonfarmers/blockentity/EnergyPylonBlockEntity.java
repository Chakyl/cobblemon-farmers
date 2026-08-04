package io.github.chakyl.cobblemonfarmers.blockentity;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.mixin.CWRecipeManagerAccessor;
import io.github.chakyl.cobblemonfarmers.recipe.EnergyPylonRecipe;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.screen.EnergyPylonMenu;
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
import java.util.List;
import java.util.Optional;

import static io.github.chakyl.cobblemonfarmers.utils.GeneralUtils.getBetweenManhattan;
import static io.github.chakyl.cobblemonfarmers.utils.GeneralUtils.isSamePos;

public class EnergyPylonBlockEntity extends StationBaseBlockEntity implements MenuProvider {
    public static int CRAFTING_TIME = 2400;
    protected final ContainerData data;
    private int progress = 0;
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

    public EnergyPylonBlockEntity(BlockPos pos, BlockState state) {
        super(CobblemonFarmersRegistery.BlockEntityRegistry.ENERGY_PYLON.get(), pos, state);
        this.checkNewRecipe = true;
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> getDataSendableTime(EnergyPylonBlockEntity.this.progress);
                    case 1 -> getDataSendableTime(CRAFTING_TIME);
                    case 2 -> Mth.floor(EnergyPylonBlockEntity.this.speedModifier * 100);
                    case 3 -> Mth.floor(EnergyPylonBlockEntity.this.bonusSpeed * 100);
                    case 4 -> EnergyPylonBlockEntity.this.aoeRadius;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> EnergyPylonBlockEntity.this.progress = pValue;
                    case 1 -> CRAFTING_TIME = pValue;
                    case 2 -> EnergyPylonBlockEntity.this.speedModifier = (double) pValue / 100;
                    case 3 -> EnergyPylonBlockEntity.this.bonusSpeed = (double) pValue / 100;
                    case 4 -> EnergyPylonBlockEntity.this.aoeRadius = pValue;
                }

            }

            @Override
            public int getCount() {
                return 5;
            }
        };
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
                Optional<EnergyPylonRecipe> recipe = this.getMatchingRecipe(new RecipeWrapper(this.inputInventory));
                if (recipe.isPresent() && this.canProcess(recipe.get()) && PokeUtils.validWorkerType(this, ElementalTypes.INSTANCE.getELECTRIC(), level)) {
                    didInventoryChange = this.processRecipe(recipe.get());
                    if (this.speedModifier <= 0) {
                        this.fetchSpeedModifier(Stats.SPECIAL_ATTACK);
                        this.fetchAoeRadius(Stats.HP);
                    }
                } else {
                    recipe.ifPresent(energyPylonRecipe -> PokeUtils.validWorkerType(this, ElementalTypes.INSTANCE.getELECTRIC(), level));
                }
            } else if (this.progress > 0) {
                this.progress = Mth.clamp(this.progress - 2, 0, CRAFTING_TIME);
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

    private boolean processRecipe(EnergyPylonRecipe recipe) {
        if (level == null) return false;
        ++progress;
        if (Mth.floor(progress * this.getBoostedSpeedModifier()) < CRAFTING_TIME) {
            return false;
        }
        if (buffNearestStation(this.getBlockPos(), this.aoeRadius, recipe)) {
            ItemStack outputStack = outputInventory.getStackInSlot(0);
            ItemStack inputStack = inputInventory.getStackInSlot(0);
            ItemStack resultStack = recipe.getResultItem(this.level.registryAccess());
            ItemStack nbtResultStack = resultStack.copy();
            if (!outputStack.isEmpty() && !ItemStack.isSameItemSameTags(outputStack, nbtResultStack)) return false;
            if (!inputStack.isEmpty() && Math.random() < recipe.getConsumeChance()) {
                inputStack.shrink(1);
                if (outputStack.isEmpty()) {
                    ItemStack newResult = resultStack.copy();
                    if (inputStack.getTag() != null) newResult.setTag(inputStack.getTag());
                    newResult.setCount(newResult.getCount());
                    outputInventory.setStackInSlot(0, newResult);
                } else if (ItemStack.isSameItem(outputStack, resultStack)) {
                    if (outputStack.getCount() < outputStack.getMaxStackSize())
                        outputStack.grow(resultStack.getCount());
                }
                progress = 0;
                this.bonusSpeed = 0;
                return true;
            }
        } else {
            progress -= 20;
            return false;
        }
        return true;
    }

    private boolean buffNearestStation(BlockPos centerPos, int radius, EnergyPylonRecipe recipe) {
        for (BlockPos pos : getBetweenManhattan(centerPos, radius, radius)) {
            if (!isSamePos(pos, centerPos) && this.level.getBlockEntity(pos) instanceof StationBaseBlockEntity stationBaseBlockEntity && stationBaseBlockEntity.hasWorker() && !(stationBaseBlockEntity instanceof RanchingStationBlockEntity)) {
                if (stationBaseBlockEntity.canReceiveBonusSpeed()) {
                    stationBaseBlockEntity.setBonusSpeed((double) Mth.floor(recipe.getBonusSpeed() * 100.0) / 100);
                    return true;
                }
            }
        }
        return false;
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
        return new EnergyPylonMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    private Optional<EnergyPylonRecipe> getMatchingRecipe(RecipeWrapper inventoryWrapper) {
        if (level == null) return Optional.empty();
        if (lastRecipeID != null) {
            Recipe<RecipeWrapper> recipe = ((CWRecipeManagerAccessor) level.getRecipeManager()).getRecipeMap(EnergyPylonRecipe.Type.INSTANCE).get(lastRecipeID);
            if (recipe instanceof EnergyPylonRecipe) {
                if (recipe.matches(inventoryWrapper, level)) {
                    return Optional.of((EnergyPylonRecipe) recipe);
                }
            }
        }
        if (checkNewRecipe) {
            List<EnergyPylonRecipe> validRecipes = level.getRecipeManager().getRecipesFor(EnergyPylonRecipe.Type.INSTANCE, inventoryWrapper, level);
            EnergyPylonRecipe foundRecipe = null;
            for (EnergyPylonRecipe recipe : validRecipes) {
                foundRecipe = recipe;
                break;
            }
            if (foundRecipe == null) {
                for (EnergyPylonRecipe recipe : validRecipes) {
                    foundRecipe = recipe;
                    break;
                }
            }
            if (foundRecipe != null) {
                lastRecipeID = foundRecipe.getId();
                return Optional.of(foundRecipe);
            }
        }
        checkNewRecipe = false;
        return Optional.empty();
    }

    protected boolean canProcess(EnergyPylonRecipe recipe) {
        ItemStack resultStack = recipe.getResultItem(this.level.registryAccess());

        if (resultStack.isEmpty()) {
            return false;
        } else {
            int mult = 1;
            int multChance = getBoostedMultChance();
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
        data.putInt("Progress", progress);
        ;
        data.putInt("BonusMult", this.bonusMult);
        data.putDouble("BonusSpeed", this.bonusSpeed);
        data.putBoolean("PublicContract", publicContract);
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
            CompoundTag newInvTag = data.getCompound("PokemonInventory");
            if (!this.pokemonInventory.serializeNBT().equals(newInvTag)) {
                this.pokemonInventory.deserializeNBT(newInvTag);
                this.initializeWorker();
            }
        }
        primaryType = ElementalTypes.INSTANCE.get(data.getString("PrimaryType"));
        secondaryType = ElementalTypes.INSTANCE.get(data.getString("SecondaryType"));
        progress = data.getInt("Progress");
        bonusMult = data.getInt("BonusMult");
        bonusSpeed = data.getDouble("BonusSpeed");
        publicContract = data.getBoolean("PublicContract");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblemon_farmers.energy_pylon");
    }

}
