package io.github.chakyl.cobblemonfarmers.JEI;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;

public class GardeningStationRecipe {
    final ElementalType type;
    final Stats speedStat;
    final int actionTime;

    public GardeningStationRecipe(ElementalType type, Stats speedStat, int actionTime) {
        this.type = type;
        this.speedStat = speedStat;
        this.actionTime = actionTime;
    }
}
