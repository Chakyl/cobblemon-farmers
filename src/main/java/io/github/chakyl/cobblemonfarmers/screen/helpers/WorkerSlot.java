package io.github.chakyl.cobblemonfarmers.screen.helpers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Optional;

public class WorkerSlot extends SlotItemHandler {
    public WorkerSlot(IItemHandler handler, int slot, int x, int y) {
        super(handler, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public boolean allowModification(Player pPlayer) {
        return false;
    }

    @Override
    public Optional<ItemStack> tryRemove(int pCount, int pDecrement, Player pPlayer) {
        return Optional.empty();
    }
}