package io.github.chakyl.cobblemonfarmers.datagen;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.registry.CobblemonFarmersRegistery;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;


public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CobblemonFarmers.MODID, exFileHelper);
    }

    @Override
    protected void registerModels() {
        itemModel(CobblemonFarmersRegistery.ItemRegistry.NORMAL_TYPE_WORKER);
        itemModel(CobblemonFarmersRegistery.ItemRegistry.FIRE_TYPE_WORKER);
        itemModel(CobblemonFarmersRegistery.ItemRegistry.WATER_TYPE_WORKER);
        itemModel(CobblemonFarmersRegistery.ItemRegistry.GRASS_TYPE_WORKER);
        itemModel(CobblemonFarmersRegistery.ItemRegistry.FLYING_TYPE_WORKER);
        itemModel(CobblemonFarmersRegistery.ItemRegistry.FIGHTING_TYPE_WORKER);
        itemModel(CobblemonFarmersRegistery.ItemRegistry.POISON_TYPE_WORKER);
        itemModel(CobblemonFarmersRegistery.ItemRegistry.ELECTRIC_TYPE_WORKER);
        itemModel(CobblemonFarmersRegistery.ItemRegistry.GROUND_TYPE_WORKER);
        itemModel(CobblemonFarmersRegistery.ItemRegistry.ROCK_TYPE_WORKER);
        itemModel(CobblemonFarmersRegistery.ItemRegistry.PSYCHIC_TYPE_WORKER);
        itemModel(CobblemonFarmersRegistery.ItemRegistry.ICE_TYPE_WORKER);
        itemModel(CobblemonFarmersRegistery.ItemRegistry.BUG_TYPE_WORKER);
        itemModel(CobblemonFarmersRegistery.ItemRegistry.GHOST_TYPE_WORKER);
        itemModel(CobblemonFarmersRegistery.ItemRegistry.STEEL_TYPE_WORKER);
        itemModel(CobblemonFarmersRegistery.ItemRegistry.DRAGON_TYPE_WORKER);
        itemModel(CobblemonFarmersRegistery.ItemRegistry.DARK_TYPE_WORKER);
        itemModel(CobblemonFarmersRegistery.ItemRegistry.FAIRY_TYPE_WORKER);

    }

    public void itemModel(RegistryObject<Item> item) {
        withExistingParent(item.getId().getPath(),"item/generated").texture("layer0", CobblemonFarmers.MODID + ":item/" + item.getId().getPath());
    }
}