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
    private static final Material[] ICON_CHOICES = {
            Material.DIAMOND, Material.GOLD_INGOT, Material.IRON_INGOT, Material.EMERALD, Material.REDSTONE, Material.DIAMOND_PICKAXE, Material.CREEPER_HEAD, Material.COOKED_BEEF, Material.RED_BED
    };

    public static void openMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Wegpunkte");

        List<Map<String, Object>> waypoints = WaypointStorage.getWaypoints(player);
        for (int i = 0; i < waypoints.size() && i < 26; i++) {
            Map<String, Object> waypoint = waypoints.get(i);
            ItemStack waypointItem = new ItemStack((Material) waypoint.getOrDefault("icon", Material.PAPER)); // Use saved icon or default to PAPER
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

    public static void openIconSelectionMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.BLUE + "Select Icon");

        for (int i = 0; i < ICON_CHOICES.length; i++) {
            ItemStack iconItem = new ItemStack(ICON_CHOICES[i]);
            ItemMeta iconItemMeta = iconItem.getItemMeta();
            //iconItemMeta.setDisplayName(ChatColor.AQUA + ICON_CHOICES[i].name());
            iconItem.setItemMeta(iconItemMeta);
            inventory.setItem(i, iconItem);
        }

        player.openInventory(inventory);
    }

    public static void handleMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        Inventory inventory = event.getClickedInventory();

        if (clickedItem != null) {
            String title = event.getView().getTitle();
            if (title.equals(ChatColor.GREEN + "Wegpunkte")) {
                if (clickedItem.getType() == Material.EMERALD) {
                    player.closeInventory();
                    openIconSelectionMenu(player);
                } else {
                    for (Map<String, Object> waypoint : WaypointStorage.getWaypoints(player)) {
                        if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.AQUA + (String) waypoint.get("name"))) {
                            if (player.getInventory().contains(Material.DIAMOND)) {
                                player.getInventory().removeItem(new ItemStack(Material.DIAMOND, 1));
                                player.teleport((Location) waypoint.get("location"));
                                player.sendMessage(ChatColor.AQUA + "Teleported to waypoint " + waypoint.get("name") + " for 1 diamond.");
                            } else {
                                player.sendMessage(ChatColor.RED + "You need 1 diamond to teleport to this waypoint.");
                            }
                            break;
                        }
                    }
                }
            } else if (title.equals(ChatColor.BLUE + "Select Icon")) {
                Map<String, Object> waypoint = new HashMap<>();
                waypoint.put("location", player.getLocation());
                waypoint.put("icon", clickedItem.getType());
                pendingWaypoints.put(player, waypoint);
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Enter the name for the new waypoint:");
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
