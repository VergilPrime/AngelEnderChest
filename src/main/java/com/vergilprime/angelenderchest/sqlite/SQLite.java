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
    String dbname;

    public SQLite(AngelEnderChest instance) {
        super(instance);
        dbname = plugin.getConfig().getString("SQLite.Filename", "ender_chests"); // Set the table name here e.g player_kills
    }

    public String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS `ender_chests` (" +
            "`uuid` UUID NOT NULL," +
            "`ender_chest` VARCHAR(64) NOT NULL" +
            "PRIMARY KEY (`uuid`)" +
            ");";


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
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
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
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(SQLiteCreateTokensTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
}

// Credit: https://www.spigotmc.org/threads/how-to-sqlite.56847/