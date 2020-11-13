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
        config.addDefault("database", "storage");
        config.options().copyDefaults(true);
        config.addDefault("tablename", "angelenderchest");
        config.options().copyDefaults(true);
        config.addDefault("username", "username");
        config.options().copyDefaults(true);
        config.addDefault("password", "password");
        config.options().copyDefaults(true);
        saveConfig();
        openAEChestsShelf = new OpenAEChestsShelf(this);
        getServer().getPluginManager().registerEvents(new EnderChestListener(this), this);
        getCommand("Chest").setExecutor(new com.vergilprime.angelenderchest.ChestCommand());
        sqlite = new SQLite(this);
        sqlite.load();
    }
}
