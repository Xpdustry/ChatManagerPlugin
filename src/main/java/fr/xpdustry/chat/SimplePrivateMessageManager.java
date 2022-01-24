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

    private final ObjectMap<Playerc, ChannelMember> members = new ObjectMap<>();
    private final ObjectMap<Playerc, Channel> replies = new ObjectMap<>();
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
    // @CommandPermission("chat-manager:chat")
    @CommandDescription("Whisper a message to another player.")
    public void sendMessage(
        final @NonNull ArcCommandSender sender,
        final @NonNull @Argument("player") Player target,
        final @NonNull @Argument("message") @Greedy String message
    ){
        if(sender.asPlayer() == target){
            sender.send("Error can't message yourself.");
        }else{
            var channel = getChannel((Player)sender.asPlayer(), target);
            channel.broadCastMessage(formatter.format(channel, members.get(sender.asPlayer()), message));

            replies.put(sender.asPlayer(), channel);
            replies.put(target, channel);
        }
    }

    @CommandMethod("reply|r <message>")
    // @CommandPermission("chat-manager:chat")
    @CommandDescription("Whisper a message to another player.")
    public void sendMessage(
        final @NonNull ArcCommandSender sender,
        final @NonNull @Argument("message") @Greedy String message
    ){
        final var channel = replies.get(sender.asPlayer());

        if(channel == null){
            sender.send("Error, no recent channel.");
        }else{
            channel.broadCastMessage(formatter.format(channel, members.get(sender.asPlayer()), message));
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

    private void onChannelClose(@NonNull Channel channel){
        channels.remove(channel);
        channel.getMembers().forEach(m -> {
            if(m instanceof LocalChannelMember l) replies.remove(l.getPlayer());
        });
    }

    private @NonNull Channel getChannel(
        final @NonNull ChannelMember sender,
        final @NonNull ChannelMember target
    ){
        for(final var channel : channels){
            if(channel.hasMember(sender) && channel.hasMember(target)) return channel;
        }

        final var channel = new PrivateMessageChannel(sender, target);
        channels.add(channel);
        return channel;
    }

    private @NonNull Channel getChannel(
        final @NonNull Player sender,
        final @NonNull Player target
    ){
        return getChannel(
            members.get(sender, () -> new LocalChannelMember(sender)),
            members.get(target, () -> new LocalChannelMember(target))
        );
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
