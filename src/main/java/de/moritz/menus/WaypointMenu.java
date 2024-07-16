package de.moritz.menus;

import de.moritz.storages.WaypointStorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class WaypointMenu {
    public static void openMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Wegpunkte");

        List<Location> waypoints = WaypointStorage.getWaypoints(player);
        for (int i = 0; i < waypoints.size() && i < 26; i++) {
            Location location = waypoints.get(i);
            ItemStack waypointItem = new ItemStack(Material.PAPER); // Material.PAPER für Papier
            ItemMeta waypointItemMeta = waypointItem.getItemMeta();
            waypointItemMeta.setDisplayName(ChatColor.AQUA + "Wegpunkt " + (i + 1));
            waypointItem.setItemMeta(waypointItemMeta);
            inventory.setItem(i, waypointItem);
        }

        ItemStack addItem = new ItemStack(Material.EMERALD); // Material.EMERALD für Smaragd
        ItemMeta addItemMeta = addItem.getItemMeta();
        addItemMeta.setDisplayName(ChatColor.GREEN + "Add Waypoint");
        addItem.setItemMeta(addItemMeta);
        inventory.setItem(26, addItem);

        player.openInventory(inventory);
    }

    public static void handleMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        Inventory inventory = event.getClickedInventory();

        if (clickedItem != null) {
            if (clickedItem.getType() == Material.EMERALD) { // Material.EMERALD für Smaragd
                // Handle add waypoint
                WaypointStorage.addWaypoint(player, player.getLocation());
                player.sendMessage(ChatColor.GREEN + "Wegpunkt hinzugefügt!");
            } else if (clickedItem.getType() == Material.PAPER) { // Material.PAPER für Papier
                int slot = event.getSlot();
                Location location = WaypointStorage.getWaypoints(player).get(slot);
                player.teleport(location);
                player.sendMessage(ChatColor.AQUA + "Teleportiert zu Wegpunkt " + (slot + 1));
            }
        }
    }
}
