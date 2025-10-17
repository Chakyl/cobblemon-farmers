package io.github.chakyl.cobbleworkers.event;

import io.github.chakyl.cobbleworkers.CobbleWorkers;
import io.github.chakyl.cobbleworkers.blockentity.renderer.CraftStationBlockEntityRenderer;
import io.github.chakyl.cobbleworkers.blockentity.renderer.MysteryMineBlockEntityRenderer;
import io.github.chakyl.cobbleworkers.registry.CobbleWorkersRegistery;
import io.github.chakyl.cobbleworkers.screen.CraftStationScreen;
import io.github.chakyl.cobbleworkers.screen.MysteryMineScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEvents {


    @Mod.EventBusSubscriber(modid = CobbleWorkers.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                MenuScreens.register(CobbleWorkersRegistery.MenuRegistry.CRAFT_STATION.get(), CraftStationScreen::new);
                MenuScreens.register(CobbleWorkersRegistery.MenuRegistry.MYSTERY_MINE.get(), MysteryMineScreen::new);
            });
        }
        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(CobbleWorkersRegistery.BlockEntityRegistry.CRAFT_STATION.get(), CraftStationBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(CobbleWorkersRegistery.BlockEntityRegistry.MYSTERY_MINE.get(), MysteryMineBlockEntityRenderer::new);
        }

    }
}