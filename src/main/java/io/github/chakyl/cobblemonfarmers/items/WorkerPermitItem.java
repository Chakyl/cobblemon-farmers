package io.github.chakyl.cobblemonfarmers.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

import static io.github.chakyl.cobblemonfarmers.utils.GeneralUtils.grantWorkerSlot;

public class WorkerPermitItem extends Item {
    public WorkerPermitItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            grantWorkerSlot(level,player);
            if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
            player.getCooldowns().addCooldown(this, 2);
        }
        return super.use(level, player, hand);
    }
}