package com.vergilprime.angelenderchest;

import org.bukkit.plugin.java.JavaPlugin;

public final class AngelEnderChest extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("AEChest").setExecutor(new com.vergilprime.angelenderchest.ChestCommand());
    }
}
