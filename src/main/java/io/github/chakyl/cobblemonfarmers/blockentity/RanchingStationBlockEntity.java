package io.github.chakyl.cobblemonfarmers.blockentity;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.api.drop.DropEntry;
import com.cobblemon.mod.common.api.drop.DropTable;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.block.RanchingStationBlock;
import io.github.chakyl.cobblemonfarmers.recipe.RanchingStationForageRecipe;
import io.github.chakyl.cobblemonfarmers.recipe.RanchingStationMilkingRecipe;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.screen.RanchingStationMenu;
import io.github.chakyl.cobblemonfarmers.tag.CobblemonFarmersTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static io.github.chakyl.cobblemonfarmers.utils.PokeUtils.getItemFormPokemon;
import static io.github.chakyl.cobblemonfarmers.utils.PokeUtils.getSpeciesFromItemFormPokemon;
import static io.github.chakyl.cobblemonfarmers.utils.RanchingStationUtils.compareDay;
import static io.github.chakyl.cobblemonfarmers.utils.RanchingStationUtils.getDay;

public class RanchingStationBlockEntity extends StationBaseBlockEntity implements MenuProvider {
    protected final ContainerData data;
    private int dayLastForaged;
    private int dayLastMilked;
    private int dayLastMagicSheared;
    private int dayLastFed;

