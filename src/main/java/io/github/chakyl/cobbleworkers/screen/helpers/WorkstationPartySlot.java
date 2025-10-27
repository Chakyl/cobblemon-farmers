package io.github.chakyl.cobbleworkers.screen.helpers;

import io.github.chakyl.cobbleworkers.screen.MysteryMineMenu;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class WorkstationPartySlot extends Slot {
    public WorkstationPartySlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
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
