package fr.xpdustry.chat;

import arc.util.*;

import mindustry.*;
import mindustry.gen.*;

import fr.xpdustry.chat.api.*;
import fr.xpdustry.chat.internal.*;
import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.command.argument.*;
import fr.xpdustry.distributor.plugin.*;

import cloud.commandframework.permission.*;
import org.checkerframework.checker.nullness.qual.*;


public class ChatManagerPlugin extends AbstractPlugin{
    public static final CommandPermission CHAT_PERMISSION = Permission.of("chat-manager:chat");

    private static @SuppressWarnings("NullAway.Init") MuteHandler muteHandler;
    private static @SuppressWarnings("NullAway.Init") ChatManagerConfig config;

    public static @NonNull MuteHandler getMuteHandler(){
        return muteHandler;
    }

    public static void setMuteHandler(@NonNull MuteHandler muteHandler){
        ChatManagerPlugin.muteHandler = muteHandler;
    }

    @Override
    public void init(){
        config = getConfig(ChatManagerConfig.class);
        muteHandler = new SimpleMuteHandler(config.getMuteDuration());

        Vars.netServer.admins.addChatFilter((p, m) -> {
            if(muteHandler.isMuted(p)){
                p.sendMessage("You are muted");
                return null;
            }else{
                return m;
            }
        });

        Vars.mods.eachEnabled(m -> {

        });
    }

    @Override public void registerClientCommands(@NonNull ArcCommandManager manager){
        manager.getAnnotationParser().parse(new SimplePrivateMessageManager((channel, member, message) -> {
            if(member instanceof LocalChannelMember m){
                return "[lightgray]<W>[] " + Vars.netServer.chatFormatter.format(m.getPlayer(), message);
            }else{
                return message;
            }
        }));

        manager.getPermissionInjector().registerInjector(CHAT_PERMISSION, s -> !muteHandler.isMuted(s.asPlayer()));
    }

    @Override public void registerSharedCommands(@NonNull ArcCommandManager manager){
        manager.command(manager.commandBuilder("mute")
            .meta(ArcMeta.DESCRIPTION, "Mute a player.")
            .meta(ArcMeta.PLUGIN, asLoadedMod().name)
            .permission(ArcPermission.ADMIN)
            .argument(PlayerArgument.of("player"))
            .handler(ctx -> {
                final Player target = ctx.get("player");

                if(muteHandler.isMuted(target)){
                    ctx.getSender().send("The player is already muted.");
                }else{
                    muteHandler.mute(target);
                    ctx.getSender().send("@ has been muted.", target.name());
                }
            })
        );

        manager.command(manager.commandBuilder("unmute")
            .meta(ArcMeta.DESCRIPTION, "Unmute a player.")
            .meta(ArcMeta.PLUGIN, asLoadedMod().name)
            .permission(ArcPermission.ADMIN)
            .argument(PlayerArgument.of("player"))
            .handler(ctx -> {
                final Player target = ctx.get("player");

                if(!muteHandler.isMuted(target)){
                    ctx.getSender().send("The player is not muted.");
                }else{
                    muteHandler.unmute(target);
                    ctx.getSender().send("@ has been unmuted.", target.name());
                }
            })
        );
    }
}
