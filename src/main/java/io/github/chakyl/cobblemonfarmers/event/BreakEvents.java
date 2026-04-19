package io.github.chakyl.cobblemonfarmers.event;

import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.blockentity.StationBaseBlockEntity;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static io.github.chakyl.cobblemonfarmers.utils.GeneralUtils.grantWorkerSlot;

@Mod.EventBusSubscriber(modid = CobblemonFarmers.MODID)
public class BreakEvents {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level level = (Level) event.getLevel();

        if (event.getState().is(CobblemonFarmersRegistery.BlockRegistry.CRAFT_STATION.get()) || event.getState().is(CobblemonFarmersRegistery.BlockRegistry.MYSTERY_MINE.get()) || event.getState().is(CobblemonFarmersRegistery.BlockRegistry.RANCHING_STATION.get()) ||event.getState().is(CobblemonFarmersRegistery.BlockRegistry.GARDENING_STATION.get())) {
            BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
            if (blockEntity instanceof StationBaseBlockEntity stationBaseBlockEntity) {
                if (stationBaseBlockEntity.hasWorker()) {
                    event.setCanceled(true);
                    player.displayClientMessage(Component.translatable("message.cobblemon_farmers.has_pokemon").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED))), false);
                } else if (stationBaseBlockEntity.validateOwner(player)) {
                   if (stationBaseBlockEntity.getPublicContract()) {
                       if (!level.isClientSide()) {
                           grantWorkerSlot(level, player);
                       }
                   }
                } else {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            double workerAssigned = event.getOriginal().getAttribute(CobblemonFarmersRegistery.AttributeRegistry.WORKERS_ASSIGNED.get()).getValue();
            double workerCap = event.getOriginal().getAttribute(CobblemonFarmersRegistery.AttributeRegistry.WORKER_CAP.get()).getValue();
            event.getEntity().getAttribute(CobblemonFarmersRegistery.AttributeRegistry.WORKERS_ASSIGNED.get()).setBaseValue(workerAssigned);
            event.getEntity().getAttribute(CobblemonFarmersRegistery.AttributeRegistry.WORKER_CAP.get()).setBaseValue(workerCap);
            event.getOriginal().invalidateCaps();
        }
    }
}
