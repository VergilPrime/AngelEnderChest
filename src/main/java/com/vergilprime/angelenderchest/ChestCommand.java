package com.vergilprime.angelenderchest;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ChestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            System.out.println("Only players can open inventories...");
            return false;
        }

        Player player = (Player) sender;

        Inventory aechest = Bukkit.createInventory(player, 27, "Angel Ender Chest");

        player.openInventory(aechest);

        return true;
    }
}
