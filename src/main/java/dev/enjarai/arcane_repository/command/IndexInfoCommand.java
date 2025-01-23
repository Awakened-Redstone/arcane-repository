package dev.enjarai.arcane_repository.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.enjarai.arcane_repository.extension.IndexesBooks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class IndexInfoCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
          literal("index-info")
            .then(
              argument("pos", BlockPosArgumentType.blockPos())
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    BlockPos pos = BlockPosArgumentType.getValidBlockPos(context, "pos");

                    BlockEntity blockEntity = source.getWorld().getBlockEntity(pos);
                    if (blockEntity instanceof IndexesBooks bookshelf) {
                        StringBuilder message = new StringBuilder();
                        bookshelf.arcane_repository$getIndex().forEach((item, slots) -> {
                            message
                              .append(item.getName().getString())
                              .append(" ")
                              .append(Arrays.toString(slots.toArray()))
                              .append("\n");
                        });
                        if (message.isEmpty()) {
                            message.append("No items!");
                        } else {
                            message.deleteCharAt(message.length() - 1);
                        }
                        source.sendMessage(Text.literal(message.toString()));
                    }
                    return 0;
                })
            )
        );
    }
}
