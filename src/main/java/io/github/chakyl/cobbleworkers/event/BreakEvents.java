package io.github.chakyl.cobbleworkers.event;

import io.github.chakyl.cobbleworkers.CobbleWorkers;
import io.github.chakyl.cobbleworkers.blockentity.CraftStationBlockEntity;
import io.github.chakyl.cobbleworkers.blockentity.StationBaseBlockEntity;
import io.github.chakyl.cobbleworkers.registry.CobbleWorkersRegistery;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CobbleWorkers.MODID)
public class BreakEvents {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();

        if (event.getState().is(CobbleWorkersRegistery.BlockRegistry.CRAFT_STATION.get()) || event.getState().is(CobbleWorkersRegistery.BlockRegistry.MYSTERY_MINE.get())) {
            BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
            if (blockEntity instanceof StationBaseBlockEntity craftStationBlockEntity && craftStationBlockEntity.hasWorker()) {
                event.setCanceled(true);
                player.displayClientMessage(Component.translatable("message.cobble_workers.has_pokemon").setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED))), false);
            }
        }
    }
}