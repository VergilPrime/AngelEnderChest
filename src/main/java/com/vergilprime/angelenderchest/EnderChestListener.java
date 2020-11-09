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
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

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
                plugin.openAEChestsShelf.openAEChest(player, player.getUniqueId());
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        // If owner of the inventory is a player
        if (holder instanceof Player) {
            UUID uuid = ((Player) holder).getUniqueId();
            // If the inventory is a registered open AEChest
            if (plugin.openAEChestsShelf.isOpenAEChest(inventory)) {
                if (event.getViewers().isEmpty()) {
                    plugin.openAEChestsShelf.unregisterOpenAEChest(uuid);
                }
                sqlite.saveEnderChest(uuid, inventory);
            }
        }
    }
}
