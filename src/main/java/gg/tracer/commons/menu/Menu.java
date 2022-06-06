package gg.tracer.commons.menu;

import gg.tracer.commons.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * @author Bradley Steele
 */
public abstract class Menu implements InventoryHolder {

    protected final Inventory inventory;

    public Menu(int size, String title) {
        inventory = Bukkit.createInventory(this, size, Messages.color(title));
    }

    public void onClick(InventoryClickEvent event, Player clicker, ItemStack stack) {
    }

    public void onDrag(InventoryDragEvent event, Player clicker, ItemStack stack) {
    }

    public void onClose(InventoryCloseEvent event, Player player) {
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
