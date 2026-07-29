package io.github.chakyl.cobblemonfarmers.blockentity;

import com.cobblemon.mod.common.api.types.ElementalTypes;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.screen.CrystalBallMenu;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CrystalBallBlockEntity extends StationBaseBlockEntity implements MenuProvider {
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
    private final LazyOptional<ItemStackHandler> pokemonOptional = LazyOptional.of(() -> this.pokemonInventory);

    public CrystalBallBlockEntity(BlockPos pos, BlockState state) {
        super(CobblemonFarmersRegistery.BlockEntityRegistry.CRYSTAL_BALL.get(), pos, state);
        this.checkNewRecipe = true;
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> getDataSendableTime(CrystalBallBlockEntity.this.progress);
                    case 1 -> getDataSendableTime(CrystalBallBlockEntity.this.craftingTime);
                    case 2 -> Mth.floor(CrystalBallBlockEntity.this.speedModifier * 100);
                    case 3 -> CrystalBallBlockEntity.this.multChance;
                    case 4 -> CrystalBallBlockEntity.this.swapPriority ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> CrystalBallBlockEntity.this.progress = pValue;
                    case 1 -> CrystalBallBlockEntity.this.craftingTime = pValue;
                    case 2 -> CrystalBallBlockEntity.this.speedModifier = (double) pValue / 100;
                    case 3 -> CrystalBallBlockEntity.this.multChance = pValue;
                    case 4 -> CrystalBallBlockEntity.this.swapPriority = pValue == 1;
                }

            }

            @Override
            public int getCount() {
                return 5;
            }
        };
    }

    public ItemStack getRenderItem() {
        ItemStack inputStack = this.inputInventory.getStackInSlot(0);
        if (!inputStack.isEmpty()) return inputStack;
        return ItemStack.EMPTY;
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
//        if (!level.isClientSide()) {
//            if (hasWorker && hasInput) {
//                Optional<CraftStationRecipe> recipe = this.getMatchingRecipe(new RecipeWrapper(this.inputInventory));
//                if (recipe.isPresent() && this.canProcess(recipe.get()) && PokeUtils.validWorkerType(this, recipe.get().getElementalType(), level)) {
//                    didInventoryChange = this.processRecipe(recipe.get());
//                    if (this.speedModifier <= 0) {
//                        this.fetchSpeedModifier(recipe.get().getSpeedStat());
//                        this.fetchMultChance(recipe.get().getMultStat());
//                    }
//                } else {
//                    recipe.ifPresent(craftStationRecipe -> PokeUtils.validWorkerType(this, craftStationRecipe.getElementalType(), level));
//                }
//            } else if (this.progress > 0) {
//                this.progress = Mth.clamp(this.progress - 2, 0, this.craftingTime);
//                this.speedModifier = 0;
//                this.multChance = 0;
//            }
//            if (didInventoryChange) {
//                setChanged();
//                level.sendBlockUpdated(pos, state, state, 3);
//            }
//        }

    }

    @Override
    public boolean hasWorker() {
        return !this.pokemonInventory.getStackInSlot(0).isEmpty() && this.primaryType != null;
    }


    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return this.inputOptional.cast();
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
        return new CrystalBallMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        CompoundTag data = new CompoundTag();
        if (owner != null) data.putUUID("Owner", owner);
        data.put("InputInventory", this.inputInventory.serializeNBT());
        data.put("PokemonInventory", this.pokemonInventory.serializeNBT());
        data.putString("PrimaryType", this.primaryType != null ? this.primaryType.getName() : "");
        data.putString("SecondaryType", this.secondaryType != null ? this.secondaryType.getName() : "");
        data.putInt("CraftingTime", craftingTime);
        data.putInt("Progress", progress);
        data.putBoolean("SwapPriority", swapPriority);
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
        if (data.contains("PokemonInventory", Tag.TAG_COMPOUND)) {
            CompoundTag newInvTag = data.getCompound("PokemonInventory");
            if (!this.pokemonInventory.serializeNBT().equals(newInvTag)) {
                this.pokemonInventory.deserializeNBT(newInvTag);
                this.initializeWorker();
            }
        }
        primaryType = ElementalTypes.INSTANCE.get(data.getString("PrimaryType"));
        secondaryType = ElementalTypes.INSTANCE.get(data.getString("SecondaryType"));
        craftingTime = data.getInt("CraftingTime");
        progress = data.getInt("Progress");
        swapPriority = data.getBoolean("SwapPriority");
        publicContract = data.getBoolean("PublicContract");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblemon_farmers.crystal_ball");
    }

}
