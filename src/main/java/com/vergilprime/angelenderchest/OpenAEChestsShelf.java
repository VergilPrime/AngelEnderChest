package com.vergilprime.angelenderchest;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.UUID;

public class OpenAEChestsShelf {
    private final AngelEnderChest plugin;
    private Map<UUID, Inventory> AEChests;

    public OpenAEChestsShelf(AngelEnderChest plugin) {
        this.plugin = plugin;
    }

    public boolean openAEChest(Player player, UUID uuid) {
        if (player.getUniqueId() == uuid || player.hasPermission("AngelEnderChest.admin")) {
            Inventory aechest = AEChests.get(uuid);
            if (aechest == null) {
                aechest = loadAEChest(uuid);
            }
            if (aechest == null) {
                aechest = createAEChest(uuid);
            }
            if (aechest == null) {
                return false;
            }
            player.openInventory(aechest);
        }
    }

    public Inventory loadAEChest(UUID uuid) {
        return registerOpenAEChest(plugin.sqlite.getEnderChest(uuid));
    }

    public Inventory createAEChest(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore()) {
            return null;
        }
        int rows = 3;
        if (offlinePlayer.isOnline()) {
            rows = 6;
            while (!((Player) offlinePlayer).hasPermission("AngelEnderChest.Rows." + rows) && rows > 3) {
                rows--;
            }
        }
        int size = rows * 9;
        String playername = offlinePlayer.getName();
        return Bukkit.createInventory((HumanEntity) offlinePlayer, size, playername + "'s Ender Chest");
    }

    public Inventory registerOpenAEChest(Inventory inventory) {
        if (inventory == null) {
            return null;
        }
        OfflinePlayer inventoryHolder = (OfflinePlayer) inventory.getHolder();
        UUID uuid = inventoryHolder.getUniqueId();
        AEChests.put(uuid, inventory);
        return inventory;
    }

    public void unregisterOpenAEChest(UUID uuid) {
        AEChests.remove(uuid);
    }

    public boolean isOpenAEChest(Inventory inventory) {
        return AEChests.containsValue(inventory);
    }
}
