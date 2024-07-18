package de.moritz;

import de.moritz.databases.WaypointsDatabase;
import org.bukkit.plugin.java.JavaPlugin;
import de.moritz.commands.WaypointCommand;
import de.moritz.listeners.MenuListener;

public class WaypointPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getCommand("waypoint").setExecutor(new WaypointCommand());
        getServer().getPluginManager().registerEvents(new MenuListener(), this);

        // Initialisieren der Datenbank
        WaypointsDatabase.connect();
    }

    @Override
    public void onDisable() {
        // Trennen der Datenbankverbindung
        WaypointsDatabase.disconnect();
    }
}