package de.moritz.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import net.md_5.bungee.api.ChatColor;
import de.moritz.menus.WaypointMenu;

public class MenuListener implements Listener {
    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GREEN + "Wegpunkte")) {
            event.setCancelled(true); // Verhindert, dass Spieler Gegenstände im Menü bewegen
            WaypointMenu.handleMenuClick(event); // Delegiert die Klickbehandlung an die WaypointMenu-Klasse
        }
    }
}
