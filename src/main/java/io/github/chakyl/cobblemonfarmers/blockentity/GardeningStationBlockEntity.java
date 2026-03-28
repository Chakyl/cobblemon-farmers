package io.github.chakyl.cobblemonfarmers.blockentity;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import cool.bot.botslib.util.Util;
import cool.bot.dewdropfarmland.utils.CropHandlerUtils;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.screen.GardeningStationMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;

import static io.github.chakyl.cobblemonfarmers.utils.PokeUtils.getItemFormPokemon;

public class GardeningStationBlockEntity extends StationBaseBlockEntity implements MenuProvider {
    protected final ContainerData data;
    private int progress = 0;
    private int actionTime;
    private ResourceLocation lastRecipeID;
    private boolean swapPriority = false;

    private final ItemStackHandler pokemonInventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
            initializeWorker();
        }
    };

    private final LazyOptional<ItemStackHandler> pokemonOptional = LazyOptional.of(() -> this.pokemonInventory);

    public GardeningStationBlockEntity(BlockPos pos, BlockState state) {
        super(CobblemonFarmersRegistery.BlockEntityRegistry.GARDENING_STATION.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> GardeningStationBlockEntity.this.progress;
                    case 1 -> GardeningStationBlockEntity.this.actionTime;
                    case 2 -> GardeningStationBlockEntity.this.swapPriority ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> GardeningStationBlockEntity.this.progress = pValue;
                    case 1 -> GardeningStationBlockEntity.this.actionTime = pValue;
                    case 2 -> GardeningStationBlockEntity.this.swapPriority = pValue == 1;
                }

            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    public boolean getPrioritySwapped() {
        return this.swapPriority;
    }

    public void setPrioritySwapped() {
        this.swapPriority = !this.swapPriority;

    }

    @Override
    public ItemStack getPokemonItem() {
        return this.pokemonInventory.getStackInSlot(0);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        super.tick(level, pos, state);
        if (!level.isClientSide() && hasWorker()) {
            ItemStack pokemonItem = getPokemonItem();
            if (pokemonItem == null || pokemonItem.isEmpty()) return;
            Pokemon pokemon = getItemFormPokemon(pokemonItem, this.level);
            ElementalType actionType = getActionType(pokemon);
            if (actionType != null) {
                ++progress;
                actionTime = getActionTime(actionType);
                int resolvedProgress = getActionProgress(actionType);
                if (resolvedProgress >= actionTime) {
                    runAction(actionType, pokemon);
                    progress = 0;
                }
            } else if (progress > 0) {
                --progress;
            }
        }
    }

    private void waterFarmland(int radius) {
        BlockPos centerPos = this.getBlockPos();
        for (BlockPos pos : BlockPos.betweenClosed(new BlockPos(centerPos.getX() - radius, centerPos.getY() - radius, centerPos.getZ() - radius), new BlockPos(centerPos.getX() + radius, centerPos.getY() + radius, centerPos.getZ() + radius))) {
            if (Util.isDryWaterable((ServerLevel) this.level, pos)) {
                Util.setMoist((ServerLevel) this.level, pos);
            }
        }
    }

    private void harvestFromRanchingStation(int radius, boolean fairy) {
        BlockPos centerPos = this.getBlockPos();
        for (BlockPos pos : BlockPos.betweenClosed(new BlockPos(centerPos.getX() - radius, centerPos.getY() - radius, centerPos.getZ() - radius), new BlockPos(centerPos.getX() + radius, centerPos.getY() + radius, centerPos.getZ() + radius))) {
            BlockEntity entity = this.level.getBlockEntity(pos);
            if (entity instanceof RanchingStationBlockEntity ranchingStationBlockEntity) {
                if (fairy) {
                    if (ranchingStationBlockEntity.getRanchingPower() > 1 && ranchingStationBlockEntity.canMagicShearToday(this.level) && ranchingStationBlockEntity.hasMagicShearDrops()) {
                        if (ranchingStationBlockEntity.magicShear(this.level, null)) {
                            this.level.playSound(null, this.getBlockPos(), CobblemonSounds.IMPACT_FAIRY, SoundSource.BLOCKS, 1.0F, 0.9F);
                            return;
                        }
                    }
                } else {
                    if (ranchingStationBlockEntity.canForageToday(this.level) && ranchingStationBlockEntity.hasForageRecipe()) {
                        if (ranchingStationBlockEntity.harvestForage(this.level)) {
                            this.level.playSound(null, this.getBlockPos(), CobblemonSounds.IMPACT_NORMAL, SoundSource.BLOCKS, 1.0F, 0.9F);
                            return;
                        }
                    }
                    if (ranchingStationBlockEntity.getRanchingPower() > 1 && ranchingStationBlockEntity.canMilkToday(this.level) && ranchingStationBlockEntity.hasMilkingRecipe()) {
                        if (ranchingStationBlockEntity.milkPokemon(this.level, null)) {
                            this.level.playSound(null, this.getBlockPos(), CobblemonSounds.IMPACT_NORMAL, SoundSource.BLOCKS, 1.0F, 0.9F);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void generateExp(int radius) {
        List<PokemonEntity> nearbyMons = level.getEntitiesOfClass(PokemonEntity.class, new AABB(this.getBlockPos()).inflate(radius));
        int spawnedXP = 0;
        for (PokemonEntity pokemon : nearbyMons) {
            if (!pokemon.isBattling() && !pokemon.isDeadOrDying() & pokemon.getPokemon().getOwnerPlayer() == null) {
                Block.popResourceFromFace(level, this.getBlockPos(), Direction.UP, CobblemonItems.EXPERIENCE_CANDY_S.getDefaultInstance());
                pokemon.kill();
                ++spawnedXP;
            }
        }
        if (spawnedXP < radius / 2) {
            for (int i = 0; i < (radius / 2) - spawnedXP; i++) {
                Block.popResourceFromFace(level, this.getBlockPos(), Direction.UP, CobblemonItems.EXPERIENCE_CANDY_XS.getDefaultInstance());
            }
        }
    }

    private void runAction(ElementalType actionType, Pokemon pokemon) {
        ElementalTypes types = ElementalTypes.INSTANCE;
        int radius = getAoeRadius(pokemon.getLevel());
        if (CobblemonFarmers.GROWTH_EDITION_INSTALLED && actionType.equals(types.getGRASS())) {
            CropHandlerUtils.growCropsInRadius((ServerLevel) this.level, this.getBlockPos(), this.getLevel().getRandom(), radius);
            this.level.playSound(null, this.getBlockPos(), CobblemonSounds.IMPACT_GRASS, SoundSource.BLOCKS, 1.0F, 0.9F);
        } else if (CobblemonFarmers.GROWTH_EDITION_INSTALLED && actionType.equals(types.getWATER())) {
            waterFarmland(radius);
            this.level.playSound(null, this.getBlockPos(), CobblemonSounds.IMPACT_WATER, SoundSource.BLOCKS, 1.0F, 0.9F);
        } else if (actionType.equals(types.getNORMAL())) {
            harvestFromRanchingStation(radius, false);
        } else if (actionType.equals(types.getFAIRY())) {
            harvestFromRanchingStation(radius, true);
        }else if (actionType.equals(types.getDARK())) {
            generateExp(radius * 4);
            this.level.playSound(null, this.getBlockPos(), CobblemonSounds.IMPACT_DARK, SoundSource.BLOCKS, 1.0F, 0.9F);
        }
    }

    private ElementalType getActionType(Pokemon pokemon) {
        ElementalType secondaryType = pokemon.getSecondaryType();
        boolean hasSecondary = secondaryType != null && getScalingStat(secondaryType) != null;
        if (this.swapPriority && hasSecondary) return secondaryType;
        if (getScalingStat(pokemon.getPrimaryType()) != null) return pokemon.getPrimaryType();
        if (hasSecondary) return secondaryType;
        return null;
    }

    private int getActionTime(ElementalType type) {
        ElementalTypes types = ElementalTypes.INSTANCE;
        if (CobblemonFarmers.GROWTH_EDITION_INSTALLED && type.equals(types.getGRASS())) return 24000;
        if (type.equals(types.getWATER())) return 600;
        if (type.equals(types.getNORMAL())) return 300;
        if (type.equals(types.getFAIRY())) return 800;
        if (type.equals(types.getDARK())) return 20000;
        return -1;
    }

    private Stats getScalingStat(ElementalType type) {
        ElementalTypes types = ElementalTypes.INSTANCE;
        if (CobblemonFarmers.GROWTH_EDITION_INSTALLED && type.equals(types.getGRASS())) return Stats.SPEED;
        if (type.equals(types.getWATER())) return Stats.SPECIAL_ATTACK;
        if (type.equals(types.getNORMAL())) return Stats.SPEED;
        if (type.equals(types.getFAIRY())) return Stats.SPECIAL_ATTACK;
        if (type.equals(types.getDARK())) return Stats.HP;
        return null;
    }

    public double getSpeedModifier() {
        ItemStack pokemonItem = getPokemonItem();
        if (pokemonItem == null || pokemonItem.isEmpty()) return 0.0;
        Pokemon pokemon = getItemFormPokemon(pokemonItem, this.level);
        ElementalType type = getActionType(pokemon);
        return type == null ? 0 : getSpeedModifier(getScalingStat(type));
    }

    public int getAoeRadius() {
        ItemStack pokemonItem = getPokemonItem();
        if (pokemonItem == null || pokemonItem.isEmpty()) return 0;
        Pokemon pokemon = getItemFormPokemon(pokemonItem, this.level);
        if (getActionType(pokemon) == null) return 0;
        return getAoeRadius(pokemon.getLevel());
    }

    public int getAoeRadius(int level) {
        return Mth.clamp(level / 10, 1, 10);
    }

    private int getActionProgress(ElementalType type) {
        return Mth.floor(progress * getSpeedModifier(getScalingStat(type)));
    }

    @Override
    public boolean hasWorker() {
        return !this.pokemonInventory.getStackInSlot(0).isEmpty();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {

        return super.getCapability(cap, side);
    }

    public <T> LazyOptional<T> getPokemonCapability(Capability<T> cap) {
        return this.pokemonOptional.cast();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.pokemonOptional.invalidate();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null)
            this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public LazyOptional<ItemStackHandler> getPokemonOptional() {
        return this.pokemonOptional;
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(2);
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
        if (owner != null) data.putUUID("Owner", owner);
        data.put("PokemonInventory", this.pokemonInventory.serializeNBT());
        data.putInt("ActionTime", actionTime);
        data.putInt("Progress", progress);
        data.putBoolean("SwapPriority", swapPriority);
        tag.put(CobblemonFarmers.MODID, data);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        CompoundTag data = pTag.getCompound(CobblemonFarmers.MODID);
        owner = data.hasUUID("Owner") ? data.getUUID("Owner") : null;
        if (data.contains("PokemonInventory", Tag.TAG_COMPOUND)) {
            this.pokemonInventory.deserializeNBT(data.getCompound("PokemonInventory"));
            this.initializeWorker();
        }

        actionTime = data.getInt("ActionTime");
        progress = data.getInt("Progress");
        swapPriority = data.getBoolean("SwapPriority");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblemon_farmers.gardening_station");
    }

}
