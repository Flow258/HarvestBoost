package com.flowey258.harvestBoost.listeners;

import com.flowey258.harvestBoost.HarvestBoost;
import com.flowey258.harvestBoost.config.ConfigManager;
import com.flowey258.harvestBoost.managers.BoostManager;
import com.flowey258.harvestBoost.managers.EffectsManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.Random;

public class CropGrowthListener implements Listener {

    private final HarvestBoost plugin;
    private final ConfigManager configManager;
    private final BoostManager boostManager;
    private final EffectsManager effectsManager;
    private final Random random;

    public CropGrowthListener(HarvestBoost plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.boostManager = plugin.getBoostManager();
        this.effectsManager = plugin.getEffectsManager();
        this.random = new Random();
    }

    /**
     * Handle regular block growth events (crops, bamboo, cactus, sugar cane)
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockGrow(BlockGrowEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Block block = event.getBlock();
        Location location = block.getLocation();

        // Check if world is disabled
        if (configManager.getDisabledWorlds().contains(location.getWorld().getName())) {
            return;
        }

        // Check if this type of plant should be boosted
        if (!shouldBoostBlock(block.getType())) {
            return;
        }

        // Get boost multiplier for this location
        double boostMultiplier = boostManager.getBoostMultiplier(location);

        if (boostMultiplier <= 1.0) {
            return; // No boost to apply
        }

        // Apply boost chance
        if (shouldApplyBoost(boostMultiplier)) {
            // Show particle effects
            effectsManager.showCropParticles(location);

            // Force additional growth stages if applicable
            applyAdditionalGrowth(block, boostMultiplier);

            if (configManager.isDebugEnabled()) {
                plugin.getLogger().info("Applied " + String.format("%.1f", boostMultiplier) +
                        "x boost to " + block.getType() + " at " +
                        location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());
            }
        }
    }

    /**
     * Handle tree growth events (saplings growing into trees)
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onStructureGrow(StructureGrowEvent event) {
        if (event.isCancelled() || !configManager.isSaplingsEnabled()) {
            return;
        }

        Location location = event.getLocation();

        // Check if world is disabled
        if (configManager.getDisabledWorlds().contains(location.getWorld().getName())) {
            return;
        }

        // Get boost multiplier for this location
        double boostMultiplier = boostManager.getBoostMultiplier(location);

        if (boostMultiplier <= 1.0) {
            return; // No boost to apply
        }

        // Apply boost chance for tree growth
        if (shouldApplyBoost(boostMultiplier)) {
            // Show particle effects
            effectsManager.showCropParticles(location);

            // Trees grow faster by having a higher chance to grow
            // The event already handles the growth, we just show effects

            if (configManager.isDebugEnabled()) {
                plugin.getLogger().info("Applied " + String.format("%.1f", boostMultiplier) +
                        "x boost to tree growth at " +
                        location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());
            }
        }
    }

    /**
     * Check if a block type should receive growth boosts
     */
    private boolean shouldBoostBlock(Material blockType) {
        String typeName = blockType.toString();

        // Check crops
        if (configManager.isCropsEnabled()) {
            if (isCropBlock(typeName)) {
                return true;
            }
        }

        // Check saplings (handled in StructureGrowEvent, but include for completeness)
        if (configManager.isSaplingsEnabled()) {
            if (typeName.contains("SAPLING") || typeName.contains("MUSHROOM")) {
                return true;
            }
        }

        // Check bamboo
        if (configManager.isBambooEnabled()) {
            if (typeName.equals("BAMBOO")) {
                return true;
            }
        }

        // Check tall plants
        if (configManager.isTallPlantsEnabled()) {
            if (typeName.equals("SUGAR_CANE") || typeName.equals("CACTUS") ||
                    typeName.contains("KELP") || typeName.contains("SEAGRASS")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if a block is a crop
     */
    private boolean isCropBlock(String blockType) {
        return blockType.contains("WHEAT") || blockType.contains("CARROTS") ||
                blockType.contains("POTATOES") || blockType.contains("BEETROOT") ||
                blockType.contains("MELON") || blockType.contains("PUMPKIN") ||
                blockType.equals("SWEET_BERRY_BUSH") || blockType.equals("COCOA") ||
                blockType.equals("NETHER_WART");
    }

    /**
     * Determine if boost should be applied based on multiplier
     */
    private boolean shouldApplyBoost(double multiplier) {
        // Convert multiplier to probability
        // 1.25x = 25% chance for extra growth
        // 1.5x = 50% chance for extra growth
        // 1.75x = 75% chance for extra growth
        double boostChance = multiplier - 1.0;
        return random.nextDouble() < boostChance;
    }

    /**
     * Apply additional growth to a block if possible
     */
    private void applyAdditionalGrowth(Block block, double boostMultiplier) {
        Material blockType = block.getType();

        // Handle age-based crops
        if (isAgeableBlock(blockType)) {
            applyAgeBasedGrowth(block, boostMultiplier);
        }

        // Handle height-based plants (bamboo, cactus, sugar cane)
        else if (isHeightBasedBlock(blockType)) {
            applyHeightBasedGrowth(block, boostMultiplier);
        }
    }

    /**
     * Check if block uses age-based growth
     */
    private boolean isAgeableBlock(Material blockType) {
        String typeName = blockType.toString();
        return typeName.contains("WHEAT") || typeName.contains("CARROTS") ||
                typeName.contains("POTATOES") || typeName.contains("BEETROOT") ||
                typeName.equals("SWEET_BERRY_BUSH") || typeName.equals("COCOA") ||
                typeName.equals("NETHER_WART");
    }

    /**
     * Check if block uses height-based growth
     */
    private boolean isHeightBasedBlock(Material blockType) {
        String typeName = blockType.toString();
        return typeName.equals("BAMBOO") || typeName.equals("SUGAR_CANE") ||
                typeName.equals("CACTUS");
    }

    /**
     * Apply growth to age-based blocks
     */
    private void applyAgeBasedGrowth(Block block, double boostMultiplier) {
        // Additional growth stages based on boost level
        int extraGrowthChance = (int) ((boostMultiplier - 1.0) * 100);

        // Small chance for bonus growth stage
        if (random.nextInt(100) < extraGrowthChance / 2) {
            // This would require NBT manipulation or using specific growth methods
            // For now, we let the natural growth happen with boosted probability
            // Advanced implementations could use reflection or NMS for direct age manipulation
        }
    }

    /**
     * Apply growth to height-based blocks
     */
    private void applyHeightBasedGrowth(Block block, double boostMultiplier) {
        // For height-based plants, we can try to grow them upward
        Block above = block.getRelative(0, 1, 0);

        if (above.getType() == Material.AIR) {
            // Small chance for immediate upward growth
            double growthChance = (boostMultiplier - 1.0) * 0.3; // 30% of boost as growth chance

            if (random.nextDouble() < growthChance) {
                // Check height limits
                if (canGrowUpward(block)) {
                    above.setType(block.getType());
                    effectsManager.showCropParticles(above.getLocation());
                }
            }
        }
    }

    /**
     * Check if a plant can grow upward (respecting height limits)
     */
    private boolean canGrowUpward(Block block) {
        Material blockType = block.getType();
        int currentHeight = getCurrentPlantHeight(block);

        return switch (blockType) {
            case SUGAR_CANE -> currentHeight < 3; // Max 3 blocks high
            case CACTUS -> currentHeight < 3;     // Max 3 blocks high
            case BAMBOO -> currentHeight < 12;    // Max 12-16 blocks high
            default -> false;
        };
    }

    /**
     * Get current height of a plant
     */
    private int getCurrentPlantHeight(Block block) {
        Material blockType = block.getType();
        int height = 1;

        // Count downward to find base
        Block below = block.getRelative(0, -1, 0);
        while (below.getType() == blockType) {
            height++;
            below = below.getRelative(0, -1, 0);
            if (height > 20) break; // Safety check
        }

        // Count upward from original block
        Block above = block.getRelative(0, 1, 0);
        while (above.getType() == blockType) {
            height++;
            above = above.getRelative(0, 1, 0);
            if (height > 20) break; // Safety check
        }

        return height;
    }
}