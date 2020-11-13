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
        config.addDefault("database", "storage.db");
        config.options().copyDefaults(true);
        saveConfig();
        getCommand("Chest").setExecutor(new com.vergilprime.angelenderchest.ChestCommand());
        sqlite = new SQLite(this);
        sqlite.initialize();
        new EnderChestListener(this);
    }
}
