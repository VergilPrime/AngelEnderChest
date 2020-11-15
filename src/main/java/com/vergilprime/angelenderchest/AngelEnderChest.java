package com.vergilprime.angelenderchest;

import com.vergilprime.angelenderchest.sqlite.SQLite;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class AngelEnderChest extends JavaPlugin {
    public SQLite sqlite;
    public OpenAEChestsShelf openAEChestsShelf;
    public FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        config.addDefault("debugging", false);
        config.addDefault("database", "storage");
        config.addDefault("tablename", "angelenderchest");
        config.addDefault("username", "username");
        config.addDefault("password", "password");
        config.options().copyDefaults(true);
        saveConfig();
        openAEChestsShelf = new OpenAEChestsShelf(this);
        getServer().getPluginManager().registerEvents(new EnderChestListener(this), this);
        getCommand("Chest").setExecutor(new com.vergilprime.angelenderchest.ChestCommand());
        sqlite = new SQLite(this);
        sqlite.load();
    }

    @Override
    public void onDisable() {
        //TODO: Save all open chests        
    }
}
