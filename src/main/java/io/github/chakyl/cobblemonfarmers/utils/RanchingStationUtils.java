package io.github.chakyl.cobblemonfarmers.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RanchingStationUtils {

    public static ItemStack applyQuality(ItemStack itemStack, int hearts) {
        int heartQuality = (int) (double) ((hearts % 11) / 2 - 2);
        if (heartQuality > 0) {
            ItemStack qualityItem = itemStack.copy();
            CompoundTag tag = qualityItem.getOrCreateTag();
            CompoundTag qualityFoodTag = new CompoundTag();
            qualityFoodTag.putString("quality", String.valueOf(heartQuality));
            qualityFoodTag.put("effects", new ListTag());
            tag.put("quality_food", qualityFoodTag);
            return qualityItem;
        }
        return itemStack;
    }

    public static ItemStack applyMilkQuality(ItemStack itemStack, int hearts) {
        int heartQuality = 0;
        if (hearts >= 10 || (hearts > 0 && (hearts % 5) == 0)) {
            heartQuality = 3;
        } else {
            heartQuality = (int) (double) ((hearts % 11) / 2 - 2);
        }
        if (heartQuality > 0) {
            ItemStack qualityItem = itemStack.copy();
            CompoundTag tag = qualityItem.getOrCreateTag();
            CompoundTag qualityFoodTag = new CompoundTag();
            qualityFoodTag.putString("quality", String.valueOf(heartQuality));
            qualityFoodTag.put("effects", new ListTag());
            tag.put("quality_food", qualityFoodTag);
            return qualityItem;
        }
        return itemStack;
    }

    public static boolean compareDay(int day, int checkedDay, int amount) {
        return day > checkedDay || checkedDay - day > amount;
    }

    public static int getDay(Level level) {
        return (int) (Math.floor((double) level.dayTime() / 24000) + 1);
    }
}
