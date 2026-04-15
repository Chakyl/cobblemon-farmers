package io.github.chakyl.cobblemonfarmers.jade;

import io.github.chakyl.cobblemonfarmers.blockentity.CraftStationBlockEntity;
import io.github.chakyl.cobblemonfarmers.blockentity.MysteryMineBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;


public enum MysteryMineInfoProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
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
        if (accessor.getServerData().contains("multChance")) {
            int chance = accessor.getServerData().getInt("multChance");
            if (chance > 0)
                tooltip.add(Component.translatable("gui.cobblemon_farmers.mult_chance", accessor.getServerData().getInt("multChance") + "%"));
        }
        tooltip.add(Component.empty());
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        MysteryMineBlockEntity stationBaseBlockEntity = (MysteryMineBlockEntity) accessor.getBlockEntity();
        data.putInt("multChance", stationBaseBlockEntity.getMultChance());
        data.putDouble("speedModifier", stationBaseBlockEntity.getSpeedModifier());
    }

    @Override
    public ResourceLocation getUid() {
        return WorkstationInfoPlugin.UID;
    }

}