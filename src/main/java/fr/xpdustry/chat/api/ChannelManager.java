package fr.xpdustry.chat.api;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public interface ChannelManager<C extends Channel>{
    @NonNull Collection<C> getChannels();

    void onChannelOpen(@NonNull C channel);

    void onChannelClose(@NonNull C channel);
    // channels.keySet().removeIf(this::hasMember);

    void close();
}
