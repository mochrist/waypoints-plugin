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
            Material.DIAMOND, Material.GOLD_INGOT, Material.IRON_INGOT, Material.EMERALD, Material.REDSTONE
    };
    private static final int DIAMONDS_NEEDED = 1;
    private static final int DIAMONDS_NEEDED_FOR_DEATH = 10;

    public static void openMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Wegpunkte");

        List<Map<String, Object>> waypoints = WaypointStorage.getWaypoints(player);
        for (int i = 0; i < waypoints.size() && i < 25; i++) { // Change 26 to 25 to leave space for the death location
            Map<String, Object> waypoint = waypoints.get(i);
            ItemStack waypointItem = new ItemStack((Material) waypoint.getOrDefault("icon", Material.PAPER));
            ItemMeta waypointItemMeta = waypointItem.getItemMeta();
            String name = (String) waypoint.get("name");
            waypointItemMeta.setDisplayName(ChatColor.AQUA + name);

            Location location = (Location) waypoint.get("location");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "X: " + location.getX());
            lore.add(ChatColor.GRAY + "Y: " + location.getY());
            lore.add(ChatColor.GRAY + "Z: " + location.getZ());
            waypointItemMeta.setLore(lore);

            waypointItem.setItemMeta(waypointItemMeta);
            inventory.setItem(i, waypointItem);
        }

        // Add the last death location to the menu
        Location deathLocation = WaypointStorage.getLastDeathLocation(player);
        if (deathLocation != null) {
            ItemStack deathItem = new ItemStack(Material.BONE);
            ItemMeta deathItemMeta = deathItem.getItemMeta();
            deathItemMeta.setDisplayName(ChatColor.RED + "Last Death Location");

            List<String> deathLore = new ArrayList<>();
            deathLore.add(ChatColor.GRAY + "X: " + deathLocation.getX());
            deathLore.add(ChatColor.GRAY + "Y: " + deathLocation.getY());
            deathLore.add(ChatColor.GRAY + "Z: " + deathLocation.getZ());
            deathItemMeta.setLore(deathLore);

            deathItem.setItemMeta(deathItemMeta);
            inventory.setItem(26, deathItem);
        }

        player.openInventory(inventory);
    }

    public static void openIconSelectionMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.BLUE + "Select Icon");

        for (int i = 0; i < ICON_CHOICES.length; i++) {
            ItemStack iconItem = new ItemStack(ICON_CHOICES[i]);
            ItemMeta iconItemMeta = iconItem.getItemMeta();
            iconItemMeta.setDisplayName(ChatColor.AQUA + ICON_CHOICES[i].name());
            iconItem.setItemMeta(iconItemMeta);
            inventory.setItem(i, iconItem);
        }

        player.openInventory(inventory);
    }

    public static void handleMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem != null) {
            String title = event.getView().getTitle();
            if (title.equals(ChatColor.GREEN + "Wegpunkte")) {
                if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "Last Death Location")) {
                    Location deathLocation = WaypointStorage.getLastDeathLocation(player);
                    if (deathLocation != null && hasEnoughDiamonds(player, DIAMONDS_NEEDED_FOR_DEATH)) {
                        removeDiamonds(player, DIAMONDS_NEEDED_FOR_DEATH);
                        player.teleport(deathLocation);
                        player.sendMessage(ChatColor.AQUA + "Teleported to your last death location for " + DIAMONDS_NEEDED_FOR_DEATH + " diamonds.");
                    } else if (deathLocation != null) {
                        player.sendMessage(ChatColor.RED + "You need " + DIAMONDS_NEEDED_FOR_DEATH + " diamonds to teleport to your last death location.");
                    } else {
                        player.sendMessage(ChatColor.RED + "No death location found.");
                    }
                } else {
                    for (Map<String, Object> waypoint : WaypointStorage.getWaypoints(player)) {
                        if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.AQUA + (String) waypoint.get("name"))) {
                            if (hasEnoughDiamonds(player, DIAMONDS_NEEDED)) {
                                removeDiamonds(player, DIAMONDS_NEEDED);
                                player.teleport((Location) waypoint.get("location"));
                                player.sendMessage(ChatColor.AQUA + "Teleported to waypoint " + waypoint.get("name") + " for " + DIAMONDS_NEEDED + " diamonds.");
                            } else {
                                player.sendMessage(ChatColor.RED + "You need " + DIAMONDS_NEEDED + " diamonds to teleport to this waypoint.");
                            }
                            break;
                        }
                    }
                }
            } else if (title.equals(ChatColor.BLUE + "Select Icon")) {
                Map<String, Object> waypointData = pendingWaypoints.get(player);
                if (waypointData != null) {
                    waypointData.put("icon", clickedItem.getType());
                    pendingWaypoints.put(player, waypointData);
                    player.closeInventory();
                    String waypointName = (String) waypointData.get("name");
                    WaypointStorage.addWaypoint(player, waypointData);
                    player.sendMessage(ChatColor.GREEN + "Waypoint " + waypointName + " added.");
                    pendingWaypoints.remove(player); // Entferne den Eintrag, nachdem der Wegpunkt hinzugefügt wurde
                }
            }
        }
    }

    private static boolean hasEnoughDiamonds(Player player, int amount) {
        int diamondCount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.DIAMOND) {
                diamondCount += item.getAmount();
                if (diamondCount >= amount) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void removeDiamonds(Player player, int amount) {
        int diamondsToRemove = amount;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.DIAMOND) {
                if (item.getAmount() > diamondsToRemove) {
                    item.setAmount(item.getAmount() - diamondsToRemove);
                    break;
                } else {
                    diamondsToRemove -= item.getAmount();
                    player.getInventory().remove(item);
                    if (diamondsToRemove <= 0) {
                        break;
                    }
                }
            }
        }
    }

    public static void handleChatInput(Player player, String message) {
        Map<String, Object> waypoint = new HashMap<>();
        waypoint.put("name", message);
        waypoint.put("location", player.getLocation());
        pendingWaypoints.put(player, waypoint);
        openIconSelectionMenu(player);
    }
}