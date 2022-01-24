package fr.xpdustry.chat;

import arc.struct.*;

import mindustry.game.EventType.*;
import mindustry.gen.*;

import fr.xpdustry.chat.api.*;
import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.event.*;
import fr.xpdustry.distributor.struct.*;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.specifier.*;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public class SimplePrivateMessageManager implements ChannelManager{
    private final @NonNull ChannelFormatter formatter;

    private final Map<Player, LocalChannelMember> members = new HashMap<>();
    private final Map<ChannelMember, Channel> replies = new HashMap<>();
    private final Seq<Channel> channels = new Seq<>();

    private final EventWatcher<PlayerLeave> listener = new EventWatcher<>(PlayerLeave.class, e -> {
        final var member = members.remove(e.player);
        if(member != null) getChannels(member).forEach(this::onChannelClose);
    });

    public SimplePrivateMessageManager(@NonNull ChannelFormatter formatter){
        this.formatter = formatter;
        listener.listen();
    }

    @CommandMethod("whisper|w <player> <message>")
    @CommandPermission("chat-manager:chat")
    @CommandDescription("Whisper a message to another player.")
    public void sendMessage(
        final @NonNull ArcCommandSender sender,
        final @NonNull @Argument("player") Player target,
        final @NonNull @Argument("message") @Greedy String message
    ){
        if(sender.asPlayer() == target){
            sender.send("Error can't message yourself.");
        }else{
            var channel = getChannel(sender.asPlayer(), target);
            onChannelMessage(channel, members.get(sender.asPlayer()), message);
        }
    }

    @CommandMethod("reply|r <message>")
    @CommandPermission("chat-manager:chat")
    @CommandDescription("Whisper a message to another player.")
    public void sendMessage(
        final @NonNull ArcCommandSender sender,
        final @NonNull @Argument("message") @Greedy String message
    ){
        final var member = members.get(sender.asPlayer());
        final var channel = member == null ? null : replies.get(member);

        if(channel == null){
            sender.send("Error, no recent channel.");
        }else{
            onChannelMessage(channel, member, message);
        }
    }

    @Override public @NonNull Collection<Channel> getChannels(){
        return Collections.unmodifiableCollection(new ArcList<>(channels.as()));
    }

    @Override public void close(){
        listener.stop();
    }

    private Collection<Channel> getChannels(@NonNull ChannelMember member){
        return getChannels().stream().filter(c -> c.hasMember(member)).toList();
    }

    private @NonNull Channel getChannel(final @NonNull ChannelMember sender, final @NonNull ChannelMember target){
        for(final var channel : channels){
            if(channel.hasMember(sender) && channel.hasMember(target)) return channel;
        }

        final var channel = new PrivateMessageChannel(sender, target);
        onChannelOpen(channel);
        return channel;
    }

    private @NonNull Channel getChannel(final @NonNull Player sender, final @NonNull Player target){
        return getChannel(
            members.computeIfAbsent(sender, LocalChannelMember::new),
            members.computeIfAbsent(target, LocalChannelMember::new)
        );
    }

    private void onChannelOpen(@NonNull Channel channel){
        channels.add(channel);
    }

    private void onChannelClose(@NonNull Channel channel){
        channels.remove(channel);
        channel.getMembers().forEach(replies::remove);
    }

    private void onChannelMessage(
        final @NonNull Channel channel,
        final @NonNull ChannelMember member,
        final @NonNull String message
    ){
        channel.broadCastMessage(formatter.format(channel, member, message));
        channel.getMembers().forEach(m -> replies.put(m, channel));
    }

    public static class PrivateMessageChannel implements Channel{
        private final Collection<ChannelMember> members;

        public PrivateMessageChannel(
            final @NonNull ChannelMember memberA,
            final @NonNull ChannelMember memberB
        ){
            members = Set.of(memberA, memberB);
        }

        @Override public @NonNull ChannelAccess getAccess(){
            return ChannelAccess.PRIVATE;
        }

        @NonNull @Override public Collection<ChannelMember> getMembers(){
            return members;
        }
    }
}
