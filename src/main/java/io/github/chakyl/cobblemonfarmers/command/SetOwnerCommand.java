package io.github.chakyl.cobblemonfarmers.command;
import com.mojang.brigadier.CommandDispatcher;
import io.github.chakyl.cobblemonfarmers.blockentity.StationBaseBlockEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class SetOwnerCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("cobblemonfarmerssetowner")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> {
                                            BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
                                            Player player = EntityArgument.getPlayer(context, "player");
                                            if (context.getSource().getLevel().getBlockEntity(pos) instanceof StationBaseBlockEntity stationBaseBlockEntity) {
                                                stationBaseBlockEntity.setOwner(player.getUUID());
                                                context.getSource().sendSuccess(() -> Component.translatable("command.cobblemon_farmers.setowner.success",pos.toShortString(), player.getName().getString()), true);
                                                return 1;
                                            } else {
                                                context.getSource().sendFailure(Component.translatable("command.cobblemon_farmers.setowner.failed"));
                                                return 0;
                                            }
                                        })
                                )
                        )
        );
    }
}