package com.flowey258.harvestBoost.config;

import com.flowey258.harvestBoost.HarvestBoost;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final HarvestBoost plugin;
    private FileConfiguration config;
    private Map<Integer, Double> boostMultipliers;

    public ConfigManager(HarvestBoost plugin) {
        this.plugin = plugin;
        this.boostMultipliers = new HashMap<>();
    }

    /**
     * Load and validate configuration
     */
    public void loadConfig() {
        // Save default config if it doesn't exist
        plugin.saveDefaultConfig();

        // Reload config from file
        plugin.reloadConfig();
        config = plugin.getConfig();

        // Load boost multipliers
        loadBoostMultipliers();

        // Validate configuration
        validateConfig();
    }

    /**
     * Load boost multipliers from config
     */
    private void loadBoostMultipliers() {
        boostMultipliers.clear();

        for (int i = 1; i <= getMaxPlayers(); i++) {
            double multiplier = config.getDouble("boosts." + i, 1.0);
            boostMultipliers.put(i, multiplier);
        }

        plugin.getLogger().info("Loaded " + boostMultipliers.size() + " boost levels");
    }

    /**
     * Validate configuration values
     */
    private void validateConfig() {
        // Validate radius
        if (getDetectionRadius() <= 0) {
            plugin.getLogger().warning("Detection radius must be positive! Using default value of 10.");
            config.set("detection.radius", 10);
        }

        // Validate check interval
        if (getCheckInterval() <= 0) {
            plugin.getLogger().warning("Check interval must be positive! Using default value of 100.");
            config.set("detection.check-interval", 100);
        }

        // Validate max players
        if (getMaxPlayers() <= 0) {
            plugin.getLogger().warning("Max players must be positive! Using default value of 4.");
            config.set("boosts.max-players", 4);
        }

        // Save config if changes were made
        plugin.saveConfig();
    }

    /**
     * Get formatted message with color codes
     */
    public String getMessage(String key) {
        String prefix = config.getString("messages.prefix", "&a[HarvestBoost] ");
        String message = config.getString("messages." + key, "Message not found: " + key);
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }

    /**
     * Get formatted message without prefix
     */
    public String getMessageNoPrefix(String key) {
        String message = config.getString("messages." + key, "Message not found: " + key);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Get boost multiplier for player count
     */
    public double getBoostMultiplier(int playerCount) {
        // Cap at max players
        int cappedCount = Math.min(playerCount, getMaxPlayers());
        return boostMultipliers.getOrDefault(cappedCount, 1.0);
    }

    /**
     * Get boost percentage for display (e.g., 25 for 1.25x multiplier)
     */
    public int getBoostPercentage(int playerCount) {
        double multiplier = getBoostMultiplier(playerCount);
        return (int) Math.round((multiplier - 1.0) * 100);
    }

    // Configuration getters
    public int getDetectionRadius() {
        return config.getInt("detection.radius", 10);
    }

    public int getCheckInterval() {
        return config.getInt("detection.check-interval", 100);
    }

    public int getMaxPlayers() {
        return config.getInt("boosts.max-players", 4);
    }

    public int getFarmingDetectionRadius() {
        return config.getInt("advanced.farming-detection-radius", 3);
    }

    public int getMinimumPresenceTime() {
        return config.getInt("advanced.minimum-presence-time", 100);
    }

    public boolean isCropsEnabled() {
        return config.getBoolean("enable.crops", true);
    }

    public boolean isSaplingsEnabled() {
        return config.getBoolean("enable.saplings", true);
    }

    public boolean isBambooEnabled() {
        return config.getBoolean("enable.bamboo", true);
    }

    public boolean isTallPlantsEnabled() {
        return config.getBoolean("enable.tall-plants", true);
    }

    public boolean isParticlesEnabled() {
        return config.getBoolean("effects.particles.enabled", true);
    }

    public String getParticleType() {
        return config.getString("effects.particles.type", "HAPPY_VILLAGER");
    }

    public int getParticleAmount() {
        return config.getInt("effects.particles.amount", 3);
    }

    public int getParticleInterval() {
        return config.getInt("effects.particles.interval", 60);
    }

    public boolean isActionBarEnabled() {
        return config.getBoolean("effects.actionbar.enabled", true);
    }

    public int getActionBarUpdateInterval() {
        return config.getInt("effects.actionbar.update-interval", 40);
    }

    public String getActionBarFormat() {
        return config.getString("effects.actionbar.format", "🌱 Growth boosted by %boost%% (%players% farmers nearby!)");
    }

    public boolean isSoundsEnabled() {
        return config.getBoolean("effects.sounds.enabled", true);
    }

    public String getEnterBoostSound() {
        return config.getString("effects.sounds.enter-boost-area", "ENTITY_EXPERIENCE_ORB_PICKUP");
    }

    public String getBoostChangeSound() {
        return config.getString("effects.sounds.boost-change", "BLOCK_NOTE_BLOCK_CHIME");
    }

    public float getSoundVolume() {
        return (float) config.getDouble("effects.sounds.volume", 0.5);
    }

    public float getSoundPitch() {
        return (float) config.getDouble("effects.sounds.pitch", 1.2);
    }

    public boolean isDebugEnabled() {
        return config.getBoolean("advanced.debug", false);
    }

    public List<String> getDisabledWorlds() {
        return config.getStringList("advanced.disabled-worlds");
    }

    public boolean isXpBonusEnabled() {
        return config.getBoolean("advanced.xp-bonus.enabled", true);
    }

    public int getXpBonusPerLevel() {
        return config.getInt("advanced.xp-bonus.per-level", 1);
    }

    public int getMaxCropsPerTick() {
        return config.getInt("performance.max-crops-per-tick", 50);
    }

    public int getLocationCacheTime() {
        return config.getInt("performance.location-cache-time", 20);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}