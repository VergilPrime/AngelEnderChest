package com.vergilprime.angelenderchest;

import com.vergilprime.angelenderchest.sqlite.SQLite;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

public class EnderChestListener implements Listener {
    AngelEnderChest plugin;
    SQLite sqlite;

    public EnderChestListener(AngelEnderChest plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        sqlite = plugin.sqlite;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType().toString() == "ENDER_CHEST") {
            Player player = (Player) event.getPlayer();
            if (player.getGameMode() == GameMode.CREATIVE && !player.hasPermission("AngelCreative.InvOverride")) {
                player.sendMessage("You don't have permission to open EnderChest in creative mode.");
            } else {
                Inventory aEChest = sqlite.getEnderChest(player);
                player.openInventory(aEChest);
            }
            event.setCancelled(true);
        }
    }

    public void onInventoryClose(InventoryCloseEvent event) {
        //TODO: How do I know this is an enderchest?
        Player player = (Player) event.getPlayer();
        sqlite.saveEnderChest(player, event.getInventory());
    }
}
