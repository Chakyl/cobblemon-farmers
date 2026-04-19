package io.github.chakyl.cobblemonfarmers.utils;

import com.cobblemon.mod.common.CobblemonItems;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.List;

public class RanchingStationUtils {
    public static boolean ranchingRecipeMatches(RecipeWrapper pContainer, String pokemon, String form) {
        if (pContainer.getItem(0).is(CobblemonItems.POKEMON_MODEL)) {
            CompoundTag tag = pContainer.getItem(0).getTag();
            if (tag != null && !tag.isEmpty()) {
                String species = tag.getString("species");
                if (form.isEmpty() && !pokemon.contains(":")) return species.equals("cobblemon:" + pokemon);
                if (form.isEmpty()) return pokemon.equals(species);
                ListTag aspects = (ListTag) tag.get("aspects");
                if (aspects == null) return false;
                return aspects.toString().contains(form);

            }
        }
        return true;
    }

    public static ItemStack applyQuality(ItemStack itemStack, int hearts) {
        int heartQuality = (int) (double) ((hearts % 11) / 2 - 2);
        if (heartQuality > 0) {
            ItemStack qualityItem = itemStack.copy();
            CompoundTag tag = qualityItem.getOrCreateTag();
            CompoundTag qualityFoodTag = new CompoundTag();
            qualityFoodTag.putInt("quality", heartQuality);
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
            qualityFoodTag.putInt("quality", heartQuality);
            qualityFoodTag.put("effects", new ListTag());
            tag.put("quality_food", qualityFoodTag);
            return qualityItem;
        }
        return itemStack;
    }
    public static boolean compareDay(int day, int checkedDay, int amount) {
        return day < checkedDay || day - checkedDay >= amount;
    }

    public static int getDay(Level level) {
        return (int) (Math.floor((double) level.dayTime() / 24000) + 1);
    }
}
