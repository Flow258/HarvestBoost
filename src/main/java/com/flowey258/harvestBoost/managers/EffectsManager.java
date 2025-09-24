package com.flowey258.harvestBoost.managers;

import com.flowey258.harvestBoost.HarvestBoost;
import com.flowey258.harvestBoost.config.ConfigManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EffectsManager {

    private final HarvestBoost plugin;
    private final ConfigManager configManager;

    // The hero's log: tracking the last communal strength level.
    private final Map<UUID, Integer> lastBoostLevel = new ConcurrentHashMap<>();

    // The spirit's memory: tracking the last time a message was whispered to the player.
    private final Map<UUID, Long> lastActionBarUpdate = new ConcurrentHashMap<>();

    public EffectsManager(HarvestBoost plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    /**
     * Summon magical growth particles at the blessed crop.
     */
    public void showCropParticles(Location location) {
        if (!configManager.isParticlesEnabled() || location.getWorld() == null) {
            return;
        }

        try {
            String particleTypeName = configManager.getParticleType();
            Particle particleType = Particle.valueOf(particleTypeName);
            int amount = configManager.getParticleAmount();

            // Conjure particles of life force slightly above the crop.
            Location particleLoc = location.clone().add(0.5, 0.5, 0.5);
            location.getWorld().spawnParticle(particleType, particleLoc, amount, 0.3, 0.2, 0.3, 0);

        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid arcane particle type: " + configManager.getParticleType());
        }
    }

    /**
     * Whisper the tale of communal strength onto the hero's action bar.
     */
    public void updateActionBar(Player player) {
        if (!configManager.isActionBarEnabled() || !shouldUpdateActionBar(player)) {
            return;
        }

        int heroCount = plugin.getBoostManager().getFarmerCount(player.getLocation());

        if (heroCount <= 1) {
            // The hero stands alone; the communal spirit is dormant.
            return;
        }

        int boostPercentage = plugin.getBoostManager().getBoostPercentage(player.getLocation());

        // Forge the epic message.
        String message = configManager.getActionBarFormat()
                .replace("%boost%", String.valueOf(boostPercentage))
                .replace("%players%", String.valueOf(heroCount));

        player.sendMessage(message);
        lastActionBarUpdate.put(player.getUniqueId(), System.currentTimeMillis());
    }

    /**
     * Check if the action bar lore should be updated for the hero.
     */
    private boolean shouldUpdateActionBar(Player player) {
        UUID playerId = player.getUniqueId();
        Long lastUpdate = lastActionBarUpdate.get(playerId);

        if (lastUpdate == null) {
            return true;
        }

        long timeSinceUpdate = System.currentTimeMillis() - lastUpdate;
        long updateInterval = configManager.getActionBarUpdateInterval() * 50; // Convert ticks to milliseconds

        // Has enough time passed since the last whisper?
        return timeSinceUpdate >= updateInterval;
    }

    /**
     * Chronicle the hero's change in communal strength with a resonant sound.
     */
    public void handleBoostChange(Player player) {
        if (!configManager.isSoundsEnabled()) {
            return;
        }

        UUID playerId = player.getUniqueId();
        int currentBoostLevel = plugin.getBoostManager().getFarmerCount(player.getLocation());
        Integer previousLevel = lastBoostLevel.get(playerId);

        if (previousLevel == null) {
            // The hero has just joined the ranks of the boosted.
            if (currentBoostLevel > 1) {
                playEnterBoostSound(player);
            }
        } else if (previousLevel != currentBoostLevel) {
            // The communal bond's strength has shifted.
            if (currentBoostLevel > previousLevel) {
                playBoostIncreaseSound(player);
            } else if (currentBoostLevel < previousLevel && currentBoostLevel <= 1) {
                // The hero has drifted away from the group.
                playBoostDecreaseSound(player);
            }
        }

        lastBoostLevel.put(playerId, currentBoostLevel);
    }

    /**
     * Play a triumphant fanfare as the hero enters a boosted fellowship.
     */
    private void playEnterBoostSound(Player player) {
        try {
            String soundName = configManager.getEnterBoostSound();
            Sound sound = Sound.valueOf(soundName);
            float volume = configManager.getSoundVolume();
            float pitch = configManager.getSoundPitch();

            player.playSound(player.getLocation(), sound, volume, pitch);

        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid fanfare sound: " + configManager.getEnterBoostSound());
        }
    }

    /**
     * Play a chime of ascension as the fellowship grows stronger.
     */
    private void playBoostIncreaseSound(Player player) {
        try {
            String soundName = configManager.getBoostChangeSound();
            Sound sound = Sound.valueOf(soundName);
            float volume = configManager.getSoundVolume();
            float pitch = configManager.getSoundPitch() + 0.2f; // A higher pitch for triumph

            player.playSound(player.getLocation(), sound, volume, pitch);

        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid chime sound: " + configManager.getBoostChangeSound());
        }
    }

    /**
     * Play a somber tone as the fellowship dwindles.
     */
    private void playBoostDecreaseSound(Player player) {
        try {
            String soundName = configManager.getBoostChangeSound();
            Sound sound = Sound.valueOf(soundName);
            float volume = configManager.getSoundVolume();
            float pitch = configManager.getSoundPitch() - 0.2f; // A lower pitch for a fading bond

            player.playSound(player.getLocation(), sound, volume, pitch);

        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid sound of fading: " + configManager.getBoostChangeSound());
        }
    }

    /**
     * Bestow all magical blessings (particles, lore, and song) upon the hero.
     */
    public void showAllEffects(Player player) {
        // Whisper a new tale on the action bar.
        updateActionBar(player);

        // Chronicle the hero's journey with sound.
        handleBoostChange(player);

        // The crops are blessed separately in their growth events.
    }

    /**
     * Cleanse the hero's log and spirit's memory of those who have departed the realm.
     */
    public void cleanupOfflinePlayers() {
        lastBoostLevel.entrySet().removeIf(entry -> {
            Player player = plugin.getServer().getPlayer(entry.getKey());
            return player == null || !player.isOnline();
        });

        lastActionBarUpdate.entrySet().removeIf(entry -> {
            Player player = plugin.getServer().getPlayer(entry.getKey());
            return player == null || !player.isOnline();
        });
    }

    /**
     * The hero has left the fellowship; remove them from our chronicles.
     */
    public void removePlayer(Player player) {
        UUID playerId = player.getUniqueId();
        lastBoostLevel.remove(playerId);
        lastActionBarUpdate.remove(playerId);
    }

    /**
     * Clear all chronicles and memories of the fellowship.
     */
    public void clearAll() {
        lastBoostLevel.clear();
        lastActionBarUpdate.clear();
    }

    /**
     * Conjure a powerful explosion of magic to celebrate a great feat of communal strength.
     */
    public void showBoostAchievementEffect(Player player, int boostLevel) {
        if (!configManager.isParticlesEnabled()) {
            return;
        }

        Location playerLoc = player.getLocation().add(0, 1, 0);

        try {
            // Unleash a burst of celebratory magic.
            player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, playerLoc, 20, 1, 1, 1, 0.1);

            // Play a special song of leveling up.
            if (configManager.isSoundsEnabled()) {
                player.playSound(playerLoc, Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.5f);
            }

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to conjure achievement magic: " + e.getMessage());
        }
    }
}