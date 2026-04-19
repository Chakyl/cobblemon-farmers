package io.github.chakyl.cobblemonfarmers.items;

import com.cobblemon.mod.common.CobblemonSounds;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
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
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> list, TooltipFlag pFlag) {
        list.add(Component.translatable("item.cobblemon_farmers.worker_permit.description").withStyle(ChatFormatting.GRAY));
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