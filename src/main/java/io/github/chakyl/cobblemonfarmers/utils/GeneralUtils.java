package io.github.chakyl.cobblemonfarmers.utils;

import com.cobblemon.mod.common.CobblemonSounds;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class GeneralUtils {

    public static void grantWorkerSlot(Level level, Player player) {
        AttributeInstance instance = player.getAttribute(CobblemonFarmersRegistery.AttributeRegistry.WORKER_CAP.get());

        double currentBase = instance.getBaseValue();
        instance.setBaseValue(currentBase + 1.0);

        player.sendSystemMessage(Component.translatable("item.cobblemon_farmers.worker_permit.used", (int) (instance.getValue())).withStyle(ChatFormatting.GREEN));
        level.playSound(null, player.getOnPos(), CobblemonSounds.FOSSIL_MACHINE_FINISHED, SoundSource.BLOCKS, 1.0F, 0.9F);
    }
}