    private final ItemStackHandler inputInventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    };

    private final ItemStackHandler pokemonInventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
            initializeWorker();
        }
    };

    private final LazyOptional<ItemStackHandler> inputOptional = LazyOptional.of(() -> this.inputInventory);
    private final LazyOptional<ItemStackHandler> pokemonOptional = LazyOptional.of(() -> this.pokemonInventory);

    public RanchingStationBlockEntity(BlockPos pos, BlockState state) {
        super(CobblemonFarmersRegistery.BlockEntityRegistry.RANCHING_STATION.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> RanchingStationBlockEntity.this.dayLastMagicSheared;
                    case 1 -> RanchingStationBlockEntity.this.dayLastForaged;
                    case 2 -> RanchingStationBlockEntity.this.dayLastMilked;
                    case 3 -> RanchingStationBlockEntity.this.dayLastFed;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> RanchingStationBlockEntity.this.dayLastMagicSheared = pValue;
                    case 1 -> RanchingStationBlockEntity.this.dayLastForaged = pValue;
                    case 2 -> RanchingStationBlockEntity.this.dayLastMilked = pValue;
                    case 3 -> RanchingStationBlockEntity.this.dayLastFed = pValue;
                }

            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    public void generateParticles(ServerLevel level, BlockPos pos, SimpleParticleType particleType) {
        level.sendParticles(
                particleType,
                pos.getX(),
                pos.getY() + 1,
                pos.getZ(),
                8,
                0.2 * Mth.randomBetween(level.random, -2, 2),
                0.2 * Mth.randomBetween(level.random, -2, 2),
                0.2 * Mth.randomBetween(level.random, -2, 2),
                0.1
        );
    }

    @Override
    public ItemStack getPokemonItem() {
        return this.pokemonInventory.getStackInSlot(0);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        boolean didInventoryChange = false;
        super.tick(level, pos, state);
        if (!level.isClientSide() && level.getGameTime() % 20 == 0) {
            if (this.hasWorker()) {
            }
            if (didInventoryChange) {
                setChanged();
            }
        }
    }


    @Override
    public boolean hasWorker() {
        return !this.pokemonInventory.getStackInSlot(0).isEmpty();
    }

    public boolean hasInput() {
        return !this.inputInventory.getStackInSlot(0).isEmpty();
    }

    public void handleInteraction(Level level, ServerPlayer pPlayer, BlockPos pPos, Item item) {
        if (this.hasWorker()) {
            if (item == Items.AIR && canForageToday(level) && hasForageRecipe()) {
                if (harvestForage(level)) return;
            } else if (item.getDefaultInstance().is(CobblemonFarmersTags.MAGIC_SHEARS_RANCHING_STATION)) {
                if (this.getRanchingPower() < 5) {
                    pPlayer.displayClientMessage(
                            Component.translatable("message.cobblemon_farmers.ranching_station.needs_hearts").withStyle(ChatFormatting.RED),
                            true
                    );
                } else if (!canMagicShearToday(level)) {
                    pPlayer.displayClientMessage(
                            Component.translatable("message.cobblemon_farmers.ranching_station.too_soon").withStyle(ChatFormatting.RED),
                            true
                    );
                } else if (!hasMagicShearDrops()) {
                    pPlayer.displayClientMessage(
                            Component.translatable("message.cobblemon_farmers.ranching_station.cannot_be_magic_sheared").withStyle(ChatFormatting.RED),
                            true
                    );
                } else if (magicShear(level, pPlayer)) return;
            } else if (item.getDefaultInstance().is(CobblemonFarmersTags.MILKS_RANCHING_STATION)) {
                if (this.getRanchingPower() < 1) {
                    pPlayer.displayClientMessage(
                            Component.translatable("message.cobblemon_farmers.ranching_station.needs_hearts").withStyle(ChatFormatting.RED),
                            true
                    );
                } else if (!canMilkToday(level)) {
                    pPlayer.displayClientMessage(
                            Component.translatable("message.cobblemon_farmers.ranching_station.too_soon").withStyle(ChatFormatting.RED),
                            true
                    );
                } else if (!hasMilkingRecipe()) {
                    pPlayer.displayClientMessage(
                            Component.translatable("message.cobblemon_farmers.ranching_station.cannot_be_milked").withStyle(ChatFormatting.RED),
                            true
                    );
                } else if (milkPokemon(level, pPlayer)) return;
            }
        }
        NetworkHooks.openScreen(pPlayer, this, pPos);
    }

    private RanchingStationForageRecipe getForageRecipe() {
        List<RanchingStationForageRecipe> validRecipes = level.getRecipeManager().getRecipesFor(RanchingStationForageRecipe.Type.INSTANCE, new RecipeWrapper(this.pokemonInventory), level);
        for (RanchingStationForageRecipe recipe : validRecipes) {
            return recipe;
        }
        return null;
    }

    public boolean hasForageRecipe() {
        return hasWorker() && getForageRecipe() != null;
    }

    public boolean canForageToday(Level level) {
        return compareDay(getDay(level), this.dayLastForaged, 1);
    }

    public boolean harvestForage(Level level) {
        RanchingStationForageRecipe currentRecipe = getForageRecipe();
        if (currentRecipe != null) {
            this.dayLastForaged = getDay(level);
            List<ItemStack> drops = currentRecipe.getScaledDrops(this.getRanchingPower());
            if (!drops.isEmpty()) {
                BlockPos pos = this.getBlockPos();
                for (ItemStack drop : drops) {
                    Block.popResourceFromFace(level, pos, this.getBlockState().getValue(RanchingStationBlock.FACING), drop.copy());
                }
                this.level.playSound(null, this.getBlockPos(), CobblemonSounds.BERRY_HARVEST, SoundSource.BLOCKS, 1.0F, 0.9F);
                generateParticles((ServerLevel) level, pos, ParticleTypes.WAX_ON);
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    private DropTable getMagicShearDrops() {
        if (!this.hasWorker()) return null;
        Species species = Objects.requireNonNull(PokemonSpecies.INSTANCE.getByName(getSpeciesFromItemFormPokemon(this.pokemonInventory.getStackInSlot(0), level)));
        return species.getDrops();
    }

    public boolean hasMagicShearDrops() {
        return hasWorker() && !getMagicShearDrops().getEntries().isEmpty();
    }

    public boolean canMagicShearToday(Level level) {
        return compareDay(getDay(level), this.dayLastMagicSheared, 1);
    }

    public boolean magicShear(Level level, Player player) {
        DropTable dropTable = getMagicShearDrops();
        if (dropTable != null) {
            this.dayLastMagicSheared = getDay(level);
            List<DropEntry> drops = dropTable.getDrops(dropTable.getAmount());
            this.level.playSound(null, this.getBlockPos(), SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!drops.isEmpty()) {
                BlockPos pos = this.getBlockPos();
                for (DropEntry drop : drops) {
                    drop.drop(null, (ServerLevel) level, this.getBlockPos().getCenter(), null);
                }
                generateParticles((ServerLevel) level, pos, ParticleTypes.WAX_ON);
                generateParticles((ServerLevel) level, pos, ParticleTypes.WAX_OFF);
                return true;
            }
        }
        if (player != null) {
            player.displayClientMessage(
                    Component.translatable("message.cobblemon_farmers.ranching_station.no_drops"),
                    true
            );
        }
        return true;
    }

    private RanchingStationMilkingRecipe getMilkingRecipe() {
        List<RanchingStationMilkingRecipe> validRecipes = level.getRecipeManager().getRecipesFor(RanchingStationMilkingRecipe.Type.INSTANCE, new RecipeWrapper(this.pokemonInventory), level);
        for (RanchingStationMilkingRecipe recipe : validRecipes) {
            return recipe;
        }
        return null;
    }

    public boolean hasMilkingRecipe() {
        return hasWorker() && getMilkingRecipe() != null;
    }

    public boolean canMilkToday(Level level) {
        return compareDay(getDay(level), this.dayLastMilked, 1);
    }

    public boolean milkPokemon(Level level, Player player) {
        RanchingStationMilkingRecipe currentRecipe = getMilkingRecipe();
        if (currentRecipe != null) {
            this.dayLastMilked = getDay(level);
            ItemStack milk = currentRecipe.getMilk(this.getRanchingPower());
            if (!milk.isEmpty()) {
                BlockPos pos = this.getBlockPos();
                if (player != null && currentRecipe.getIsBucketConsumed()) {
                    ItemUtils.createFilledResult(player.getMainHandItem(), player, milk.copy(), true);
                }
                else {
                    Block.popResourceFromFace(level, pos, this.getBlockState().getValue(RanchingStationBlock.FACING), milk.copy());
                }
                this.level.playSound(null, this.getBlockPos(), SoundEvents.COW_MILK, SoundSource.BLOCKS, 1.0F, 1.0F);
                generateParticles((ServerLevel) level, pos, ParticleTypes.WAX_ON);
                return true;
            }
        } else {
            return false;
        }
        return false;
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

    public int getRanchingPower() {
        ItemStack pokemonItem = getPokemonItem();
        if (!pokemonItem.isEmpty()) {
            Pokemon pokemon = getItemFormPokemon(pokemonItem, this.level);
            int friendshipHearts = (int) (5 * ((double) pokemon.getFriendship() / Cobblemon.config.getMaxPokemonFriendship()));
            int hpHearts = (int) (5 * ((double) pokemon.getStat(Stats.HP) / ((double) (255 + 31 + 252) / 2)));  // Base Stat Max + IV + EV
//            CobblemonFarmers.LOGGER.info("Friendship Hearts: " + friendshipHearts + " HP Hearts: " + hpHearts);
            return Math.min(friendshipHearts + hpHearts, 10);
        }
        return 0;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        CompoundTag data = new CompoundTag();
        if (owner != null) data.putUUID("Owner", owner);
        data.put("InputInventory", this.inputInventory.serializeNBT());
        data.put("PokemonInventory", this.pokemonInventory.serializeNBT());
        data.putInt("DayLastMagicSheared", dayLastMagicSheared);
        data.putInt("DayLastForaged", dayLastForaged);
        data.putInt("DayLastMilked", dayLastMilked);
        data.putInt("DayLastFed", dayLastFed);
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
        dayLastMagicSheared = data.getInt("DayLastMagicSheared");
        dayLastForaged = data.getInt("DayLastForaged");
        dayLastMilked = data.getInt("DayLastMilked");
        dayLastFed = data.getInt("DayLastFed");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblemon_farmers.ranching_station");
    }

}
