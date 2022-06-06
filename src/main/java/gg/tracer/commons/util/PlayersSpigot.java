package gg.tracer.commons.util;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * @author Bradley Steele
 */
public final class PlayersSpigot {

    PlayersSpigot() {}

    public void sendMessage(CommandSender receiver, BaseComponent component) {
        if (component == null || !Players.isOnline(receiver)) {
            return;
        }

        receiver.spigot().sendMessage(component);
    }

    public void sendMessage(CommandSender receiver, Iterable<BaseComponent> components) {
        for (BaseComponent component : components) {
            sendMessage(receiver, component);
        }
    }

    public void sendMessage(CommandSender receiver, BaseComponent... components) {
        sendMessage(receiver, Arrays.asList(components));
    }

    public void sendMessage(Iterable<? extends CommandSender> receivers, Iterable<BaseComponent> components) {
        for (CommandSender receiver : receivers) {
            sendMessage(receiver, components);
        }
    }
}
