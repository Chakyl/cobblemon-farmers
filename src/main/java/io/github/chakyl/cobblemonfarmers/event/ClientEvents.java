package io.github.chakyl.cobblemonfarmers.event;

import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.block.RanchingStationBlock;
import io.github.chakyl.cobblemonfarmers.blockentity.renderer.CraftStationBlockEntityRenderer;
import io.github.chakyl.cobblemonfarmers.blockentity.renderer.GardeningStationBlockEntityRenderer;
import io.github.chakyl.cobblemonfarmers.blockentity.renderer.MysteryMineBlockEntityRenderer;
import io.github.chakyl.cobblemonfarmers.blockentity.renderer.RanchingStationBlockEntityRenderer;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import io.github.chakyl.cobblemonfarmers.screen.CraftStationScreen;
import io.github.chakyl.cobblemonfarmers.screen.GardeningStationScreen;
import io.github.chakyl.cobblemonfarmers.screen.MysteryMineScreen;
import io.github.chakyl.cobblemonfarmers.screen.RanchingStationScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEvents {


    @Mod.EventBusSubscriber(modid = CobblemonFarmers.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                MenuScreens.register(CobblemonFarmersRegistery.MenuRegistry.CRAFT_STATION.get(), CraftStationScreen::new);
                MenuScreens.register(CobblemonFarmersRegistery.MenuRegistry.MYSTERY_MINE.get(), MysteryMineScreen::new);
                MenuScreens.register(CobblemonFarmersRegistery.MenuRegistry.GARDENING_STATION.get(), GardeningStationScreen::new);
                MenuScreens.register(CobblemonFarmersRegistery.MenuRegistry.RANCHING_STATION.get(), RanchingStationScreen::new);
            });
        }
        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(CobblemonFarmersRegistery.BlockEntityRegistry.CRAFT_STATION.get(), CraftStationBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(CobblemonFarmersRegistery.BlockEntityRegistry.MYSTERY_MINE.get(), MysteryMineBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(CobblemonFarmersRegistery.BlockEntityRegistry.GARDENING_STATION.get(), GardeningStationBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(CobblemonFarmersRegistery.BlockEntityRegistry.RANCHING_STATION.get(), RanchingStationBlockEntityRenderer::new);
        }

    }
}