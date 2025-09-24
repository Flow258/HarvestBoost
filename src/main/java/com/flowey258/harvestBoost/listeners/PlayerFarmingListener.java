package com.flowey258.harvestBoost.listeners;

import com.flowey258.harvestBoost.HarvestBoost;
import com.flowey258.harvestBoost.config.ConfigManager;
import com.flowey258.harvestBoost.managers.PlayerTracker;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerFarmingListener implements Listener {

    private final HarvestBoost plugin;
    private final ConfigManager configManager;
    private final PlayerTracker playerTracker;

    public PlayerFarmingListener(HarvestBoost plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.playerTracker = plugin.getPlayerTracker();
    }

    /**
     * Track when players plant crops
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        Material blockType = event.getBlock().getType();

        // Check if it's a farmable block
        if (isFarmingBlock(blockType)) {
            updatePlayerFarmingActivity(player, event.getBlock().getLocation());
        }
    }

    /**
     * Track when players harvest crops
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        Material blockType = event.getBlock().getType();

        // Check if it's a farmable block
        if (isFarmingBlock(blockType)) {
            updatePlayerFarmingActivity(player, event.getBlock().getLocation());

            // Give XP bonus if enabled
            if (configManager.isXpBonusEnabled()) {
                giveXpBonus(player, event.getBlock().getLocation());
            }
        }
    }

    /**
     * Track modern harvest events (right-click harvesting)
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerHarvest(PlayerHarvestBlockEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        updatePlayerFarmingActivity(player, event.getHarvestedBlock().getLocation());

        // Give XP bonus if enabled
        if (configManager.isXpBonusEnabled()) {
            giveXpBonus(player, event.getHarvestedBlock().getLocation());
        }
    }

    /**
     * Track when players interact with farmland or farming tools
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }

        Player player = event.getPlayer();
        Material blockType = event.getClickedBlock().getType();
        ItemStack item = event.getItem();

        // Check for farmland interaction (tilling, planting)
        if (blockType == Material.FARMLAND || blockType == Material.DIRT || blockType == Material.GRASS_BLOCK) {
            if (item != null && isFarmingTool(item.getType())) {
                updatePlayerFarmingActivity(player, event.getClickedBlock().getLocation());
            }
        }

        // Check for bone meal usage
        if (item != null && item.getType() == Material.BONE_MEAL) {
            if (isFarmingBlock(blockType)) {
                updatePlayerFarmingActivity(player, event.getClickedBlock().getLocation());
            }
        }

        // Check for composter interaction
        if (blockType == Material.COMPOSTER) {
            updatePlayerFarmingActivity(player, event.getClickedBlock().getLocation());
        }
    }

    /**
     * Track when players pick up farming-related items
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        Material itemType = event.getItem().getItemStack().getType();

        // Check if it's a farming-related item
        if (isFarmingItem(itemType)) {
            updatePlayerFarmingActivity(player, event.getItem().getLocation());
        }
    }

    /**
     * Clean up player data when they leave
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerTracker.removePlayer(event.getPlayer());
        plugin.getEffectsManager().removePlayer(event.getPlayer());
    }

    /**
     * Welcome message when players join
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Send welcome message after a short delay
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Player player = event.getPlayer();
            if (player.isOnline() && !player.hasPlayedBefore()) {
                player.sendMessage(configManager.getMessage("plugin-enabled"));
                player.sendMessage("§7Tip: Farm together with other players for faster crop growth!");
            }
        }, 40L); // 2 seconds delay
    }

    /**
     * Update player's farming activity tracking
     */
    private void updatePlayerFarmingActivity(Player player, Location farmingLocation) {
        // Check if world is disabled
        if (configManager.getDisabledWorlds().contains(farmingLocation.getWorld().getName())) {
            return;
        }

        // Update player presence
        playerTracker.updatePlayerPresence(player, farmingLocation);

        // Show effects if player is in a boosted area
        if (plugin.getBoostManager().hasBoost(farmingLocation)) {
            plugin.getEffectsManager().showAllEffects(player);
        }
    }

    /**
     * Give XP bonus to player based on boost level
     */
    private void giveXpBonus(Player player, Location location) {
        int farmerCount = plugin.getBoostManager().getFarmerCount(location);

        if (farmerCount > 1) {
            int bonusXp = (farmerCount - 1) * configManager.getXpBonusPerLevel();
            player.giveExp(bonusXp);

            if (configManager.isDebugEnabled()) {
                plugin.getLogger().info("Gave " + bonusXp + " bonus XP to " + player.getName() +
                        " for cooperative farming (" + farmerCount + " farmers)");
            }
        }
    }

    /**
     * Check if a block is farming-related
     */
    private boolean isFarmingBlock(Material blockType) {
        String typeName = blockType.toString();

        // Crops
        if (typeName.contains("WHEAT") || typeName.contains("CARROTS") ||
                typeName.contains("POTATOES") || typeName.contains("BEETROOT") ||
                typeName.contains("MELON") || typeName.contains("PUMPKIN") ||
                typeName.equals("SWEET_BERRY_BUSH") || typeName.equals("COCOA") ||
                typeName.equals("NETHER_WART")) {
            return true;
        }

        // Saplings and mushrooms
        if (typeName.contains("SAPLING") || typeName.contains("MUSHROOM")) {
            return true;
        }

        // Tall plants
        if (typeName.equals("SUGAR_CANE") || typeName.equals("CACTUS") ||
                typeName.equals("BAMBOO") || typeName.contains("KELP")) {
            return true;
        }

        // Farmland and related
        if (typeName.equals("FARMLAND") || typeName.equals("COMPOSTER")) {
            return true;
        }

        return false;
    }

    /**
     * Check if an item is farming-related
     */
    private boolean isFarmingItem(Material itemType) {
        String typeName = itemType.toString();

        // Seeds and crops
        if (typeName.contains("SEEDS") || typeName.contains("WHEAT") ||
                typeName.contains("CARROT") || typeName.contains("POTATO") ||
                typeName.contains("BEETROOT") || typeName.equals("MELON_SLICE") ||
                typeName.contains("PUMPKIN") || typeName.equals("SWEET_BERRIES") ||
                typeName.equals("COCOA_BEANS") || typeName.equals("NETHER_WART")) {
            return true;
        }

        // Saplings and mushrooms
        if (typeName.contains("SAPLING") || typeName.contains("MUSHROOM")) {
            return true;
        }

        // Other farming items
        if (typeName.equals("SUGAR_CANE") || typeName.equals("CACTUS") ||
                typeName.equals("BAMBOO") || typeName.equals("KELP")) {
            return true;
        }

        return false;
    }

    /**
     * Check if an item is a farming tool
     */
    private boolean isFarmingTool(Material toolType) {
        String typeName = toolType.toString();

        return typeName.contains("HOE") ||
                typeName.equals("BONE_MEAL") ||
                typeName.contains("SEEDS") ||
                typeName.equals("WATER_BUCKET") ||
                typeName.equals("SHEARS");
    }
}