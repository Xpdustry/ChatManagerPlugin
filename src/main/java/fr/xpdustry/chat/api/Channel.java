package fr.xpdustry.chat.api;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public interface Channel{
    @NonNull ChannelAccess getAccess();

    @NonNull Collection<ChannelMember> getMembers();

    default boolean hasMember(@NonNull ChannelMember member){
        return getMembers().contains(member);
    }

    default void broadCastMessage(@NonNull String message){
        getMembers().forEach(m -> m.sendMessage(message));
    }

    enum ChannelAccess{
        PUBLIC,
        PROTECTED,
        PRIVATE
    }
}
