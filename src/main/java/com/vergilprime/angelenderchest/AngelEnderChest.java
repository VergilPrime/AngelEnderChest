package com.vergilprime.angelenderchest;

import com.vergilprime.angelenderchest.sqlite.SQLite;
import org.bukkit.plugin.java.JavaPlugin;

public final class AngelEnderChest extends JavaPlugin {
    public SQLite sqlite;

    @Override
    public void onEnable() {
        getCommand("AEChest").setExecutor(new com.vergilprime.angelenderchest.ChestCommand());
        sqlite = new SQLite(this);
        sqlite.initialize();
        new EnderChestListener(this);
    }
}
