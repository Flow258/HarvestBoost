package com.flowey258.harvestBoost.tasks;

import com.flowey258.harvestBoost.HarvestBoost;
import com.flowey258.harvestBoost.config.ConfigManager;
import com.flowey258.harvestBoost.managers.BoostManager;
import com.flowey258.harvestBoost.managers.EffectsManager;
import com.flowey258.harvestBoost.managers.PlayerTracker;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BoostUpdateTask extends BukkitRunnable {

    private final HarvestBoost plugin;
    private final ConfigManager configManager;
    private final BoostManager boostManager;
    private final PlayerTracker playerTracker;
    private final EffectsManager effectsManager;

    private int tickCounter = 0;

    public BoostUpdateTask(HarvestBoost plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.boostManager = plugin.getBoostManager();
        this.playerTracker = plugin.getPlayerTracker();
        this.effectsManager = plugin.getEffectsManager();
    }

    @Override
    public void run() {
        try {
            // Increment tick counter
            tickCounter++;

            // Update player tracking and effects
            updatePlayerTracking();

            // Clean up caches periodically (every 5 minutes)
            if (tickCounter % 6000 == 0) {
                performCleanup();
            }

            // Update actionbars for players in boosted areas
            updateActionBars();

            // Show particle effects periodically
            if (tickCounter % (configManager.getParticleInterval() / 5) == 0) {
                showParticleEffects();
            }

        } catch (Exception e) {
            plugin.getLogger().warning("Error in BoostUpdateTask: " + e.getMessage());
            if (configManager.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Update player tracking and presence
     */
    private void updatePlayerTracking() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.isOnline()) {
                // Update player presence if they're near farming areas
                updatePlayerFarmingPresence(player);

                // Handle boost changes and sound effects
                effectsManager.handleBoostChange(player);
            }
        }

        // Clean up offline players every 20 ticks (1 second)
        if (tickCounter % 20 == 0) {
            playerTracker.cleanupOfflinePlayers();
            effectsManager.cleanupOfflinePlayers();
        }
    }

    /**
     * Update player farming presence
     */
    private void updatePlayerFarmingPresence(Player player) {
        // Check if player is in a disabled world
        if (configManager.getDisabledWorlds().contains(player.getWorld().getName())) {
            return;
        }

        // Check if player is near any farmable blocks
        if (isPlayerNearFarmableBlocks(player)) {
            playerTracker.updatePlayerPresence(player, player.getLocation());
        }
    }

    /**
     * Check if player is near farmable blocks
     */
    private boolean isPlayerNearFarmableBlocks(Player player) {
        var playerLoc = player.getLocation();
        int farmRadius = configManager.getFarmingDetectionRadius();

        // Check blocks around player
        for (int x = -farmRadius; x <= farmRadius; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -farmRadius; z <= farmRadius; z++) {
                    var checkLoc = playerLoc.clone().add(x, y, z);
                    if (isFarmableBlock(checkLoc)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Check if a location has farmable blocks
     */
    private boolean isFarmableBlock(org.bukkit.Location location) {
        if (location.getWorld() == null) return false;

        String blockType = location.getBlock().getType().toString();

        // Check crops
        if (configManager.isCropsEnabled()) {
            if (blockType.contains("WHEAT") || blockType.contains("CARROTS") ||
                    blockType.contains("POTATOES") || blockType.contains("BEETROOT") ||
                    blockType.contains("MELON") || blockType.contains("PUMPKIN") ||
                    blockType.equals("FARMLAND") || blockType.equals("SWEET_BERRY_BUSH") ||
                    blockType.equals("COCOA") || blockType.equals("NETHER_WART")) {
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
            if (blockType.equals("BAMBOO")) {
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
     * Update actionbars for players in boosted areas
     */
    private void updateActionBars() {
        if (!configManager.isActionBarEnabled()) {
            return;
        }

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.isOnline() && playerTracker.isPlayerInBoostedArea(player)) {
                effectsManager.updateActionBar(player);
            }
        }
    }

    /**
     * Show particle effects on crops in boosted areas
     */
    private void showParticleEffects() {
        if (!configManager.isParticlesEnabled()) {
            return;
        }

        // This is simplified - in a full implementation, you might want to track
        // all active farming areas and show particles on crops in those areas
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.isOnline() && playerTracker.isPlayerInBoostedArea(player)) {
                // Show particles around the player's farming area
                showNearbyFarmParticles(player);
            }
        }
    }

    /**
     * Show particles on farmable blocks near a player
     */
    private void showNearbyFarmParticles(Player player) {
        var playerLoc = player.getLocation();
        int radius = Math.min(configManager.getDetectionRadius(), 5); // Limit for performance

        int particlesShown = 0;
        int maxParticles = 10; // Limit particles per player per tick

        for (int x = -radius; x <= radius && particlesShown < maxParticles; x++) {
            for (int z = -radius; z <= radius && particlesShown < maxParticles; z++) {
                for (int y = -2; y <= 2 && particlesShown < maxParticles; y++) {
                    var checkLoc = playerLoc.clone().add(x, y, z);

                    if (isFarmableBlock(checkLoc) && Math.random() < 0.1) { // 10% chance
                        effectsManager.showCropParticles(checkLoc);
                        particlesShown++;
                    }
                }
            }
        }
    }

    /**
     * Perform periodic cleanup tasks
     */
    private void performCleanup() {
        if (configManager.isDebugEnabled()) {
            plugin.getLogger().info("Performing periodic cleanup...");
        }

        // Clean boost cache
        boostManager.cleanCache();

        // Clean player tracking data
        playerTracker.cleanupOfflinePlayers();

        // Clean effects data
        effectsManager.cleanupOfflinePlayers();

        // Force garbage collection suggestion (not guaranteed)
        System.gc();

        if (configManager.isDebugEnabled()) {
            plugin.getLogger().info("Cleanup completed. Online players: " +
                    plugin.getServer().getOnlinePlayers().size());
        }
    }

    /**
     * Get current tick count
     */
    public int getTickCounter() {
        return tickCounter;
    }

    /**
     * Reset tick counter
     */
    public void resetTickCounter() {
        tickCounter = 0;
    }
}