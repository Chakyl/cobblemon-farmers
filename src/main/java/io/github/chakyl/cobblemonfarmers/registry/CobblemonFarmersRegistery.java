package io.github.chakyl.cobblemonfarmers.registry;

import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.google.common.base.Suppliers;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.block.CraftStationBlock;
import io.github.chakyl.cobblemonfarmers.block.GardeningStationBlock;
import io.github.chakyl.cobblemonfarmers.block.MysteryMineBlock;
import io.github.chakyl.cobblemonfarmers.block.RanchingStationBlock;
import io.github.chakyl.cobblemonfarmers.blockentity.CraftStationBlockEntity;
import io.github.chakyl.cobblemonfarmers.blockentity.GardeningStationBlockEntity;
import io.github.chakyl.cobblemonfarmers.blockentity.MysteryMineBlockEntity;
import io.github.chakyl.cobblemonfarmers.blockentity.RanchingStationBlockEntity;
import io.github.chakyl.cobblemonfarmers.items.PublicContractItem;
import io.github.chakyl.cobblemonfarmers.items.WorkerPermitItem;
import io.github.chakyl.cobblemonfarmers.items.WorkerTypeItem;
import io.github.chakyl.cobblemonfarmers.recipe.CraftStationRecipe;
import io.github.chakyl.cobblemonfarmers.recipe.MysteryMineRecipe;
import io.github.chakyl.cobblemonfarmers.recipe.RanchingStationForageRecipe;
import io.github.chakyl.cobblemonfarmers.recipe.RanchingStationMilkingRecipe;
import io.github.chakyl.cobblemonfarmers.screen.CraftStationMenu;
import io.github.chakyl.cobblemonfarmers.screen.GardeningStationMenu;
import io.github.chakyl.cobblemonfarmers.screen.MysteryMineMenu;
import io.github.chakyl.cobblemonfarmers.screen.RanchingStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class CobblemonFarmersRegistery {

    private static final String MODID = CobblemonFarmers.MODID;

    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);
    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MODID);

    public static void register() {
        BlockRegistry.register();
        BlockEntityRegistry.register();
        ItemRegistry.register();
        MenuRegistry.register();
        RecipeRegistry.register();
        AttributeRegistry.register();
        CreativeTabReg.register();
    }

    public static final class BlockRegistry {

        private static void register() {
            BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<Block> CRAFT_STATION = registerWithItem("craft_station", () ->
                new CraftStationBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).noOcclusion().strength(1.5F, 6.0F)));
        public static final RegistryObject<Block> GARDENING_STATION = registerWithItem("gardening_station", () ->
                new GardeningStationBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).noOcclusion().strength(1.5F, 6.0F)));
        public static final RegistryObject<Block> MYSTERY_MINE = registerWithItem("mystery_mine", () ->
                new MysteryMineBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).noOcclusion().strength(1.5F, 6.0F)));
        public static final RegistryObject<Block> RANCHING_STATION = registerWithItem("ranching_station", () ->
                new RanchingStationBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).noOcclusion().strength(1.5F, 6.0F)));

        private static RegistryObject<Block> registerWithItem(final String name, final Supplier<Block> supplier) {
            return registerWithItem(name, supplier, ItemRegistry::registerBlockItem);
        }

        private static RegistryObject<Block> registerWithItem(final String name, final Supplier<Block> blockSupplier, final Function<RegistryObject<Block>, RegistryObject<Item>> itemSupplier) {
            final RegistryObject<Block> block = BLOCKS.register(name, blockSupplier);
            final RegistryObject<Item> item = itemSupplier.apply(block);
            return block;
        }

        private static boolean never(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
            return false;
        }
    }

    public static final class BlockEntityRegistry {
        private static void register() {
            BLOCK_ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<BlockEntityType<CraftStationBlockEntity>> CRAFT_STATION = BLOCK_ENTITY_TYPES.register("craft_station",
                () -> BlockEntityType.Builder.of(CraftStationBlockEntity::new, BlockRegistry.CRAFT_STATION.get()).build(null));
        public static final RegistryObject<BlockEntityType<GardeningStationBlockEntity>> GARDENING_STATION = BLOCK_ENTITY_TYPES.register("gardening_station",
                () -> BlockEntityType.Builder.of(GardeningStationBlockEntity::new, BlockRegistry.GARDENING_STATION.get()).build(null));
        public static final RegistryObject<BlockEntityType<MysteryMineBlockEntity>> MYSTERY_MINE = BLOCK_ENTITY_TYPES.register("mystery_mine",
                () -> BlockEntityType.Builder.of(MysteryMineBlockEntity::new, BlockRegistry.MYSTERY_MINE.get()).build(null));
        public static final RegistryObject<BlockEntityType<RanchingStationBlockEntity>> RANCHING_STATION = BLOCK_ENTITY_TYPES.register("ranching_station",
                () -> BlockEntityType.Builder.of(RanchingStationBlockEntity::new, BlockRegistry.RANCHING_STATION.get()).build(null));
    }


    public static final class ItemRegistry {

        private static final List<RegistryObject<Item>> ALL_ITEMS = new ArrayList<>();

        private static void register() {
            ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<Item> RETRIEVE_WORKER = register("retrieve_worker", () -> new Item(new Item.Properties().stacksTo(1)));

        public static final RegistryObject<Item> NORMAL_TYPE_WORKER = register("normal_type_worker", () -> new WorkerTypeItem(new Item.Properties().stacksTo(64), ElementalTypes.INSTANCE.getNORMAL()));
        public static final RegistryObject<Item> FIRE_TYPE_WORKER = register("fire_type_worker", () -> new WorkerTypeItem(new Item.Properties().stacksTo(64), ElementalTypes.INSTANCE.getFIRE()));
        public static final RegistryObject<Item> WATER_TYPE_WORKER = register("water_type_worker", () -> new WorkerTypeItem(new Item.Properties().stacksTo(64), ElementalTypes.INSTANCE.getWATER()));
        public static final RegistryObject<Item> GRASS_TYPE_WORKER = register("grass_type_worker", () -> new WorkerTypeItem(new Item.Properties().stacksTo(64), ElementalTypes.INSTANCE.getGRASS()));
        public static final RegistryObject<Item> FLYING_TYPE_WORKER = register("flying_type_worker", () -> new WorkerTypeItem(new Item.Properties().stacksTo(64), ElementalTypes.INSTANCE.getFLYING()));
        public static final RegistryObject<Item> FIGHTING_TYPE_WORKER = register("fighting_type_worker", () -> new WorkerTypeItem(new Item.Properties().stacksTo(64), ElementalTypes.INSTANCE.getFIGHTING()));
        public static final RegistryObject<Item> POISON_TYPE_WORKER = register("poison_type_worker", () -> new WorkerTypeItem(new Item.Properties().stacksTo(64), ElementalTypes.INSTANCE.getPOISON()));
        public static final RegistryObject<Item> ELECTRIC_TYPE_WORKER = register("electric_type_worker", () -> new WorkerTypeItem(new Item.Properties().stacksTo(64), ElementalTypes.INSTANCE.getELECTRIC()));
        public static final RegistryObject<Item> GROUND_TYPE_WORKER = register("ground_type_worker", () -> new WorkerTypeItem(new Item.Properties().stacksTo(64), ElementalTypes.INSTANCE.getGROUND()));
        public static final RegistryObject<Item> ROCK_TYPE_WORKER = register("rock_type_worker", () -> new WorkerTypeItem(new Item.Properties().stacksTo(64), ElementalTypes.INSTANCE.getROCK()));
        public static final RegistryObject<Item> PSYCHIC_TYPE_WORKER = register("psychic_type_worker", () -> new WorkerTypeItem(new Item.Properties().stacksTo(64), ElementalTypes.INSTANCE.getPSYCHIC()));
        public static final RegistryObject<Item> ICE_TYPE_WORKER = register("ice_type_worker", () -> new WorkerTypeItem(new Item.Properties().stacksTo(64), ElementalTypes.INSTANCE.getICE()));
        public static final RegistryObject<Item> BUG_TYPE_WORKER = register("bug_type_worker", () -> new WorkerTypeItem(new Item.Properties().stacksTo(64), ElementalTypes.INSTANCE.getBUG()));
        public static final RegistryObject<Item> GHOST_TYPE_WORKER = register("ghost_type_worker", () -> new WorkerTypeItem(new Item.Properties().stacksTo(64), ElementalTypes.INSTANCE.getGHOST()));
        public static final RegistryObject<Item> STEEL_TYPE_WORKER = register("steel_type_worker", () -> new WorkerTypeItem(new Item.Properties().stacksTo(64), ElementalTypes.INSTANCE.getSTEEL()));
        public static final RegistryObject<Item> DRAGON_TYPE_WORKER = register("dragon_type_worker", () -> new WorkerTypeItem(new Item.Properties().stacksTo(64), ElementalTypes.INSTANCE.getDRAGON()));
        public static final RegistryObject<Item> DARK_TYPE_WORKER = register("dark_type_worker", () -> new WorkerTypeItem(new Item.Properties().stacksTo(64), ElementalTypes.INSTANCE.getDARK()));
        public static final RegistryObject<Item> FAIRY_TYPE_WORKER = register("fairy_type_worker", () -> new WorkerTypeItem(new Item.Properties().stacksTo(64), ElementalTypes.INSTANCE.getFAIRY()));

        public static final RegistryObject<Item> PUBLIC_CONTRACT = register("public_contract", () -> new PublicContractItem(new Item.Properties().stacksTo(64)));
        public static final RegistryObject<Item> WORKER_PERMIT = register("worker_permit", () -> new WorkerPermitItem(new Item.Properties().stacksTo(64)));

        /**
         * Creates a registry object for a block item and adds it to the mod creative tab
         *
         * @param block the block
         * @return the registry object
         */
        private static RegistryObject<Item> registerBlockItem(final RegistryObject<Block> block) {
            return register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
        }

        /**
         * Creates a registry object for the given item and adds it to the mod creative tab
         *
         * @param name     the registry name
         * @param supplier the item supplier
         * @return the item registry object
         */
        private static RegistryObject<Item> register(final String name, final Supplier<Item> supplier) {
            final RegistryObject<Item> item = ITEMS.register(name, supplier);
            if (!name.equals("retrieve_worker"))
                ALL_ITEMS.add(item);
            return item;
        }
    }

    public static final class MenuRegistry {

        private static void register() {
            MENU_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<MenuType<CraftStationMenu>> CRAFT_STATION = MENU_TYPES.register("craft_station", () ->
                IForgeMenuType.create(CraftStationMenu::new)
        );
        public static final RegistryObject<MenuType<GardeningStationMenu>> GARDENING_STATION = MENU_TYPES.register("gardening_station", () ->
                IForgeMenuType.create(GardeningStationMenu::new)
        );
        public static final RegistryObject<MenuType<MysteryMineMenu>> MYSTERY_MINE = MENU_TYPES.register("mystery_mine", () ->
                IForgeMenuType.create(MysteryMineMenu::new)
        );
        public static final RegistryObject<MenuType<RanchingStationMenu>> RANCHING_STATION = MENU_TYPES.register("ranching_station", () ->
                IForgeMenuType.create(RanchingStationMenu::new)
        );
    }

    public static final class RecipeRegistry {

        private static void register() {
            RECIPE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
            RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<RecipeSerializer<CraftStationRecipe>> CRAFT_STATION_SERIALIZER = RECIPE_SERIALIZERS.register("craft_station", CraftStationRecipe.Serializer::new);
        public static final RegistryObject<RecipeSerializer<MysteryMineRecipe>> MYSTERY_MINE_SERIALIZER = RECIPE_SERIALIZERS.register("mystery_mine", MysteryMineRecipe.Serializer::new);
        public static final RegistryObject<RecipeSerializer<RanchingStationForageRecipe>> RANCHING_STATION_FORAGE_SERIALIZER = RECIPE_SERIALIZERS.register("ranching_station/forage", RanchingStationForageRecipe.Serializer::new);
        public static final RegistryObject<RecipeSerializer<RanchingStationMilkingRecipe>> RANCHING_STATION_MILK_SERIALIZER = RECIPE_SERIALIZERS.register("ranching_station/milk", RanchingStationMilkingRecipe.Serializer::new);

        public static final RegistryObject<RecipeType<CraftStationRecipe>> CRAFT_STATION = RECIPE_TYPES.register("craft_station", () -> new RecipeType<>() {
            @Override
            public String toString() {
                return "craft_station";
            }
        });
        public static final RegistryObject<RecipeType<MysteryMineRecipe>> MYSTERY_MINE = RECIPE_TYPES.register("mystery_mine", () -> new RecipeType<>() {
            @Override
            public String toString() {
                return "mystery_mine";
            }
        });
        public static final RegistryObject<RecipeType<RanchingStationForageRecipe>> RANCHING_STATION_FORAGE = RECIPE_TYPES.register("ranching_station/forage", () -> new RecipeType<>() {
            @Override
            public String toString() {
                return "ranching_station/forage";
            }
        });
        public static final RegistryObject<RecipeType<RanchingStationMilkingRecipe>> RANCHING_STATION_MILK = RECIPE_TYPES.register("ranching_station/milk", () -> new RecipeType<>() {
            @Override
            public String toString() {
                return "ranching_station/milk";
            }
        });
    }

    public static final class AttributeRegistry {

        private static void register() {
            ATTRIBUTES.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<Attribute> WORKERS_ASSIGNED = ATTRIBUTES.register("workers_assigned", () -> new RangedAttribute("attribute.cobblemon_farmers.workers_assigned", 0, 0, 1024).setSyncable(true));
        public static final RegistryObject<Attribute> WORKER_CAP = ATTRIBUTES.register("worker_cap", () -> new RangedAttribute("attribute.cobblemon_farmers.worker_cap", 5, 0, 1024).setSyncable(true));
        public static final RegistryObject<Attribute> WORKER_PERMITS = ATTRIBUTES.register("worker_permits", () -> new RangedAttribute("attribute.cobblemon_farmers.worker_permits", 0, 0, 1024 - 5).setSyncable(true));
        public static final RegistryObject<Attribute> PUBLIC_CONTRACTS = ATTRIBUTES.register("public_contracts", () -> new RangedAttribute("attribute.cobblemon_farmers.public_contracts", 0, 0, 1024).setSyncable(true));
    }

    public static final class CreativeTabReg {

        private static void register() {
            CREATIVE_MODE_TABS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("tab", () ->
                CreativeModeTab.builder()
                        .icon(Suppliers.memoize(() -> new ItemStack(BlockRegistry.CRAFT_STATION.get())))
                        .title(Component.translatable("itemGroup." + CobblemonFarmers.MODID))
                        .withSearchBar()
                        .displayItems((parameters, output) ->
                                output.acceptAll(ItemRegistry.ALL_ITEMS
                                        .stream()
                                        .map(o -> new ItemStack(o.get()))
                                        .toList())
                        )
                        .build()
        );
    }

}