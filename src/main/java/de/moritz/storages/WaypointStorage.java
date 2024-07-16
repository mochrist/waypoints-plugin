package de.moritz.storages;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaypointStorage {
    private static Map<Player, List<Location>> playerWaypoints = new HashMap<>();

    public static void addWaypoint(Player player, Location location) {
        playerWaypoints.putIfAbsent(player, new ArrayList<>());
        playerWaypoints.get(player).add(location);
    }

    public static List<Location> getWaypoints(Player player) {
        return playerWaypoints.getOrDefault(player, new ArrayList<>());
    }
}
