package de.moritz.commands;

import de.moritz.storages.WaypointStorage;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import de.moritz.menus.WaypointMenu;
import org.bukkit.inventory.ItemStack;

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
            } else if (args.length > 0 && args[0].equalsIgnoreCase("tp") && args.length == 2) {
                String waypointName = args[1];
                teleportToWaypoint(player, waypointName);
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
            player.sendMessage(ChatColor.RED + "Du hast keine Markierungen.");
        } else {
            player.sendMessage(ChatColor.GREEN + "Deine Markierungen:");
            for (Map<String, Object> waypoint : waypoints) {
                String name = (String) waypoint.get("name");
                Location location = (Location) waypoint.get("location");
                TextComponent message = new TextComponent(ChatColor.AQUA + name + ChatColor.GRAY + " - " +
                        "X: " + (int)location.getX() + ", " +
                        "Y: " + (int)location.getY() + ", " +
                        "Z: " + (int)location.getZ());
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/waypoint tp " + name));

                // Hinzufügen des Hover-Effekts
                String hoverText = ChatColor.GOLD + "Teleport zu " + ChatColor.AQUA + name + ChatColor.GOLD + "\n" +
                        "X: " + (int)location.getX() + "\n" +
                        "Y: " + (int)location.getY() + "\n" +
                        "Z: " + (int)location.getZ();
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));

                player.spigot().sendMessage(message);
            }
        }
    }

    private void removeWaypoint(Player player, String waypointName) {
        WaypointStorage.removeWaypoint(player, waypointName);
        player.sendMessage(ChatColor.GREEN + "Markierung " + waypointName + " wurde entfernt.");
    }

    private void teleportToWaypoint(Player player, String waypointName) {
        List<Map<String, Object>> waypoints = WaypointStorage.getWaypoints(player);
        for (Map<String, Object> waypoint : waypoints) {
            if (waypointName.equals(waypoint.get("name"))) {
                Location location = (Location) waypoint.get("location");
                if (isSafeLocation(location)) {
                    if (player.getInventory().contains(Material.DIAMOND)) {
                        player.getInventory().removeItem(new ItemStack(Material.DIAMOND, 1));
                        player.teleport(location);
                        player.sendMessage(ChatColor.AQUA + "Für einen Diamanten zur Markierung " + waypointName + " teleportiert.");
                    } else {
                        player.sendMessage(ChatColor.RED + "Du benötigst einen Diamanten um dich zu teleportieren.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "The waypoint location is not safe for teleportation.");
                }
                return;
            }
        }
        player.sendMessage(ChatColor.RED + "Markierung " + waypointName + " nicht gefunden.");
    }

    public static boolean isSafeLocation(Location location) {
        Block feet = location.getBlock();
        if (!feet.getType().isTransparent() && !feet.getLocation().add(0, 1, 0).getBlock().getType().isTransparent()) {
            return false; // not transparent (will suffocate)
        }
        Block head = feet.getRelative(BlockFace.UP);
        if (!head.getType().isTransparent()) {
            return false; // not transparent (will suffocate)
        }
        Block ground = feet.getRelative(BlockFace.DOWN);
        if (!ground.getType().isSolid()) {
            return false; // not solid
        }
        return true;
    }
}