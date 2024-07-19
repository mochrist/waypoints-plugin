package de.moritz.commands;

import de.moritz.storages.WaypointStorage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import de.moritz.menus.WaypointMenu;

import java.util.List;
import java.util.Map;

public class WaypointCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0 && args[0].equalsIgnoreCase("add") && args.length == 2) {
                String waypointName = args[1];
                WaypointMenu.handleChatInput(player, waypointName); // Start the process by setting the waypoint name
                return true;
            } else if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
                listWaypoints(player);
                return true;
            } else if (args.length > 0 && args[0].equalsIgnoreCase("remove") && args.length == 2) {
                String waypointName = args[1];
                removeWaypoint(player, waypointName);
                return true;
            } else {
                WaypointMenu.openMenu(player);
                return true;
            }
        }
        return false;
    }

    private void listWaypoints(Player player) {
        List<Map<String, Object>> waypoints = WaypointStorage.getWaypoints(player);
        if (waypoints.isEmpty()) {
            player.sendMessage(ChatColor.RED + "You have no waypoints.");
        } else {
            player.sendMessage(ChatColor.GREEN + "Your waypoints:");
            for (Map<String, Object> waypoint : waypoints) {
                String name = (String) waypoint.get("name");
                Location location = (Location) waypoint.get("location");
                player.sendMessage(ChatColor.AQUA + name + ChatColor.GRAY + " - " +
                        "X: " + location.getX() + ", " +
                        "Y: " + location.getY() + ", " +
                        "Z: " + location.getZ());
            }
        }
    }

    private void removeWaypoint(Player player, String waypointName) {
        WaypointStorage.removeWaypoint(player, waypointName);
        player.sendMessage(ChatColor.GREEN + "Waypoint " + waypointName + " has been removed.");
    }
}

