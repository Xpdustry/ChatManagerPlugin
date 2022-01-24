package fr.xpdustry.chat.api;

import org.checkerframework.checker.nullness.qual.*;


public interface ChannelMember{
    @NonNull String getName();

    @NonNull String getUUID();

    void sendMessage(@NonNull String message);
}
