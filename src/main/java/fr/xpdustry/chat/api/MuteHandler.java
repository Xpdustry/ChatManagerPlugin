package fr.xpdustry.chat.api;

import mindustry.gen.*;

import org.checkerframework.checker.nullness.qual.*;


public interface MuteHandler{
    void mute(@NonNull Player player);

    void unmute(@NonNull Player player);

    boolean isMuted(@NonNull Player player);

    record PlayerMuteEvent(@NonNull Playerc player){
    }

    record PlayerUnmuteEvent(@NonNull Playerc player){
    }
}
