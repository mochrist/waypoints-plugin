package de.moritz;

import de.moritz.databases.WaypointsDatabase;
import org.bukkit.plugin.java.JavaPlugin;
import de.moritz.commands.WaypointCommand;
import de.moritz.listeners.MenuListener;

public class WaypointPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getCommand("waypoint").setExecutor(new WaypointCommand());
        this.getCommand("waypoint").setTabCompleter(new WaypointTabCompleter());
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        // Stellen Sie sicher, dass Ihre Datenbankverbindung hier initialisiert wird, falls erforderlich
    }

    @Override
    public void onDisable() {
        // Trennen der Datenbankverbindung
        WaypointsDatabase.disconnect();
    }
}