package io.github.chakyl.cobblemonfarmers.jade;

import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.block.*;
import io.github.chakyl.cobblemonfarmers.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class WorkstationInfoPlugin implements IWailaPlugin {
    public static final ResourceLocation UID = new ResourceLocation(CobblemonFarmers.MODID, "cobblemon_farmers");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(WorkstationInfoProvider.INSTANCE, CraftStationBlockEntity.class);
        registration.registerBlockDataProvider(WorkstationInfoProvider.INSTANCE, GardeningStationBlockEntity.class);
        registration.registerBlockDataProvider(WorkstationInfoProvider.INSTANCE, MysteryMineBlockEntity.class);
        registration.registerBlockDataProvider(WorkstationInfoProvider.INSTANCE, RanchingStationBlockEntity.class);
        registration.registerBlockDataProvider(WorkstationInfoProvider.INSTANCE, CrystalBallBlockEntity.class);
        registration.registerBlockDataProvider(WorkstationInfoProvider.INSTANCE, EnergyPylonBlockEntity.class);

        // Individual Providers
        registration.registerBlockDataProvider(RanchingStationInfoProvider.INSTANCE, RanchingStationBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(WorkstationInfoProvider.INSTANCE, CraftStationBlock.class);
        registration.registerBlockComponent(WorkstationInfoProvider.INSTANCE, GardeningStationBlock.class);
        registration.registerBlockComponent(WorkstationInfoProvider.INSTANCE, MysteryMineBlock.class);
        registration.registerBlockComponent(WorkstationInfoProvider.INSTANCE, RanchingStationBlock.class);
        registration.registerBlockComponent(WorkstationInfoProvider.INSTANCE, CrystalBallBlock.class);
        registration.registerBlockComponent(WorkstationInfoProvider.INSTANCE, EnergyPylonBlock.class);

        // Individual Providers
        registration.registerBlockComponent(RanchingStationInfoProvider.INSTANCE, RanchingStationBlock.class);
    }
}