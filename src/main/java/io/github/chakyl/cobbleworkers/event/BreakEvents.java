package io.github.chakyl.cobbleworkers.event;

import io.github.chakyl.cobbleworkers.CobbleWorkers;
import io.github.chakyl.cobbleworkers.blockentity.CraftStationBlockEntity;
import io.github.chakyl.cobbleworkers.blockentity.StationBaseBlockEntity;
import io.github.chakyl.cobbleworkers.registry.CobbleWorkersRegistery;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CobbleWorkers.MODID)
public class BreakEvents {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();

        if (event.getState().is(CobbleWorkersRegistery.BlockRegistry.CRAFT_STATION.get()) || event.getState().is(CobbleWorkersRegistery.BlockRegistry.MYSTERY_MINE.get()) || event.getState().is(CobbleWorkersRegistery.BlockRegistry.GARDENING_STATION.get())) {
            BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
            if (blockEntity instanceof StationBaseBlockEntity craftStationBlockEntity && craftStationBlockEntity.hasWorker()) {
                event.setCanceled(true);
                player.displayClientMessage(Component.translatable("message.cobble_workers.has_pokemon").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED))), false);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            double workerAssigned = event.getOriginal().getAttribute(CobbleWorkersRegistery.AttributeRegistry.WORKERS_ASSIGNED.get()).getValue();
            event.getEntity().getAttribute(CobbleWorkersRegistery.AttributeRegistry.WORKERS_ASSIGNED.get()).setBaseValue(workerAssigned);
            event.getOriginal().invalidateCaps();
        }
    }
}
