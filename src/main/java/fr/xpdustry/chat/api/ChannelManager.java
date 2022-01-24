package fr.xpdustry.chat.api;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public interface ChannelManager{
    @NonNull Collection<Channel> getChannels();

    void close();
}
