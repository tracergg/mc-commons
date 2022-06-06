package gg.tracer.commons.menu;

import gg.tracer.commons.util.Players;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import java.util.Arrays;

/**
 * @author Bradley Steele
 */
public final class Menus {

    private Menus() {}

    public static void close(Iterable<? extends Player> players, Class<? extends InventoryHolder> menu) {
        for (Player player : players) {
            if (menu.isInstance(player.getOpenInventory().getTopInventory().getHolder())) {
                player.closeInventory();
            }
        }
    }

    public static void close(Class<? extends InventoryHolder> menu) {
        close(Players.getOnlinePlayers(), menu);
    }

    public static void close(Iterable<? extends Player> players) {
        for (Player player : players) {
            player.closeInventory();
        }
    }

    public static void close(Player... players) {
        close(Arrays.asList(players));
    }
}
