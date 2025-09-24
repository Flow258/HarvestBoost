package com.flowey258.harvestBoost.managers;

import com.flowey258.harvestBoost.HarvestBoost;
import com.flowey258.harvestBoost.config.ConfigManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BoostManager {

    private final HarvestBoost plugin;
    private final ConfigManager configManager;

    // Cache for location-based boost levels
    private final Map<String, BoostData> boostCache = new ConcurrentHashMap<>();

    public BoostManager(HarvestBoost plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    /**
     * Get the current boost multiplier at a location
     */
    public double getBoostMultiplier(Location location) {
        String locationKey = getLocationKey(location);
        BoostData boostData = boostCache.get(locationKey);

        if (boostData == null || isBoostDataExpired(boostData)) {
            int playerCount = countNearbyFarmers(location);
            double multiplier = configManager.getBoostMultiplier(playerCount);

            boostCache.put(locationKey, new BoostData(playerCount, multiplier, System.currentTimeMillis()));
            return multiplier;
        }

        return boostData.multiplier();
    }

    /**
     * Get the current boost percentage at a location (for display)
     */
    public int getBoostPercentage(Location location) {
        double multiplier = getBoostMultiplier(location);
        return (int) Math.round((multiplier - 1.0) * 100);
    }

    /**
     * Get the number of farmers contributing to boost at a location
     */
    public int getFarmerCount(Location location) {
        String locationKey = getLocationKey(location);
        BoostData boostData = boostCache.get(locationKey);

        if (boostData == null || isBoostDataExpired(boostData)) {
            return countNearbyFarmers(location);
        }

        return boostData.playerCount();
    }

    /**
     * Check if a location has any boost active
     */
    public boolean hasBoost(Location location) {
        return getBoostMultiplier(location) > 1.0;
    }

    /**
     * Count nearby farming players within boost radius
     */
    private int countNearbyFarmers(Location location) {
        if (location.getWorld() == null) return 0;

        // Check if world is disabled
        if (configManager.getDisabledWorlds().contains(location.getWorld().getName())) {
            return 0;
        }

        int count = 0;
        int radius = configManager.getDetectionRadius();

        for (Player player : location.getWorld().getPlayers()) {
            if (isPlayerFarming(player, location, radius)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Check if a player is actively farming near the location
     */
    private boolean isPlayerFarming(Player player, Location cropLocation, int radius) {
        if (player == null || !player.isOnline()) return false;

        Location playerLoc = player.getLocation();

        // Check if player is within the boost radius
        if (playerLoc.distance(cropLocation) > radius) return false;

        // Check if player has been in the area long enough
        PlayerTracker tracker = plugin.getPlayerTracker();
        if (!tracker.hasBeenInAreaLongEnough(player, cropLocation)) {
            return false;
        }

        // Check if player is actually near farmable blocks
        return isNearFarmableBlocks(player);
    }

    /**
     * Check if player is near farmable blocks (crops, saplings, etc.)
     */
    private boolean isNearFarmableBlocks(Player player) {
        Location playerLoc = player.getLocation();
        int farmRadius = configManager.getFarmingDetectionRadius();

        // Check blocks around player for farmable materials
        for (int x = -farmRadius; x <= farmRadius; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -farmRadius; z <= farmRadius; z++) {
                    Location checkLoc = playerLoc.clone().add(x, y, z);
                    if (isFarmableBlock(checkLoc)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Check if a block is farmable based on configuration
     */
    private boolean isFarmableBlock(Location location) {
        if (location.getWorld() == null) return false;

        String blockType = location.getBlock().getType().toString();

        // Check crops
        if (configManager.isCropsEnabled()) {
            if (blockType.contains("WHEAT") || blockType.contains("CARROTS") ||
                    blockType.contains("POTATOES") || blockType.contains("BEETROOT") ||
                    blockType.contains("MELON") || blockType.contains("PUMPKIN") ||
                    blockType.equals("FARMLAND") || blockType.equals("TILLED_DIRT")) {
                return true;
            }
        }

        // Check saplings
        if (configManager.isSaplingsEnabled()) {
            if (blockType.contains("SAPLING") || blockType.contains("MUSHROOM")) {
                return true;
            }
        }

        // Check bamboo
        if (configManager.isBambooEnabled()) {
            if (blockType.equals("BAMBOO") || blockType.equals("BAMBOO_SAPLING")) {
                return true;
            }
        }

        // Check tall plants
        if (configManager.isTallPlantsEnabled()) {
            if (blockType.equals("SUGAR_CANE") || blockType.equals("CACTUS") ||
                    blockType.contains("KELP") || blockType.contains("SEAGRASS")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Update boost cache for a location
     */
    public void updateBoostCache(Location location) {
        String locationKey = getLocationKey(location);
        int playerCount = countNearbyFarmers(location);
        double multiplier = configManager.getBoostMultiplier(playerCount);

        boostCache.put(locationKey, new BoostData(playerCount, multiplier, System.currentTimeMillis()));
    }

    /**
     * Clear expired cache entries
     */
    public void cleanCache() {
        long currentTime = System.currentTimeMillis();
        long cacheTime = configManager.getLocationCacheTime() * 50; // Convert ticks to milliseconds

        boostCache.entrySet().removeIf(entry ->
                currentTime - entry.getValue().timestamp() > cacheTime);
    }

    /**
     * Clear all cached data
     */
    public void clearCache() {
        boostCache.clear();
    }

    /**
     * Generate a cache key for a location
     */
    private String getLocationKey(Location location) {
        return location.getWorld().getName() + ":" +
                location.getBlockX() + ":" +
                location.getBlockY() + ":" +
                location.getBlockZ();
    }

    /**
     * Check if boost data is expired
     */
    private boolean isBoostDataExpired(BoostData boostData) {
        long cacheTime = configManager.getLocationCacheTime() * 50; // Convert ticks to milliseconds
        return System.currentTimeMillis() - boostData.timestamp() > cacheTime;
    }

    /**
     * Data record for caching boost information
     */
    private record BoostData(int playerCount, double multiplier, long timestamp) {}
}