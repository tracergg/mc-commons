package gg.tracer.commons.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author Bradley Steele
 */
public final class Players {

    private static final PlayersSpigot spigot;

    static {
        spigot = ServerSoftware.isSpigot() ? new PlayersSpigot() : null;
    }

    public Players() {}

    public static List<Player> getOnlinePlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    public static Player getPlayer(String s) {
        Player player = Bukkit.getPlayerExact(s);

        if (player == null) {
            player = Bukkit.getPlayer(s);
        }

        return player;
    }

    public static Player getPlayer(UUID uuid) {
        return uuid != null ? Bukkit.getPlayer(uuid) : null;
    }

    public static Player getPlayer(CommandSender sender) {
        return sender instanceof Player ? (Player) sender : null;
    }

    public static boolean isOnline(OfflinePlayer player) {
        return player != null && player.isOnline();
    }

    public static boolean isOnline(CommandSender sender) {
        return !(sender instanceof OfflinePlayer) || isOnline((OfflinePlayer) sender);
    }

    public static void sendMessage(CommandSender receiver, String message) {
        if (message == null || !isOnline(receiver)) {
            return;
        }

        receiver.sendMessage(Messages.color(message));
    }

    @SuppressWarnings("unchecked")
    public static void sendMessage(CommandSender receiver, Iterable<Object> messages) {
        for (Object message : messages) {
            if (message == null) {
                continue;
            }

            if (message instanceof String) {
                sendMessage(receiver, (String) message);
            } else if (message instanceof Iterable) {
                sendMessage(receiver, (Iterable<Object>) message);
            } else {
                sendMessage(receiver, message.toString());
            }
        }
    }

    public static void sendMessage(CommandSender receiver, Object... messages) {
        sendMessage(receiver, Arrays.asList(messages));
    }

    public static void sendMessage(Iterable<? extends CommandSender> receivers, Iterable<Object> messages) {
        for (CommandSender receiver : receivers) {
            sendMessage(receiver, messages);
        }
    }

    public static PlayersSpigot spigot() {
        if (spigot == null) {
            throw new UnsupportedOperationException("this is not a spigot server");
        }

        return spigot;
    }
}
