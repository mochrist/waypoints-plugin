package de.moritz.storages;

import de.moritz.WaypointPlugin;
import de.moritz.databases.WaypointsDatabase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaypointStorage {
    private static Map<Player, List<Map<String, Object>>> playerWaypoints = new HashMap<>();
    private static Map<Player, Location> lastDeathLocations = new HashMap<>();

    public static void addWaypoint(Player player, Map<String, Object> waypoint) {
        playerWaypoints.putIfAbsent(player, new ArrayList<>());
        playerWaypoints.get(player).add(waypoint);

        try (Connection conn = WaypointsDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO waypoints (player_uuid, name, world, x, y, z, icon) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, (String) waypoint.get("name"));
            Location location = (Location) waypoint.get("location");
            ps.setString(3, location.getWorld().getName());
            ps.setDouble(4, location.getX());
            ps.setDouble(5, location.getY());
            ps.setDouble(6, location.getZ());
            ps.setString(7, ((Material) waypoint.get("icon")).name());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Map<String, Object>> getWaypoints(Player player) {
        if (!playerWaypoints.containsKey(player)) {
            loadWaypoints(player);
        }
        return playerWaypoints.getOrDefault(player, new ArrayList<>());
    }

    public static void removeWaypoint(Player player, String waypointName) {
        List<Map<String, Object>> waypoints = playerWaypoints.get(player);
        if (waypoints != null) {
            waypoints.removeIf(waypoint -> waypointName.equals(waypoint.get("name")));
        }

        try (Connection conn = WaypointsDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM waypoints WHERE player_uuid = ? AND name = ?")) {
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, waypointName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loadWaypoints(Player player) {
        List<Map<String, Object>> waypoints = new ArrayList<>();
        try (Connection conn = WaypointsDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT name, world, x, y, z, icon FROM waypoints WHERE player_uuid = ?")) {
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> waypoint = new HashMap<>();
                waypoint.put("name", rs.getString("name"));
                Location location = new Location(
                        Bukkit.getWorld(rs.getString("world")),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getDouble("z")
                );
                waypoint.put("location", location);
                waypoint.put("icon", Material.valueOf(rs.getString("icon")));
                waypoints.add(waypoint);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        playerWaypoints.put(player, waypoints);
    }

    public static void setLastDeathLocation(Player player, Location location) {
        lastDeathLocations.put(player, location);
    }

    public static Location getLastDeathLocation(Player player) {
        return lastDeathLocations.get(player);
    }
}
