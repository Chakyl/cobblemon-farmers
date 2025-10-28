package io.github.chakyl.cobbleworkers;

import com.mojang.logging.LogUtils;
import io.github.chakyl.cobbleworkers.registry.CobbleWorkersRegistery;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(CobbleWorkers.MODID)
public class CobbleWorkers {
    public static final String MODID = "cobble_workers";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static boolean GROWTH_EDITION_INSTALLED = false;

    public CobbleWorkers() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        CobbleWorkersRegistery.register();
        MinecraftForge.EVENT_BUS.register(this);
    }
}