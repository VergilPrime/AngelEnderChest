package com.vergilprime.angelenderchest.sqlite;

import com.vergilprime.angelenderchest.AngelEnderChest;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;


public abstract class Database {
    AngelEnderChest plugin;
    Connection connection;
    String dbname;
    String tablename;

    public Database(AngelEnderChest plugin) {
        this.plugin = plugin;
        dbname = plugin.config.getString("database");
        tablename = plugin.config.getString("tablename");
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize() {
        connection = getSQLConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `" + tablename + "` LIMIT 1;");
            ResultSet rs = ps.executeQuery();
            close(ps, rs);

        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retrieve connection", ex);
        }
    }

    // These are the methods you can use to get things out of your database. You of course can make new ones to return different things in the database.
    // This returns the number of people the player killed.
    public Inventory getEnderChest(UUID uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT ender_chest FROM " + tablename + " WHERE uuid = '" + uuid + "';");

            rs = ps.executeQuery();

            while (rs.next()) {
                String invstring = rs.getString("ender_chest");

                if (!invstring.isEmpty()) {
                    Inventory inventory = InventorySerializer.fromBase64(invstring);

                    return inventory;
                } else {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                    return Bukkit.createInventory((Player) player, 27, player.getName() + "'s Ender Chest");
                }
            }
        } catch (SQLException | IOException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;
    }

    // Now we need methods to save things to the database
    public void saveEnderChest(UUID uuid, Inventory aechest) {
        String stringInventory = InventorySerializer.toBase64(aechest);
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO " + tablename + " (uuid,ender_chest) VALUES(?,?)");
            ps.setObject(1, uuid);
            ps.setString(2, stringInventory);
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;
    }


    public void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }
}

// Credit: https://www.spigotmc.org/threads/how-to-sqlite.56847/