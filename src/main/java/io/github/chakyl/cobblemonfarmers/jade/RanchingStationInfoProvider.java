package io.github.chakyl.cobblemonfarmers.jade;

import io.github.chakyl.cobblemonfarmers.blockentity.RanchingStationBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;


public enum RanchingStationInfoProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(
            ITooltip tooltip,
            BlockAccessor accessor,
            IPluginConfig config
    ) {
        if (accessor.getServerData().contains("ranchingPower")) {
            double rp = accessor.getServerData().getDouble("ranchingPower");
            if (rp > 0)
                tooltip.add(Component.translatable("tooltip.cobblemon_farmers.ranching_station.ranching_power", (int) rp));
        }
        tooltip.add(Component.empty());
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        RanchingStationBlockEntity stationBaseBlockEntity = (RanchingStationBlockEntity) accessor.getBlockEntity();
        data.putInt("ranchingPower", stationBaseBlockEntity.getRanchingPower());
    }

    @Override
    public ResourceLocation getUid() {
        return WorkstationInfoPlugin.UID;
    }

}