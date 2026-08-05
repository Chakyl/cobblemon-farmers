package io.github.chakyl.cobblemonfarmers.JEI;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;

public class JEIGardeningStationRecipe {
    final ElementalType type;
    final Stats speedStat;
    final int actionTime;

    public JEIGardeningStationRecipe(ElementalType type, Stats speedStat, int actionTime) {
        this.type = type;
        this.speedStat = speedStat;
        this.actionTime = actionTime;
    }
}
