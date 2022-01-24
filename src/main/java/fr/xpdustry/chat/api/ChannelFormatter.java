package fr.xpdustry.chat.api;


import org.checkerframework.checker.nullness.qual.*;


@FunctionalInterface
public interface ChannelFormatter{
    @NonNull String format(@NonNull Channel channel, @NonNull String member, @NonNull String message);
}
