package io.github.chakyl.cobblemonfarmers.utils;

import com.cobblemon.mod.common.api.types.ElementalType;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import net.minecraft.world.item.Item;

public class ElementalTypeUtils {
    // Criticisms welcome
    public static Item getItemFromElementalType(ElementalType type) {
        return switch (type.getName()) {
            case "dark" -> CobblemonFarmersRegistery.ItemRegistry.DARK_TYPE_WORKER.get();
            case "dragon" -> CobblemonFarmersRegistery.ItemRegistry.DRAGON_TYPE_WORKER.get();
            case "electric" -> CobblemonFarmersRegistery.ItemRegistry.ELECTRIC_TYPE_WORKER.get();
            case "fairy" -> CobblemonFarmersRegistery.ItemRegistry.FAIRY_TYPE_WORKER.get();
            case "fighting" -> CobblemonFarmersRegistery.ItemRegistry.FIGHTING_TYPE_WORKER.get();
            case "fire" -> CobblemonFarmersRegistery.ItemRegistry.FIRE_TYPE_WORKER.get();
            case "flying" -> CobblemonFarmersRegistery.ItemRegistry.FLYING_TYPE_WORKER.get();
            case "ghost" -> CobblemonFarmersRegistery.ItemRegistry.GHOST_TYPE_WORKER.get();
            case "grass" -> CobblemonFarmersRegistery.ItemRegistry.GRASS_TYPE_WORKER.get();
            case "ground" -> CobblemonFarmersRegistery.ItemRegistry.GROUND_TYPE_WORKER.get();
            case "ice" -> CobblemonFarmersRegistery.ItemRegistry.ICE_TYPE_WORKER.get();
            case "poison" -> CobblemonFarmersRegistery.ItemRegistry.POISON_TYPE_WORKER.get();
            case "psychic" -> CobblemonFarmersRegistery.ItemRegistry.PSYCHIC_TYPE_WORKER.get();
            case "rock" -> CobblemonFarmersRegistery.ItemRegistry.ROCK_TYPE_WORKER.get();
            case "steel" -> CobblemonFarmersRegistery.ItemRegistry.STEEL_TYPE_WORKER.get();
            case "water" -> CobblemonFarmersRegistery.ItemRegistry.WATER_TYPE_WORKER.get();
            default -> CobblemonFarmersRegistery.ItemRegistry.NORMAL_TYPE_WORKER.get();
        };
    }
}
