package de.moritz;

import de.moritz.storages.WaypointStorage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WaypointTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        if (args.length == 1) {
            return Arrays.asList("add", "list", "remove", "tp");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            Player player = (Player) sender;
            List<String> waypointNames = new ArrayList<>();
            for (Map<String, Object> waypoint : WaypointStorage.getWaypoints(player)) {
                waypointNames.add((String) waypoint.get("name"));
            }
            return waypointNames;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("tp")) {
            Player player = (Player) sender;
            List<String> waypointNames = new ArrayList<>();
            for (Map<String, Object> waypoint : WaypointStorage.getWaypoints(player)) {
                waypointNames.add((String) waypoint.get("name"));
            }
            return waypointNames;
        }

        return null;
    }
}

