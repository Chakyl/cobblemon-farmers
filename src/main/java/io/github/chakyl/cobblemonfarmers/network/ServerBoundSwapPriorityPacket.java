package io.github.chakyl.cobblemonfarmers.network;

import io.github.chakyl.cobblemonfarmers.screen.AbstractWorkerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerBoundSwapPriorityPacket {

    public ServerBoundSwapPriorityPacket() {

    }

    public ServerBoundSwapPriorityPacket(FriendlyByteBuf buffer) {

    }

    public void encode(FriendlyByteBuf buffer) {

    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player != null) {
            if (player.containerMenu instanceof AbstractWorkerMenu menu && menu.stillValid(player)) {
                menu.setPrioritySwapped();
                menu.broadcastChanges();
            }
        }
        context.get().setPacketHandled(true);

    }
}