package de.moritz.databases;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class WaypointsDatabase {
    private static Connection connection;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            File dbFile = new File("plugins/WaypointPlugin/waypoints.db");
            File dbDir = dbFile.getParentFile();
            if (!dbDir.exists()) {
                dbDir.mkdirs();
            }
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getPath());
            initialize();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void initialize() {
        try (Statement statement = connection.createStatement()) {
            // Update table creation to include the icon column
            String createTableSQL = "CREATE TABLE IF NOT EXISTS waypoints (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "player_uuid TEXT, " +
                    "name TEXT, " +
                    "world TEXT, " +
                    "x DOUBLE, " +
                    "y DOUBLE, " +
                    "z DOUBLE, " +
                    "icon TEXT)";
            statement.execute(createTableSQL);

            // Update existing table to add the icon column if it doesn't exist
            String addColumnSQL = "ALTER TABLE waypoints ADD COLUMN icon TEXT";
            try {
                statement.execute(addColumnSQL);
            } catch (SQLException e) {
                // This will throw an error if the column already exists, so we ignore it
                if (!e.getMessage().contains("duplicate column name")) {
                    throw e;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
