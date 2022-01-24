package fr.xpdustry.chat;

import mindustry.gen.*;
import mindustry.net.Administration.*;

import fr.xpdustry.distributor.command.sender.*;

import cloud.commandframework.execution.preprocessor.*;
import org.checkerframework.checker.nullness.qual.*;


@FunctionalInterface
public interface ChatPermissionHandler extends ChatFilter, CommandPreprocessor<ArcCommandSender>{
    boolean isMuted(Playerc player);

    @Override default void accept(@NonNull CommandPreprocessingContext<ArcCommandSender> ctx){
        if(!isMuted(ctx.getCommandContext().getSender().asPlayer()))
            ctx.getCommandContext().getSender().addPermission("chat-manager:chat");
    }

    @Override default String filter(Player player, String message){
        if(isMuted(player)){
            player.sendMessage("You are muted...");
            return null;
        }else{
            return message;
        }
    }
}
