package rexfracht868454.spawnelytra;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class SpawnElytra extends JavaPlugin {

    @Override
    public void onEnable() {
        registerListener();
        saveDefaultConfig();

        String world = this.getConfig().getString("world");
        int multiplyValue = this.getConfig().getInt("multiplyValue");
        int useRadius = this.getConfig().getInt("useRadius");
        if (world == null || multiplyValue <= 0 || useRadius <= 0) {
            this.getLogger().warning("Please configure the config.yml correctly!");
        }
    }

    @Override
    public void onDisable() {
    }

    private void registerListener() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new Listener(this), this);
    }

    @NotNull
    @Override
    public FileConfiguration getConfig() {
        return super.getConfig();
    }
}
