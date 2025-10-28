package io.github.chakyl.cobbleworkers.event;

import io.github.chakyl.cobbleworkers.CobbleWorkers;
import io.github.chakyl.cobbleworkers.registry.CobbleWorkersRegistery;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonEvents {
    @Mod.EventBusSubscriber(modid = CobbleWorkers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBus {
        @SubscribeEvent

        public static void onCommonSetup(final FMLCommonSetupEvent event) {
            event.enqueueWork(() -> CobbleWorkers.GROWTH_EDITION_INSTALLED = ModList.get().isLoaded("dew_drop_farmland_growth"));
        }

        @SubscribeEvent
        public static void setAttributes(final EntityAttributeModificationEvent event) {
            event.add(EntityType.PLAYER, CobbleWorkersRegistery.AttributeRegistry.WORKER_CAP.get());
            event.add(EntityType.PLAYER, CobbleWorkersRegistery.AttributeRegistry.WORKERS_ASSIGNED.get());
        }
    }
}