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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaypointMenu {
    public static Map<Player, Map<String, Object>> pendingWaypoints = new HashMap<>();

    public static void openMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Wegpunkte");

        List<Map<String, Object>> waypoints = WaypointStorage.getWaypoints(player);
        for (int i = 0; i < waypoints.size() && i < 26; i++) {
            Map<String, Object> waypoint = waypoints.get(i);
            ItemStack waypointItem = new ItemStack(Material.PAPER); // Standard-Icon
            ItemMeta waypointItemMeta = waypointItem.getItemMeta();
            String name = (String) waypoint.get("name");
            waypointItemMeta.setDisplayName(ChatColor.AQUA + name);

            Location location = (Location) waypoint.get("location");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "X: " + (int)location.getX());
            lore.add(ChatColor.GRAY + "Y: " + (int)location.getY());
            lore.add(ChatColor.GRAY + "Z: " + (int)location.getZ());
            waypointItemMeta.setLore(lore);

            waypointItem.setItemMeta(waypointItemMeta);
            inventory.setItem(i, waypointItem);
        }

        ItemStack addItem = new ItemStack(Material.EMERALD);
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
            if (clickedItem.getType() == Material.EMERALD) {
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Gebe den Namen für den neuen Wegpunkt ein:");
                Map<String, Object> waypoint = new HashMap<>();
                waypoint.put("location", player.getLocation());
                pendingWaypoints.put(player, waypoint);
            } else {
                for (Map<String, Object> waypoint : WaypointStorage.getWaypoints(player)) {
                    if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.AQUA + (String) waypoint.get("name"))) {
                        if (player.getInventory().contains(Material.DIAMOND)) {
                            player.getInventory().removeItem(new ItemStack(Material.DIAMOND, 1));
                            player.teleport((Location) waypoint.get("location"));
                            player.sendMessage(ChatColor.AQUA + "Für einen Diamanten zu " + waypoint.get("name") + " teleportiert.");
                        } else {
                            player.sendMessage(ChatColor.RED + "Du benötigst einen Diamanten um dich teleportieren zu können.");
                        }
                        break;
                    }
                }
            }
        }
    }

    public static void handleChatInput(Player player, String message) {
        Map<String, Object> waypoint = pendingWaypoints.remove(player);
        if (waypoint != null) {
            waypoint.put("name", message);
            WaypointStorage.addWaypoint(player, waypoint);
            player.sendMessage(ChatColor.GREEN + "Waypoint " + message + " added.");
        }
    }
}
