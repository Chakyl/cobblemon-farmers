package io.github.chakyl.cobblemonfarmers.network;


import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(
                    new ResourceLocation(CobblemonFarmers.MODID, "main"))
            .serverAcceptedVersions((version) -> true)
            .clientAcceptedVersions((version) -> true)
            .networkProtocolVersion(() -> String.valueOf(1))
            .simpleChannel();

    public static void register() {

        INSTANCE.messageBuilder(ServerBoundSwapPriorityPacket.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ServerBoundSwapPriorityPacket::encode)
                .decoder(ServerBoundSwapPriorityPacket::new)
                .consumerMainThread(ServerBoundSwapPriorityPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.send(PacketDistributor.SERVER.noArg(), message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

}