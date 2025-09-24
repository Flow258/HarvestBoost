package com.flowey258.harvestBoost.managers;

import com.flowey258.harvestBoost.HarvestBoost;
import com.flowey258.harvestBoost.config.ConfigManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerTracker {

    private final HarvestBoost plugin;
    private final ConfigManager configManager;

    // Track player presence in farming areas
    private final Map<UUID, PlayerPresenceData> playerPresence = new ConcurrentHashMap<>();

    // Track last known farming location for each player
    private final Map<UUID, Location> lastFarmingLocation = new ConcurrentHashMap<>();

    public PlayerTracker(HarvestBoost plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    /**
     * Update player's presence in a farming area
     */
    public void updatePlayerPresence(Player player, Location farmingLocation) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        PlayerPresenceData currentData = playerPresence.get(playerId);
        Location lastLocation = lastFarmingLocation.get(playerId);

        // Check if player moved to a different farming area
        if (lastLocation == null || !isSameFarmingArea(lastLocation, farmingLocation)) {
            // Player entered new farming area
            playerPresence.put(playerId, new PlayerPresenceData(currentTime, farmingLocation));
            lastFarmingLocation.put(playerId, farmingLocation.clone());

            if (configManager.isDebugEnabled()) {
                plugin.getLogger().info("Player " + player.getName() + " entered farming area at " +
                        farmingLocation.getBlockX() + "," + farmingLocation.getBlockY() + "," + farmingLocation.getBlockZ());
            }
        } else if (currentData != null) {
            // Update presence time in same area
            playerPresence.put(playerId, new PlayerPresenceData(currentData.entryTime(), farmingLocation));
        }
    }

    /**
     * Check if player has been in the farming area long enough to contribute to boost
     */
    public boolean hasBeenInAreaLongEnough(Player player, Location farmingLocation) {
        UUID playerId = player.getUniqueId();
        PlayerPresenceData presenceData = playerPresence.get(playerId);

        if (presenceData == null) {
            return false;
        }

        // Check if it's the same farming area
        if (!isSameFarmingArea(presenceData.location(), farmingLocation)) {
            return false;
        }

        // Check minimum presence time
        long currentTime = System.currentTimeMillis();
        long presenceTime = currentTime - presenceData.entryTime();
        long requiredTime = configManager.getMinimumPresenceTime() * 50; // Convert ticks to milliseconds

        return presenceTime >= requiredTime;
    }

    /**
     * Remove player from tracking when they leave
     */
    public void removePlayer(Player player) {
        UUID playerId = player.getUniqueId();
        playerPresence.remove(playerId);
        lastFarmingLocation.remove(playerId);
    }

    /**
     * Get all players currently in a farming area
     */
    public Map<UUID, PlayerPresenceData> getPlayersInArea(Location centerLocation, int radius) {
        Map<UUID, PlayerPresenceData> playersInArea = new ConcurrentHashMap<>();

        for (Map.Entry<UUID, PlayerPresenceData> entry : playerPresence.entrySet()) {
            PlayerPresenceData data = entry.getValue();
            if (data.location().distance(centerLocation) <= radius) {
                playersInArea.put(entry.getKey(), data);
            }
        }

        return playersInArea;
    }

    /**
     * Clean up old presence data for offline players
     */
    public void cleanupOfflinePlayers() {
        playerPresence.entrySet().removeIf(entry -> {
            Player player = plugin.getServer().getPlayer(entry.getKey());
            return player == null || !player.isOnline();
        });

        lastFarmingLocation.entrySet().removeIf(entry -> {
            Player player = plugin.getServer().getPlayer(entry.getKey());
            return player == null || !player.isOnline();
        });
    }

    /**
     * Get player's current boost level based on their location
     */
    public int getPlayerBoostLevel(Player player) {
        Location playerLoc = player.getLocation();
        return plugin.getBoostManager().getFarmerCount(playerLoc);
    }

    /**
     * Check if player is currently in a boosted farming area
     */
    public boolean isPlayerInBoostedArea(Player player) {
        Location playerLoc = player.getLocation();
        return plugin.getBoostManager().hasBoost(playerLoc);
    }

    /**
     * Get the time a player has been in their current farming area (in milliseconds)
     */
    public long getTimeInCurrentArea(Player player) {
        UUID playerId = player.getUniqueId();
        PlayerPresenceData data = playerPresence.get(playerId);

        if (data == null) {
            return 0;
        }

        return System.currentTimeMillis() - data.entryTime();
    }

    /**
     * Check if two locations are in the same farming area
     */
    private boolean isSameFarmingArea(Location loc1, Location loc2) {
        if (!loc1.getWorld().equals(loc2.getWorld())) {
            return false;
        }

        int farmRadius = configManager.getFarmingDetectionRadius();
        return loc1.distance(loc2) <= farmRadius;
    }

    /**
     * Clear all tracking data
     */
    public void clearAll() {
        playerPresence.clear();
        lastFarmingLocation.clear();
    }

    /**
     * Get debug information about a player's farming status
     */
    public String getPlayerDebugInfo(Player player) {
        UUID playerId = player.getUniqueId();
        PlayerPresenceData data = playerPresence.get(playerId);

        if (data == null) {
            return "Not in farming area";
        }

        long timeInArea = getTimeInCurrentArea(player);
        boolean qualifies = hasBeenInAreaLongEnough(player, data.location());
        int boostLevel = getPlayerBoostLevel(player);

        return String.format("Area: %d,%d,%d | Time: %dms | Qualifies: %s | Boost: %dx",
                data.location().getBlockX(),
                data.location().getBlockY(),
                data.location().getBlockZ(),
                timeInArea,
                qualifies,
                boostLevel);
    }

    /**
     * Data record for tracking player presence in farming areas
     */
    public record PlayerPresenceData(long entryTime, Location location) {}
}