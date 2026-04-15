package io.github.chakyl.cobblemonfarmers.blockentity;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.block.BerryBlock;
import com.cobblemon.mod.common.block.entity.BerryBlockEntity;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import cool.bot.botslib.util.Util;
import cool.bot.dewdropfarmland.utils.CropHandlerUtils;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.block.GardeningStationBlock;
import io.github.chakyl.cobblemonfarmers.block.MysteryMineBlock;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.screen.GardeningStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.chakyl.cobblemonfarmers.utils.PokeUtils.getItemFormPokemon;
import static io.github.chakyl.cobblemonfarmers.utils.PokeUtils.insertIntoFacingOrPopOut;

public class GardeningStationBlockEntity extends StationBaseBlockEntity implements MenuProvider {
    protected final ContainerData data;
    private int progress = 0;
    private int actionTime;
    private ResourceLocation lastRecipeID;
    private boolean swapPriority = false;
    private int aoeRadius;

    private final ItemStackHandler pokemonInventory = new ItemStackHandler(1) {
        private ItemStack previousWorker;

        @Override
        protected void onContentsChanged(int slot) {
            ItemStack current = getStackInSlot(slot);
            if (previousWorker == null || !ItemStack.matches(current, previousWorker)) {
                this.previousWorker = current.copy();
                super.onContentsChanged(slot);
                setChanged();
                initializeWorker();
            }
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
                    case 2 -> Mth.floor(GardeningStationBlockEntity.this.speedModifier * 100);
                    case 3 -> GardeningStationBlockEntity.this.aoeRadius;
                    case 4 -> GardeningStationBlockEntity.this.swapPriority ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> GardeningStationBlockEntity.this.progress = pValue;
                    case 1 -> GardeningStationBlockEntity.this.actionTime = pValue;
                    case 2 -> GardeningStationBlockEntity.this.speedModifier = (double) pValue / 100;
                    case 3 -> GardeningStationBlockEntity.this.aoeRadius = pValue;
                    case 4 -> GardeningStationBlockEntity.this.swapPriority = pValue == 1;
                }

            }

