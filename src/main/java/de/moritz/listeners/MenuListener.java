package de.moritz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import net.md_5.bungee.api.ChatColor;
import de.moritz.menus.WaypointMenu;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MenuListener implements Listener {
    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.equals(ChatColor.GREEN + "Wegpunkte") || title.equals(ChatColor.BLUE + "Select Icon")) {
            event.setCancelled(true); // Verhindert, dass Spieler Gegenstände im Menü bewegen
            WaypointMenu.handleMenuClick(event); // Delegiert die Klickbehandlung an die WaypointMenu-Klasse
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (WaypointMenu.pendingWaypoints.containsKey(player)) {
            event.setCancelled(true); // Verhindert, dass die Nachricht im Chat angezeigt wird
            WaypointMenu.handleChatInput(player, event.getMessage());
        }
    }
}
