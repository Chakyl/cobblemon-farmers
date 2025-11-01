package io.github.chakyl.cobbleworkers;

import com.mojang.logging.LogUtils;
import io.github.chakyl.cobbleworkers.registry.CobbleWorkersRegistery;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

@Mod(CobbleWorkers.MODID)
public class CobbleWorkers {
    public static final String MODID = "cobble_workers";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static boolean GROWTH_EDITION_INSTALLED = false;
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MODID, MODID))
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .networkProtocolVersion(() -> "1.0.0")
            .simpleChannel();

    public CobbleWorkers() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        CobbleWorkersRegistery.register();
        MinecraftForge.EVENT_BUS.register(this);
    }
}