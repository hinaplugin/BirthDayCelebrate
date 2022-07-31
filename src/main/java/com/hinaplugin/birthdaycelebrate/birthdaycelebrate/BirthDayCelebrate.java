package com.hinaplugin.birthdaycelebrate.birthdaycelebrate;

import org.bukkit.plugin.java.JavaPlugin;

public final class BirthDayCelebrate extends JavaPlugin {
    public static BirthDayCelebrate plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        getServer().getPluginManager().registerEvents(new LoginListener(plugin), this);
        getCommand("birthday").setExecutor(new Commands(plugin));
        Database database = new Database(this);
        database.CreateDatabase();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Database database = new Database(plugin);
        database.CloseConnection();
    }
}
