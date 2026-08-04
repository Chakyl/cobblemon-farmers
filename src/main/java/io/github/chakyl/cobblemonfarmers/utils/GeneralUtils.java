package io.github.chakyl.cobblemonfarmers.utils;

import com.cobblemon.mod.common.CobblemonSounds;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class GeneralUtils {

    public static void grantWorkerSlot(Level level, Player player) {
        AttributeInstance workerPermitsInstance = player.getAttribute(CobblemonFarmersRegistery.AttributeRegistry.WORKER_PERMITS.get());

        double currentPermits = workerPermitsInstance.getBaseValue();
        workerPermitsInstance.setBaseValue(currentPermits + 1.0);
        player.sendSystemMessage(Component.translatable("item.cobblemon_farmers.worker_permit.used", (int) getWorkerCap(player)).withStyle(ChatFormatting.GREEN));
        level.playSound(null, player.getOnPos(), CobblemonSounds.FOSSIL_MACHINE_FINISHED, SoundSource.BLOCKS, 1.0F, 0.9F);
    }

    public static void removePublicContract(Level level, Player player) {
        AttributeInstance publicContractInstance = player.getAttribute(CobblemonFarmersRegistery.AttributeRegistry.PUBLIC_CONTRACTS.get());
        double currentBase = publicContractInstance.getBaseValue();
        publicContractInstance.setBaseValue(currentBase - 1.0);
        player.sendSystemMessage(Component.translatable("item.cobblemon_farmers.worker_permit.used", (int) getWorkerCap(player)).withStyle(ChatFormatting.GREEN));
        level.playSound(null, player.getOnPos(), CobblemonSounds.FOSSIL_MACHINE_FINISHED, SoundSource.BLOCKS, 1.0F, 0.9F);
    }

    public static int getWorkerCap(Player player) {
        return Mth.floor(player.getAttribute(CobblemonFarmersRegistery.AttributeRegistry.WORKER_CAP.get()).getValue() + player.getAttribute(CobblemonFarmersRegistery.AttributeRegistry.WORKER_PERMITS.get()).getValue() - player.getAttribute(CobblemonFarmersRegistery.AttributeRegistry.PUBLIC_CONTRACTS.get()).getValue());
    }

    public static Iterable<BlockPos> getBetween(BlockPos centerPos, int radius, int maxYRadius) {
        return BlockPos.betweenClosed(new BlockPos(centerPos.getX() - radius, centerPos.getY() - Math.min(maxYRadius, radius), centerPos.getZ() - radius), new BlockPos(centerPos.getX() + radius, centerPos.getY() + Math.min(maxYRadius, radius), centerPos.getZ() + radius));
    }

    public static Iterable<BlockPos> getBetweenManhattan(BlockPos centerPos, int radius, int maxYRadius) {
        int yRadius = Math.min(maxYRadius, radius);
        return BlockPos.withinManhattan(centerPos, radius, yRadius, radius);
    }

    public static boolean isSamePos(BlockPos pos1, BlockPos pos2) {
        return pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY() && pos1.getZ() == pos2.getZ();
    }
}
