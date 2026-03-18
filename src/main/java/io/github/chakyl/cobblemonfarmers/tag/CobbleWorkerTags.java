package io.github.chakyl.cobblemonfarmers.tag;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class CobbleWorkerTags {
    public static final TagKey<Item> COMMON_BERRIES = tag("common_berries");
    public static final TagKey<Item> UNCOMMON_BERRIES = tag("uncommon_berries");
    public static final TagKey<Item> RARE_BERRIES = tag("rare_berries");
    public static final TagKey<Item> LEGENDARY_BERRIES = tag("legendary_berries");
    public static TagKey<Item> tag(String name) {
        return ItemTags.create(new ResourceLocation(CobblemonFarmers.MODID, name));
    }
}