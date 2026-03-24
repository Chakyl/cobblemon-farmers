package io.github.chakyl.cobblemonfarmers.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RanchingForage {
    private final ItemStack item;
    private final double chance;
    private final boolean hasQuality;
    private final int minHearts;

    public RanchingForage(ItemStack item, double chance, boolean hasQuality, int minHearts) {
        this.item = item;
        this.chance = chance;
        this.hasQuality = hasQuality;
        this.minHearts = minHearts;
    }

    public static RanchingForage getDefaultInstance() {
        return new RanchingForage(Items.AIR.getDefaultInstance(), 0, false, 0);
    }

    public ItemStack getItem() { return item; }

    public double getChance() { return chance; }

    public boolean hasQuality() { return hasQuality; }

    public int getMinHearts() { return minHearts; }
}