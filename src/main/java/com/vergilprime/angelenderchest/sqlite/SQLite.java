package com.vergilprime.angelenderchest.sqlite;

import com.vergilprime.angelenderchest.AngelEnderChest;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;


public class SQLite extends Database {
    private String dbname;
    private String tablename;


    public SQLite(AngelEnderChest plugin) {
        super(plugin);
        dbname = plugin.config.getString("database");
        tablename = plugin.config.getString("tablename");
    }


    // SQL creation stuff, You can leave the blow stuff untouched.
    @Override
    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname + ".db");
        if (!dataFolder.exists()) {
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: " + dbname + ".db");
            }
        }
        try {
            if (connection != null && !connection.isClosed()) {
                plugin.getLogger().log(Level.INFO, "SQLite connection already exists");
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            plugin.getLogger().log(Level.INFO, "New SQLite connection established");
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Please grab it from https://github.com/xerial/sqlite-jdbc/releases and put it in your server directory /libs folder.");
        }
        return null;
    }

    @Override
    public void load() {
        connection = getSQLConnection();
        if (connection != null) {
            plugin.getLogger().log(Level.INFO, "Connection is not null");
        } else {
            plugin.getLogger().log(Level.SEVERE, "Connection null");
        }
        try {
            Statement statement = connection.createStatement();
            plugin.getLogger().log(Level.INFO, "Executing table creation.");
            boolean result = statement.execute("CREATE TABLE IF NOT EXISTS `" + tablename + "` (" +
                    "`uuid` UUID NOT NULL, " +
                    "`ender_chest` TEXT NOT NULL, " +
                    "PRIMARY KEY (`uuid`)" +
                    ");");
            plugin.getLogger().log(Level.INFO, "Table creaetion complete: " + result);
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "SQLite failed to load", e);
        }
        initialize();
    }
}

// Credit: https://www.spigotmc.org/threads/how-to-sqlite.56847/