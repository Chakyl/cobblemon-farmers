package io.github.chakyl.cobblemonfarmers.items;

import com.cobblemon.mod.common.CobblemonSounds;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.utils.GeneralUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class PublicContractItem extends Item {
    public PublicContractItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> list, TooltipFlag pFlag) {
        list.add(Component.translatable("item.cobblemon_farmers.public_contract.description").withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("item.cobblemon_farmers.public_contract.warn").withStyle(ChatFormatting.RED));
    }

    public boolean useContract(Level level, Player player, InteractionHand hand, boolean hasWorker) {
        if (!level.isClientSide()) {
            AttributeInstance publicContract = player.getAttribute(CobblemonFarmersRegistery.AttributeRegistry.PUBLIC_CONTRACTS.get());
            AttributeInstance workerAssignedInstance = player.getAttribute(CobblemonFarmersRegistery.AttributeRegistry.WORKERS_ASSIGNED.get());

            double currentWorkerCap = GeneralUtils.getWorkerCap(player);
            if (currentWorkerCap <= 0 || currentWorkerCap - workerAssignedInstance.getValue() <= 0) {
                player.sendSystemMessage(Component.translatable("item.cobblemon_farmers.public_contract.not_enough").withStyle(ChatFormatting.RED));
                return false;
            }
            publicContract.setBaseValue(publicContract.getBaseValue() + 1.0);
            if (hasWorker) {
                workerAssignedInstance.setBaseValue(workerAssignedInstance.getValue() + -1);
            }
            if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
            level.playSound(null, player.getOnPos(), CobblemonSounds.FOSSIL_MACHINE_FINISHED, SoundSource.BLOCKS, 1.0F, 0.9F);
            player.sendSystemMessage(Component.translatable("item.cobblemon_farmers.public_contract.used").withStyle(ChatFormatting.GREEN));
            return true;
        }
        return false;
    }
}