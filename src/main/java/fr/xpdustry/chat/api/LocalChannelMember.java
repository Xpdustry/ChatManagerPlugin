package fr.xpdustry.chat.api;

import mindustry.gen.*;

import fr.xpdustry.chat.api.*;

import org.checkerframework.checker.nullness.qual.*;


public class LocalChannelMember implements ChannelMember{
    private final @NonNull Player player;

    public LocalChannelMember(@NonNull Player player){
        this.player = player;
    }

    public @NonNull Player getPlayer(){
        return player;
    }

    @Override
    public @NonNull String getName(){
        return player.name();
    }

    @Override
    public @NonNull String getUUID(){
        return player.uuid();
    }

    @Override
    public void sendMessage(@NonNull String message){
        player.sendMessage(message);
    }
}
