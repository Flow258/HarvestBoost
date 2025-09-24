package com.flowey258.harvestBoost.commands;

import com.flowey258.harvestBoost.HarvestBoost;
import com.flowey258.harvestBoost.config.ConfigManager;
import com.flowey258.harvestBoost.managers.BoostManager;
import com.flowey258.harvestBoost.managers.PlayerTracker;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HarvestBoostCommand implements CommandExecutor, TabCompleter {

    private final HarvestBoost plugin;
    private final ConfigManager configManager;
    private final BoostManager boostManager;
    private final PlayerTracker playerTracker;

    public HarvestBoostCommand(HarvestBoost plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.boostManager = plugin.getBoostManager();
        this.playerTracker = plugin.getPlayerTracker();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        return switch (subCommand) {
            case "reload" -> handleReload(sender);
            case "status" -> handleStatus(sender);
            case "info" -> handleInfo(sender);
            case "debug" -> handleDebug(sender, args);
            case "help" -> {
                showHelp(sender);
                yield true;
            }
            default -> {
                sender.sendMessage(configManager.getMessage("unknown-command"));
                showHelp(sender);
                yield true;
            }
        };
    }

    /**
     * Handle reload command
     */
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("harvestboost.admin")) {
            sender.sendMessage(configManager.getMessage("no-permission"));
            return true;
        }

        try {
            plugin.reload();
            sender.sendMessage(configManager.getMessage("reload-success"));
            return true;
        } catch (Exception e) {
            sender.sendMessage("§cError reloading config: " + e.getMessage());
            plugin.getLogger().severe("Error reloading config: " + e.getMessage());
            return true;
        }
    }

    /**
     * Handle status command
     */
    private boolean handleStatus(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }

        if (!player.hasPermission("harvestboost.use")) {
            player.sendMessage(configManager.getMessage("no-permission"));
            return true;
        }

        Location playerLoc = player.getLocation();
        int farmerCount = boostManager.getFarmerCount(playerLoc);
        int boostPercentage = boostManager.getBoostPercentage(playerLoc);

        player.sendMessage(configManager.getMessageNoPrefix("status-header"));
        player.sendMessage(configManager.getMessageNoPrefix("status-players")
                .replace("%count%", String.valueOf(farmerCount)));

        if (boostPercentage > 0) {
            player.sendMessage(configManager.getMessageNoPrefix("status-boost")
                    .replace("%boost%", String.valueOf(boostPercentage)));

            // Show individual farmer info if debug is enabled
            if (configManager.isDebugEnabled() && player.hasPermission("harvestboost.admin")) {
                player.sendMessage("§7Debug Info:");
                player.sendMessage("§7- Detection radius: " + configManager.getDetectionRadius());
                player.sendMessage("§7- Time in area: " + playerTracker.getTimeInCurrentArea(player) + "ms");
                player.sendMessage("§7- Boost multiplier: " + String.format("%.2f", boostManager.getBoostMultiplier(playerLoc)));
            }
        } else {
            player.sendMessage(configManager.getMessageNoPrefix("status-no-boost"));
        }

        return true;
    }

    /**
     * Handle info command
     */
    private boolean handleInfo(CommandSender sender) {
        sender.sendMessage("§a=== HarvestBoost Plugin Info ===");
        sender.sendMessage("§7Version: §b" + plugin.getDescription().getVersion());
        sender.sendMessage("§7Author: §b" + plugin.getDescription().getAuthors().get(0));
        sender.sendMessage("");
        sender.sendMessage("§7Detection radius: §b" + configManager.getDetectionRadius() + " blocks");
        sender.sendMessage("§7Max boost level: §b" + configManager.getMaxPlayers() + " players");
        sender.sendMessage("§7Boost levels:");

        for (int i = 1; i <= configManager.getMaxPlayers(); i++) {
            double multiplier = configManager.getBoostMultiplier(i);
            int percentage = configManager.getBoostPercentage(i);
            sender.sendMessage("§7  " + i + " players: §a" + percentage + "% boost §7(§b" +
                    String.format("%.2f", multiplier) + "x§7)");
        }

        sender.sendMessage("");
        sender.sendMessage("§7Enabled features:");
        sender.sendMessage("§7- Crops: " + (configManager.isCropsEnabled() ? "§aYes" : "§cNo"));
        sender.sendMessage("§7- Saplings: " + (configManager.isSaplingsEnabled() ? "§aYes" : "§cNo"));
        sender.sendMessage("§7- Bamboo: " + (configManager.isBambooEnabled() ? "§aYes" : "§cNo"));
        sender.sendMessage("§7- Tall plants: " + (configManager.isTallPlantsEnabled() ? "§aYes" : "§cNo"));
        sender.sendMessage("§7- Particles: " + (configManager.isParticlesEnabled() ? "§aYes" : "§cNo"));
        sender.sendMessage("§7- Sounds: " + (configManager.isSoundsEnabled() ? "§aYes" : "§cNo"));
        sender.sendMessage("§7- XP bonus: " + (configManager.isXpBonusEnabled() ? "§aYes" : "§cNo"));

        return true;
    }

    /**
     * Handle debug command
     */
    private boolean handleDebug(CommandSender sender, String[] args) {
        if (!sender.hasPermission("harvestboost.admin")) {
            sender.sendMessage(configManager.getMessage("no-permission"));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /harvestboost debug <on|off|player>");
            return true;
        }

        String debugAction = args[1].toLowerCase();

        switch (debugAction) {
            case "on" -> {
                configManager.getConfig().set("advanced.debug", true);
                plugin.saveConfig();
                sender.sendMessage("§aDebug mode enabled.");
                return true;
            }
            case "off" -> {
                configManager.getConfig().set("advanced.debug", false);
                plugin.saveConfig();
                sender.sendMessage("§cDebug mode disabled.");
                return true;
            }
            case "player" -> {
                String debugInfo = playerTracker.getPlayerDebugInfo(player);
                sender.sendMessage("§aDebug info for " + player.getName() + ":");
                sender.sendMessage("§7" + debugInfo);
                return true;
            }
            default -> {
                sender.sendMessage("§cUsage: /harvestboost debug <on|off|player>");
                return true;
            }
        }
    }

    /**
     * Show help message
     */
    private void showHelp(CommandSender sender) {
        sender.sendMessage("§a=== HarvestBoost Commands ===");
        sender.sendMessage("§7/harvestboost status §8- §7Show boost status in your area");
        sender.sendMessage("§7/harvestboost info §8- §7Show plugin information");
        sender.sendMessage("§7/harvestboost help §8- §7Show this help message");

        if (sender.hasPermission("harvestboost.admin")) {
            sender.sendMessage("§c=== Admin Commands ===");
            sender.sendMessage("§7/harvestboost reload §8- §7Reload plugin configuration");
            sender.sendMessage("§7/harvestboost debug <on|off|player> §8- §7Debug commands");
        }

        sender.sendMessage("");
        sender.sendMessage("§7Farm together with other players for faster crop growth!");
        sender.sendMessage("§7The more players farming nearby, the faster your crops grow!");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("status", "info", "help");

            if (sender.hasPermission("harvestboost.admin")) {
                subCommands = new ArrayList<>(subCommands);
                subCommands.addAll(Arrays.asList("reload", "debug"));
            }

            String partial = args[0].toLowerCase();
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(partial)) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            if (sender.hasPermission("harvestboost.admin")) {
                List<String> debugCommands = Arrays.asList("on", "off", "player");
                String partial = args[1].toLowerCase();
                for (String debugCommand : debugCommands) {
                    if (debugCommand.startsWith(partial)) {
                        completions.add(debugCommand);
                    }
                }
            }
        }

        return completions;
    }
}