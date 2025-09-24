package com.flowey258.harvestBoost;

import org.bukkit.plugin.java.JavaPlugin;

import com.flowey258.harvestBoost.commands.HarvestBoostCommand;
import com.flowey258.harvestBoost.config.ConfigManager;
import com.flowey258.harvestBoost.listeners.CropGrowthListener;
import com.flowey258.harvestBoost.listeners.PlayerFarmingListener;
import com.flowey258.harvestBoost.managers.BoostManager;
import com.flowey258.harvestBoost.managers.EffectsManager;
import com.flowey258.harvestBoost.managers.PlayerTracker;
import com.flowey258.harvestBoost.tasks.BoostUpdateTask;

public class HarvestBoost extends JavaPlugin {

    private static HarvestBoost instance;

    private ConfigManager configManager;
    private BoostManager boostManager;
    private PlayerTracker playerTracker;
    private EffectsManager effectsManager;

    private BoostUpdateTask boostUpdateTask;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.boostManager = new BoostManager(this);
        this.playerTracker = new PlayerTracker(this);
        this.effectsManager = new EffectsManager(this);

        // Load configuration
        configManager.loadConfig();

        // Register listeners
        getServer().getPluginManager().registerEvents(new CropGrowthListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerFarmingListener(this), this);

        // Register commands
        getCommand("harvestboost").setExecutor(new HarvestBoostCommand(this));

        // Start boost update task
        startBoostUpdateTask();

        getLogger().info("HarvestBoost has been enabled! Players can now farm together for faster growth.");

        // Send enable message to online players
        if (configManager.getConfig().getBoolean("messages.enabled", true)) {
            getServer().getOnlinePlayers().forEach(player ->
                    player.sendMessage(configManager.getMessage("plugin-enabled")));
        }
    }

    @Override
    public void onDisable() {
        // Stop tasks
        if (boostUpdateTask != null) {
            boostUpdateTask.cancel();
        }

        // Clear data
        if (playerTracker != null) {
            playerTracker.clearAll();
        }

        // Send disable message
        if (configManager != null && configManager.getConfig().getBoolean("messages.enabled", true)) {
            getServer().getOnlinePlayers().forEach(player ->
                    player.sendMessage(configManager.getMessage("plugin-disabled")));
        }

        getLogger().info("HarvestBoost has been disabled.");
        instance = null;
    }

    /**
     * Start the main boost update task
     */
    private void startBoostUpdateTask() {
        if (boostUpdateTask != null) {
            boostUpdateTask.cancel();
        }

        int interval = configManager.getConfig().getInt("detection.check-interval", 100);
        boostUpdateTask = new BoostUpdateTask(this);
        boostUpdateTask.runTaskTimer(this, interval, interval);
    }

    /**
     * Reload the plugin configuration and restart tasks
     */
    public void reload() {
        // Reload config
        configManager.loadConfig();

        // Restart task with new interval
        startBoostUpdateTask();

        getLogger().info("HarvestBoost configuration reloaded.");
    }

    // Getters for managers
    public static HarvestBoost getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public BoostManager getBoostManager() {
        return boostManager;
    }

    public PlayerTracker getPlayerTracker() {
        return playerTracker;
    }

    public EffectsManager getEffectsManager() {
        return effectsManager;
    }
}
