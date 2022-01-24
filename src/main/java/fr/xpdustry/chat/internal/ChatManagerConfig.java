package fr.xpdustry.chat.internal;

import org.aeonbits.owner.*;
import org.aeonbits.owner.Config.*;


@HotReload
public interface ChatManagerConfig extends Accessible, Reloadable{
    @DefaultValue("5")
    @Key("chat.mute.duration")
    long getMuteDuration();
}
