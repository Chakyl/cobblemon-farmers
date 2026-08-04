package io.github.chakyl.cobblemonfarmers.tag;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CobblemonFarmersTags {
    public static final TagKey<Item> COMMON_BERRIES = itemTag("common_berries");
    public static final TagKey<Item> UNCOMMON_BERRIES = itemTag("uncommon_berries");
    public static final TagKey<Item> RARE_BERRIES = itemTag("rare_berries");
    public static final TagKey<Item> LEGENDARY_BERRIES = itemTag("legendary_berries");
    public static final TagKey<Item> MAGIC_SHEARS_RANCHING_STATION = itemTag("magic_shears_ranching_station");
    public static final TagKey<Item> MILKS_RANCHING_STATION = itemTag("milks_ranching_station");
    public static final TagKey<Item> CRAFT_STATION_RENDERS_FLAT = itemTag("craft_station_renders_flat");
    public static final TagKey<Item> POKEMON_WORKSTATION_ITEM = itemTag("pokemon_workstation");
    public static final TagKey<Block> POKEMON_WORKSTATION_BLOCK = blockTag("pokemon_workstation");

    public static TagKey<Item> itemTag(String name) {
        return ItemTags.create(new ResourceLocation(CobblemonFarmers.MODID, name));
    }
    public static TagKey<Block> blockTag(String name) {
        return BlockTags.create(new ResourceLocation(CobblemonFarmers.MODID, name));
    }
}