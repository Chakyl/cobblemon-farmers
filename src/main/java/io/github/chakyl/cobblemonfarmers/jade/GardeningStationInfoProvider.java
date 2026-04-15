package io.github.chakyl.cobblemonfarmers.jade;

import io.github.chakyl.cobblemonfarmers.blockentity.CraftStationBlockEntity;
import io.github.chakyl.cobblemonfarmers.blockentity.GardeningStationBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;


public enum GardeningStationInfoProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(
            ITooltip tooltip,
            BlockAccessor accessor,
            IPluginConfig config
    ) {
        if (accessor.getServerData().contains("speedModifier")) {
            double speed = accessor.getServerData().getDouble("speedModifier");
            if (speed > 0) tooltip.add(Component.translatable("gui.cobblemon_farmers.speed", speed));
        }
        if (accessor.getServerData().contains("aoeRadius")) {
            int chance = accessor.getServerData().getInt("aoeRadius");
            if (chance > 0)
                tooltip.add(Component.translatable("gui.cobblemon_farmers.working_radius", accessor.getServerData().getInt("aoeRadius")));
        }
        tooltip.add(Component.empty());
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        GardeningStationBlockEntity stationBaseBlockEntity = (GardeningStationBlockEntity) accessor.getBlockEntity();
        data.putDouble("speedModifier", stationBaseBlockEntity.getSpeedModifier());
        data.putInt("aoeRadius", stationBaseBlockEntity.getAoeRadius());
    }

    @Override
    public ResourceLocation getUid() {
        return WorkstationInfoPlugin.UID;
    }

}