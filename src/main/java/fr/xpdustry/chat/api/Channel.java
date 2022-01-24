package fr.xpdustry.chat.api;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public interface Channel{
    @NonNull ChannelAccess getAccess();

    @NonNull Collection<String> getMembers();

    default boolean hasMember(@NonNull String member){
        return getMembers().contains(member);
    }

    enum ChannelAccess{
        PUBLIC,
        PROTECTED,
        PRIVATE
    }
}
