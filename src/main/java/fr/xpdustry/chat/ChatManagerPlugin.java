package fr.xpdustry.chat;

import mindustry.*;
import mindustry.gen.*;

import fr.xpdustry.chat.api.*;
import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.command.argument.PlayerArgument.*;
import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.plugin.*;

import cloud.commandframework.annotations.*;
import io.leangen.geantyref.*;
import org.checkerframework.checker.nullness.qual.*;


public class ChatManagerPlugin extends AbstractPlugin{
    // private static @SuppressWarnings("NullAway.Init") ChatManagerConfig config;
    private static final ChannelManager channelManager = new SimplePrivateMessageManager((channel, member, message) -> {
        if(member instanceof LocalChannelMember m){
            return "[lightgray]<W>[] " + Vars.netServer.chatFormatter.format(m.getPlayer(), message);
        }else{
            return message;
        }
    });


    /**
     * This method is called when game initializes.
     */
    @Override
    public void init(){
        // config = getConfig(ChatManagerConfig.class);
    }

    @Override public void registerClientCommands(@NonNull ArcCommandManager manager){
        manager.getParserRegistry().registerParserSupplier(TypeToken.get(Player.class), p -> new PlayerParser<>());
        manager.getParserRegistry().registerParserSupplier(TypeToken.get(Playerc.class), p -> new PlayerParser<>());
        final var processor = new AnnotationParser<>(manager, ArcCommandSender.class, p -> manager.createDefaultCommandMeta());
        processor.parse(channelManager);
    }

    public static record PlayerMuteEvent(@NonNull Playerc player){
    }

    public static record PlayerUnmuteEvent(@NonNull Playerc player){
    }
}
