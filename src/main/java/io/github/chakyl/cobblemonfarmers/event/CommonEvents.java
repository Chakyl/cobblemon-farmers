package io.github.chakyl.cobblemonfarmers.event;

import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.network.PacketHandler;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonEvents {
    @Mod.EventBusSubscriber(modid = CobblemonFarmers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBus {
        @SubscribeEvent

        public static void onCommonSetup(final FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                PacketHandler.register();
            });
            event.enqueueWork(() -> CobblemonFarmers.GROWTH_EDITION_INSTALLED = ModList.get().isLoaded("dew_drop_farmland_growth"));
        }

        @SubscribeEvent
        public static void setAttributes(final EntityAttributeModificationEvent event) {
            event.add(EntityType.PLAYER, CobblemonFarmersRegistery.AttributeRegistry.WORKERS_ASSIGNED.get());
            event.add(EntityType.PLAYER, CobblemonFarmersRegistery.AttributeRegistry.WORKER_CAP.get());
            event.add(EntityType.PLAYER, CobblemonFarmersRegistery.AttributeRegistry.WORKER_PERMITS.get());
            event.add(EntityType.PLAYER, CobblemonFarmersRegistery.AttributeRegistry.PUBLIC_CONTRACTS.get());
        }
    }
}