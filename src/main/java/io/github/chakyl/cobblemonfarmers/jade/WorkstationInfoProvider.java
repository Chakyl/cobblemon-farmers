package io.github.chakyl.cobblemonfarmers.jade;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.chakyl.cobblemonfarmers.blockentity.StationBaseBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.UsernameCache;
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

        if (accessor.getServerData().getBoolean("public")) {
            tooltip.add(Component.translatable("jade.cobblemon_farmers.workstation.public").withStyle(ChatFormatting.GREEN));
        }
        if (accessor.getPlayer().isCrouching() && accessor.getServerData().hasUUID("owner")) {
            tooltip.add(Component.translatable("jade.cobblemon_farmers.owner", UsernameCache.getLastKnownUsername(accessor.getServerData().getUUID("owner"))));
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
    }

    @Override
    public ResourceLocation getUid() {
        return WorkstationInfoPlugin.UID;
    }

}