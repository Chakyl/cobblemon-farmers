package io.github.chakyl.cobblemonfarmers.jade;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.authlib.GameProfile;
import io.github.chakyl.cobblemonfarmers.blockentity.StationBaseBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.ServerLifecycleHooks;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.Objects;

import static io.github.chakyl.cobblemonfarmers.utils.PokeUtils.getSpeciesFromCompoundTag;


public enum WorkstationInfoProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(
            ITooltip tooltip,
            BlockAccessor accessor,
            IPluginConfig config
    ) {
        if (accessor.getServerData().contains("pokemon")) {
            CompoundTag tag = (CompoundTag) accessor.getServerData().get("pokemon");
            CompoundTag pokeData = ((CompoundTag) tag.get("pokeData"));
            Species species = PokemonSpecies.INSTANCE.getByName(getSpeciesFromCompoundTag(tag));
            if (species == null) {
                tooltip.add(Component.translatable("jade.cobblemon_farmers.workstation.no_worker"));
                return;
            }
            tooltip.add(Component.translatable("jade.cobblemon_farmers.workstation.pokemon.name", species.getName(), pokeData.getShort("Level")).withStyle(ChatFormatting.BLUE));
        } else {
            tooltip.add(Component.translatable("jade.cobblemon_farmers.workstation.no_worker"));
        }
        if (accessor.getServerData().contains("speedModifier")) {
            double speed = accessor.getServerData().getDouble("speedModifier");
            double bonusSpeed = 0;
            if (accessor.getServerData().contains("bonusSpeed")) {
                bonusSpeed = accessor.getServerData().getDouble("bonusSpeed");
            }
            if (speed > 0)
                tooltip.add(Component.translatable("gui.cobblemon_farmers.speed", speed + (bonusSpeed > 0 ? "( " + speed + " + " + bonusSpeed + ")" : "")));
        }
        if (accessor.getServerData().contains("multChance")) {
            int chance = accessor.getServerData().getInt("multChance");
            int bonusMult = 0;
            if (accessor.getServerData().contains("bonusMult")) {
                bonusMult = accessor.getServerData().getInt("bonusMult");
            }
            if (chance > 0)
                tooltip.add(Component.translatable("gui.cobblemon_farmers.mult_chance", (chance + bonusMult) + "%" + (bonusMult > 0 ? " (" + chance + "% + " + bonusMult + "%)" : "")).withStyle(bonusMult > 0 ? ChatFormatting.GREEN : ChatFormatting.GRAY));
        }
        if (accessor.getServerData().contains("aoeRadius")) {
            int radius = accessor.getServerData().getInt("aoeRadius");
            if (radius > 0) {
                radius *= 2;
                radius += 1;
                tooltip.add(Component.translatable("gui.cobblemon_farmers.working_radius", radius, Math.min(5, radius), radius));
            }
        }
        tooltip.add(Component.empty());
        if (accessor.getServerData().getBoolean("public")) {
            tooltip.add(Component.translatable("jade.cobblemon_farmers.workstation.public").withStyle(ChatFormatting.GREEN));
        }
        if (accessor.getPlayer().isCrouching() && accessor.getServerData().hasUUID("owner")) {
            PlayerInfo info = Minecraft.getInstance().getConnection().getPlayerInfo(accessor.getServerData().getUUID("owner"));
            if (info != null) {
                String name = info.getProfile().getName();
                tooltip.add(Component.translatable("jade.cobblemon_farmers.owner", name));
            } else {
                tooltip.add(Component.translatable("jade.cobblemon_farmers.owner", "Unknown"));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        StationBaseBlockEntity stationBaseBlockEntity = (StationBaseBlockEntity) accessor.getBlockEntity();
        ItemStack pokemonItem = stationBaseBlockEntity.getPokemonItem();
        if (pokemonItem != null && pokemonItem.getTag() != null && !pokemonItem.getTag().isEmpty())
            data.put("pokemon", pokemonItem.getTag());
        data.putBoolean("public", stationBaseBlockEntity.getPublicContract());
        if (stationBaseBlockEntity.getOwner() != null)
            data.putUUID("owner", Objects.requireNonNull(stationBaseBlockEntity.getOwner()));

        data.putDouble("speedModifier", stationBaseBlockEntity.getSpeedModifier());
        data.putDouble("bonusSpeed", stationBaseBlockEntity.getBonusSpeed());
        data.putDouble("aoeRadius", stationBaseBlockEntity.getAoeRadius());
        data.putInt("multChance", stationBaseBlockEntity.getMultChance());
        data.putInt("bonusMult", stationBaseBlockEntity.getBonusMult());
    }

    @Override
    public ResourceLocation getUid() {
        return WorkstationInfoPlugin.UID;
    }

}