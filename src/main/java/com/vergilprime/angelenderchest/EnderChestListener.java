package com.vergilprime.angelenderchest;

import com.vergilprime.angelenderchest.sqlite.SQLite;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class EnderChestListener implements Listener {
    AngelEnderChest plugin;
    SQLite sqlite;
    Boolean debugging;

    public EnderChestListener(AngelEnderChest plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        sqlite = plugin.sqlite;
        debugging = plugin.getConfig().getBoolean("debugging");
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        if (debugging) {
            plugin.getLogger().log(Level.INFO, player.getName() + " attempting to open an inventory.");
        }
        if (event.getInventory().getType() == InventoryType.ENDER_CHEST) {
            if (debugging) {
                plugin.getLogger().log(Level.INFO, player.getName() + " attempting to open Ender Chest.");
            }
            if (player.getGameMode() == GameMode.CREATIVE && !player.hasPermission("AngelEnderChest.CreativeOverride")) {
                player.sendMessage("You don't have permission to open EnderChest in creative mode.");
            } else {
                if (debugging) {
                    plugin.getLogger().log(Level.INFO, player.getName() + "isn't creative or has permission.");
                }
                plugin.openAEChestsShelf.openAEChest(player, player.getUniqueId());
            }
            event.setCancelled(true);
        } else {
            if (debugging) {
                plugin.getLogger().log(Level.INFO, "Type: " + event.getInventory().getType().toString());
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        // If owner of the inventory is a player
        if (holder instanceof Player) {
            if (debugging) {
                plugin.getLogger().log(Level.INFO, "Player's inventory being closed.");
            }
            UUID uuid = ((Player) holder).getUniqueId();
            // If the inventory is a registered open AEChest
            if (plugin.openAEChestsShelf.isOpenAEChest(inventory)) {
                List<HumanEntity> viewers = inventory.getViewers();
                while (viewers.contains((HumanEntity) event.getPlayer())) {
                    viewers.remove((HumanEntity) event.getPlayer());
                }
                if (debugging) {
                    plugin.getLogger().log(Level.INFO, "Inventory exists on OpenAEChestShelf.");
                    plugin.getLogger().log(Level.INFO, viewers.toString() + "players still observing.");
                }
                if (viewers.isEmpty()) {
                    plugin.openAEChestsShelf.unregisterOpenAEChest(uuid);
                }
            }
        }
    }
}
