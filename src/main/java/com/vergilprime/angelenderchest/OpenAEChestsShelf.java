package com.vergilprime.angelenderchest;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class OpenAEChestsShelf {
    private final AngelEnderChest plugin;
    private HashMap<UUID, Inventory> aEChests;
    private Boolean debugging;

    public OpenAEChestsShelf(AngelEnderChest plugin) {
        this.plugin = plugin;
        aEChests = new HashMap<>();
        debugging = plugin.getConfig().getBoolean("debugging");
    }


    public boolean openAEChest(Player player, UUID uuid) {
        if (debugging) {
            if (player.getUniqueId() == uuid) {
                plugin.getLogger().log(Level.INFO, player.getName() + " is opening their own AEChest.");
            } else if (player.hasPermission("AngelEnderChest.admin")) {
                plugin.getLogger().log(Level.INFO, player.getName() + " is opening someone else's AEChest.");
            } else {
                plugin.getLogger().log(Level.INFO, player.getName() + " can't open someone else's AEChest.");
            }
        }
        if (player.getUniqueId() == uuid || player.hasPermission("AngelEnderChest.admin")) {
            if (debugging) {
                plugin.getLogger().log(Level.INFO, "Grabbing AEChest from memory.");
            }
            Inventory aechest = aEChests.get(uuid);
            if (aechest == null) {
                if (debugging) {
                    plugin.getLogger().log(Level.INFO, "Grabbing AEChest from database.");
                }
                aechest = loadAEChest(uuid);
            }
            if (aechest == null) {
                if (debugging) {
                    plugin.getLogger().log(Level.INFO, "Creating AEChest from scratch.");
                }
                aechest = createAEChest(uuid);
            }
            if (aechest == null) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create AEChest.");
                return false;
            }
            player.openInventory(aechest);
        } else {
            player.sendMessage("You don't have permission to open another player's Ender Chest.");
            return false;
        }
        return true;
    }

    public Inventory loadAEChest(UUID uuid) {
        Inventory inventory = plugin.sqlite.getEnderChest(uuid);
        registerOpenAEChest(inventory);
        return (inventory);
    }

    public Inventory createAEChest(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore()) {
            return null;
        }
        int rows = 3;
        if (offlinePlayer.isOnline()) {
            rows = 6;
            while (rows > 3 && !((Player) offlinePlayer).hasPermission("AngelEnderChest.Rows." + rows)) {
                rows--;
            }
        }
        int size = rows * 9;
        String playername = offlinePlayer.getName();
        if (debugging) {
            plugin.getLogger().log(Level.INFO, "Creating AEChest for " + playername + ".");
        }
        Inventory inventory = Bukkit.createInventory((HumanEntity) offlinePlayer, size, playername + "'s Ender Chest");
        registerOpenAEChest(inventory);
        return inventory;
    }

    public void registerOpenAEChest(Inventory inventory) {
        if (inventory != null) {
            OfflinePlayer inventoryHolder = (OfflinePlayer) inventory.getHolder();
            if (inventoryHolder == null) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create AEChest.");
            }
            UUID uuid = inventoryHolder.getUniqueId();
            aEChests.put(uuid, inventory);
        }
    }

    public void unregisterOpenAEChest(UUID uuid) {
        if (debugging) {
            plugin.getLogger().log(Level.INFO, "Saving inventory.");
        }
        Inventory inventory = aEChests.get(uuid);
        plugin.sqlite.saveEnderChest(uuid, inventory);
        aEChests.remove(uuid);
    }

    public boolean isOpenAEChest(Inventory inventory) {
        return aEChests.containsValue(inventory);
    }
}
