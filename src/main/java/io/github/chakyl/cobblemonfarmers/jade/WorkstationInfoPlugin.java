package io.github.chakyl.cobblemonfarmers.jade;

import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.block.CraftStationBlock;
import io.github.chakyl.cobblemonfarmers.block.GardeningStationBlock;
import io.github.chakyl.cobblemonfarmers.block.MysteryMineBlock;
import io.github.chakyl.cobblemonfarmers.block.RanchingStationBlock;
import io.github.chakyl.cobblemonfarmers.blockentity.CraftStationBlockEntity;
import io.github.chakyl.cobblemonfarmers.blockentity.GardeningStationBlockEntity;
import io.github.chakyl.cobblemonfarmers.blockentity.MysteryMineBlockEntity;
import io.github.chakyl.cobblemonfarmers.blockentity.RanchingStationBlockEntity;
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

        // Individual Providers
        registration.registerBlockDataProvider(CraftStationInfoProvider.INSTANCE, CraftStationBlockEntity.class);
        registration.registerBlockDataProvider(MysteryMineInfoProvider.INSTANCE, MysteryMineBlockEntity.class);
        registration.registerBlockDataProvider(GardeningStationInfoProvider.INSTANCE, GardeningStationBlockEntity.class);
        registration.registerBlockDataProvider(RanchingStationInfoProvider.INSTANCE, RanchingStationBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(WorkstationInfoProvider.INSTANCE, CraftStationBlock.class);
        registration.registerBlockComponent(WorkstationInfoProvider.INSTANCE, GardeningStationBlock.class);
        registration.registerBlockComponent(WorkstationInfoProvider.INSTANCE, MysteryMineBlock.class);
        registration.registerBlockComponent(WorkstationInfoProvider.INSTANCE, RanchingStationBlock.class);

        // Individual Providers
        registration.registerBlockComponent(CraftStationInfoProvider.INSTANCE, CraftStationBlock.class);
        registration.registerBlockComponent(MysteryMineInfoProvider.INSTANCE, MysteryMineBlock.class);
        registration.registerBlockComponent(GardeningStationInfoProvider.INSTANCE, GardeningStationBlock.class);
        registration.registerBlockComponent(RanchingStationInfoProvider.INSTANCE, RanchingStationBlock.class);
    }
}