            @Override
            public int getCount() {
                return 5;
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
            if (this.getSpeedModifier() == 0.0 && getActionType() != null) {
                ElementalType type = getActionType();
                this.fetchSpeedModifier(getScalingStat(type));
                this.fetchAoeRadius();
            }
            ItemStack pokemonItem = getPokemonItem();
            if (pokemonItem == null || pokemonItem.isEmpty()) return;
            ElementalType actionType = getActionType();
            if (actionType != null) {
                ++progress;
                actionTime = getActionTime(actionType);
                int resolvedProgress = getActionProgress(actionType);
                if (resolvedProgress >= actionTime) {
                    Pokemon pokemon = getItemFormPokemon(pokemonItem, this.level);
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
                if (ranchingStationBlockEntity.isHungry(this.level)) return;
                if (fairy) {
                    if (ranchingStationBlockEntity.getRanchingPower() > 1 && ranchingStationBlockEntity.canMagicShearToday(this.level) && ranchingStationBlockEntity.hasMagicShearDrops()) {
                        if (ranchingStationBlockEntity.magicShear(this.level, null, centerPos, this.getBlockState().getValue(GardeningStationBlock.FACING))) {
                            this.level.playSound(null, this.getBlockPos(), CobblemonSounds.IMPACT_FAIRY, SoundSource.BLOCKS, 1.0F, 0.9F);
                            return;
                        }
                    }
                } else {
                    if (ranchingStationBlockEntity.canForageToday(this.level) && ranchingStationBlockEntity.hasForageRecipe()) {
                        if (ranchingStationBlockEntity.harvestForage(this.level, centerPos, this.getBlockState().getValue(GardeningStationBlock.FACING))) {
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

    private void harvestNearbyBerries(int radius) {
        BlockPos centerPos = this.getBlockPos();
        boolean ateBerry = this.level.getRandom().nextDouble() < 1 - (radius * 0.1);
        for (BlockPos pos : BlockPos.betweenClosed(new BlockPos(centerPos.getX() - radius, centerPos.getY() - radius, centerPos.getZ() - radius), new BlockPos(centerPos.getX() + radius, centerPos.getY() + radius, centerPos.getZ() + radius))) {
            if (this.level.getBlockEntity(pos) instanceof BerryBlockEntity berryBlockEntity) {
                if (this.level.getBlockState(pos).getBlock() instanceof BerryBlock berryBlock && level.getBlockState(pos).hasProperty(BlockStateProperties.AGE_5)) {
                    if (level.getBlockState(pos).getValue(BlockStateProperties.AGE_5) == BerryBlock.FRUIT_AGE) {
                        if (!ateBerry) {
                            List<ItemStack> mergedStacks = berryBlockEntity.berryAndGrowthPoint$common().stream()
                                    .collect(Collectors.groupingBy(
                                            pair -> pair.getFirst().item(),
                                            Collectors.summingInt(pair -> 1)
                                    ))
                                    .entrySet().stream()
                                    .map(entry -> new ItemStack(entry.getKey(), entry.getValue()))
                                    .collect(Collectors.toList());
                            for (ItemStack itemStack : mergedStacks) {
                                insertIntoFacingOrPopOut(level, this.getBlockPos(), this.getBlockState().getValue(MysteryMineBlock.FACING), itemStack);
                            }
                            this.level.playSound(null, this.getBlockPos(), CobblemonSounds.IMPACT_FLYING, SoundSource.BLOCKS, 0.4F, 0.9F);
                        } else {
                            this.level.playSound(null, this.getBlockPos(), CobblemonSounds.BERRY_EAT, SoundSource.BLOCKS, 0.4F, 1.0F);
                        }
                        level.setBlock(pos, berryBlockEntity.getBlockState().setValue(BlockStateProperties.AGE_5, BerryBlock.MATURE_AGE), 2);
                        return;
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
                insertIntoFacingOrPopOut(level, this.getBlockPos(), this.getBlockState().getValue(MysteryMineBlock.FACING), CobblemonItems.EXPERIENCE_CANDY_S.getDefaultInstance());
                pokemon.kill();
                ++spawnedXP;
            }
        }
        if (spawnedXP < radius / 2) {
            for (int i = 0; i < (radius / 2) - spawnedXP; i++) {
                insertIntoFacingOrPopOut(level, this.getBlockPos(), this.getBlockState().getValue(MysteryMineBlock.FACING), CobblemonItems.EXPERIENCE_CANDY_XS.getDefaultInstance());
            }
        }
    }

    private void runAction(ElementalType actionType, Pokemon pokemon) {
        ElementalTypes types = ElementalTypes.INSTANCE;
        int radius = this.getAoeRadius();
        if (CobblemonFarmers.GROWTH_EDITION_INSTALLED && actionType.equals(types.getGRASS())) {
            CropHandlerUtils.growCropsInRadius((ServerLevel) this.level, this.getBlockPos(), this.getLevel().getRandom(), radius);
            this.level.playSound(null, this.getBlockPos(), CobblemonSounds.IMPACT_GRASS, SoundSource.BLOCKS, 0.5F, 0.9F);
        } else if (CobblemonFarmers.GROWTH_EDITION_INSTALLED && actionType.equals(types.getWATER())) {
            waterFarmland(radius);
            this.level.playSound(null, this.getBlockPos(), CobblemonSounds.IMPACT_WATER, SoundSource.BLOCKS, 0.5F, 0.9F);
        } else if (actionType.equals(types.getFLYING())) {
            harvestNearbyBerries(radius);
        } else if (actionType.equals(types.getNORMAL())) {
            harvestFromRanchingStation(radius, false);
        } else if (actionType.equals(types.getFAIRY())) {
            harvestFromRanchingStation(radius, true);
        } else if (actionType.equals(types.getDARK())) {
            generateExp(radius * 4);
            this.level.playSound(null, this.getBlockPos(), CobblemonSounds.IMPACT_DARK, SoundSource.BLOCKS, 0.5F, 0.9F);
        }
    }


    private ElementalType getActionType() {
        boolean hasSecondary = this.secondaryType != null && getScalingStat(this.secondaryType) != null;
        if (this.swapPriority && hasSecondary) return secondaryType;
        if (getScalingStat(this.primaryType) != null) return this.primaryType;
        if (hasSecondary) return this.secondaryType;
        return null;
    }


    private int getActionTime(ElementalType type) {
        ElementalTypes types = ElementalTypes.INSTANCE;
        if (CobblemonFarmers.GROWTH_EDITION_INSTALLED && type.equals(types.getGRASS())) return 24000;
        if (type.equals(types.getWATER())) return 600;
        if (type.equals(types.getNORMAL())) return 300;
        if (type.equals(types.getFAIRY())) return 800;
        if (type.equals(types.getFLYING())) return 200;
        if (type.equals(types.getDARK())) return 20000;
        return -1;
    }

    private Stats getScalingStat(ElementalType type) {
        if (type == null) return null;
        ElementalTypes types = ElementalTypes.INSTANCE;
        if (CobblemonFarmers.GROWTH_EDITION_INSTALLED && type.equals(types.getGRASS())) return Stats.SPEED;
        if (type.equals(types.getWATER())) return Stats.SPECIAL_ATTACK;
        if (type.equals(types.getNORMAL())) return Stats.SPEED;
        if (type.equals(types.getFAIRY())) return Stats.SPECIAL_ATTACK;
        if (type.equals(types.getFLYING())) return Stats.SPECIAL_DEFENCE;
        if (type.equals(types.getDARK())) return Stats.HP;
        return null;
    }

    public void fetchAoeRadius() {
        ItemStack pokemonItem = getPokemonItem();
        if (pokemonItem == null || pokemonItem.isEmpty()) {
            this.aoeRadius = 0;
        } else {
            Pokemon pokemon = getItemFormPokemon(pokemonItem, this.level);
            if (getActionType() == null) {
                this.aoeRadius = 0;
            } else {
                this.aoeRadius = Mth.clamp(pokemon.getLevel() / 10, 1, 10);
            }
        }
    }

    public int getAoeRadius() {
        return this.aoeRadius;
    }

    private int getActionProgress(ElementalType type) {
        return Mth.floor(progress * getSpeedModifier());
    }

    @Override
    public boolean hasWorker() {
        return !this.pokemonInventory.getStackInSlot(0).isEmpty() && this.primaryType != null;
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
        data.putString("PrimaryType", this.primaryType != null ? this.primaryType.getName() : "");
        data.putString("SecondaryType", this.secondaryType != null ? this.secondaryType.getName() : "");
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
        primaryType = ElementalTypes.INSTANCE.get(data.getString("PrimaryType"));
        secondaryType = ElementalTypes.INSTANCE.get(data.getString("SecondaryType"));
        actionTime = data.getInt("ActionTime");
        progress = data.getInt("Progress");
        swapPriority = data.getBoolean("SwapPriority");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblemon_farmers.gardening_station");
    }

}
