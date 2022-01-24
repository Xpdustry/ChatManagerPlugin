package fr.xpdustry.chat;

import arc.struct.*;
import arc.util.*;

import mindustry.game.EventType.*;
import mindustry.gen.*;

import fr.xpdustry.chat.SimplePrivateMessageManager.*;
import fr.xpdustry.chat.api.*;
import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.event.*;
import fr.xpdustry.distributor.struct.*;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.specifier.*;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public class SimplePrivateMessageManager implements ChannelManager<PrivateMessageChannel>{
    private final @NonNull ChannelFormatter formatter;
    private final Seq<PrivateMessageChannel> channels = new Seq<>();
    private final Map<String, PrivateMessageChannel> replies = new HashMap<>();
    private final EventWatcher<PlayerLeave> listener = new EventWatcher<>(PlayerLeave.class, e -> {
        channels.select(c -> c.hasMember(e.player.uuid())).forEach(this::onChannelClose);
        replies.remove(e.player.uuid());
    });

    public SimplePrivateMessageManager(@NonNull ChannelFormatter formatter){
        this.formatter = formatter;
        listener.listen();
    }

    @Override public @NonNull Collection<PrivateMessageChannel> getChannels(){
        return Collections.unmodifiableCollection(new ArcList<>(channels));
    }

    @Override public void onChannelOpen(@NonNull PrivateMessageChannel channel){
        channels.add(channel);
    }

    @Override public void onChannelClose(@NonNull PrivateMessageChannel channel){
        channels.remove(channel);
    }

    @Override public void close(){
        listener.stop();
    }

    @CommandMethod("whisper|w <player> <message>")
    // @CommandPermission("chat-manager:chat")
    @CommandDescription("Whisper a message to another player.")
    public void sendMessage(
        final @NonNull ArcCommandSender sender,
        final @NonNull @Argument("player") Player target,
        final @NonNull @Argument("message") @Greedy String message
    ){
        if(sender.asPlayer().uuid().equals(target.uuid())){
            sender.send("Error can't message yourself.");
        }else{
            var channel = getChannel(sender.asPlayer().uuid(), target.uuid());
            broadcast(channel, sender.asPlayer().name(), message);
            replies.put(sender.asPlayer().uuid(), channel);
            replies.put(target.uuid(), channel);
        }
    }

    @CommandMethod("reply|r <message>")
    // @CommandPermission("chat-manager:chat")
    @CommandDescription("Whisper a message to another player.")
    public void sendMessage(
        final @NonNull ArcCommandSender sender,
        final @NonNull @Argument("message") @Greedy String message
    ){
        final var channel = replies.get(sender.asPlayer().uuid());

        if(channel == null){
            sender.send("Error, no recent channel.");
        }else{
            broadcast(channel, sender.asPlayer().name(), message);
        }
    }

    private @NonNull PrivateMessageChannel getChannel(
        final @NonNull String senderUUID,
        final @NonNull String targetUUID
    ){
        for(final var channel : channels){
            if(channel.hasMember(senderUUID) && channel.hasMember(targetUUID)) return channel;
        }

        Log.info(senderUUID, targetUUID);
        final var channel = new PrivateMessageChannel(senderUUID, targetUUID);
        onChannelOpen(channel);
        return channel;
    }

    private void broadcast(
        final @NonNull PrivateMessageChannel channel,
        final @NonNull String name,
        final @NonNull String message
    ){
        Groups.player.each(
            p -> channel.hasMember(p.uuid()),
            p -> p.sendMessage(formatter.format(channel, name, message))
        );
    }

    public static class PrivateMessageChannel implements Channel{
        private final Collection<String> members;

        public PrivateMessageChannel(@NonNull String uuidA, @NonNull String uuidB){
            members = Set.of(uuidA, uuidB);
        }

        @Override public @NonNull ChannelAccess getAccess(){
            return ChannelAccess.PRIVATE;
        }

        @Override public @NonNull Collection<String> getMembers(){
            return members;
        }
    }
}
