package fr.xpdustry.chat;

import arc.*;

import mindustry.gen.*;

import fr.xpdustry.chat.api.*;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public class SimpleMuteHandler implements MuteHandler{
    private final long muteDuration;
    private final HashMap<String, Long> mutes = new HashMap<>();

    public SimpleMuteHandler(long muteDuration){
        this.muteDuration = muteDuration * 1000L;
    }

    @Override public void mute(@NonNull Player player){
        mutes.put(player.uuid(), System.currentTimeMillis());
        Events.fire(new PlayerMuteEvent(player));
    }

    @Override public void unmute(@NonNull Player player){
        mutes.remove(player.uuid());
        Events.fire(new PlayerUnmuteEvent(player));
    }

    @Override public boolean isMuted(@NonNull Player player){
        if(mutes.containsKey(player.uuid())){
            if(mutes.get(player.uuid()) + muteDuration > System.currentTimeMillis()){
                return true;
            }else{
                unmute(player);
            }
        }

        return false;
    }
}
