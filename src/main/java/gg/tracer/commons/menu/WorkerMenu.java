package gg.tracer.commons.menu;

import gg.tracer.commons.register.worker.TracerWorker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * @author Bradley Steele
 */
public class WorkerMenu extends TracerWorker {

    @Override
    public void unregister() {
        Menus.close(Menu.class);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof Menu) {
            ((Menu) holder).onClick(event, (Player) event.getWhoClicked(), event.getCurrentItem());
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof Menu) {
            ((Menu) holder).onDrag(event, (Player) event.getWhoClicked(), event.getCursor());
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof Menu) {
            ((Menu) holder).onClose(event, (Player) event.getPlayer());
        }
    }
}
