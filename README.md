Of course. Here is a comprehensive README file for your HarvestBoost plugin, created by combining all the details from your plugin.yml, config.yml, and the previous Java code.

🌱 HarvestBoost Plugin
A cooperative farming plugin for Minecraft 1.21 that encourages players to farm together by boosting crop growth when multiple players are active in the same area.

🎯 Core Features
Cooperative Growth Boost: Crops, saplings, and other plants grow significantly faster when 2 or more players are farming in close proximity.

Highly Configurable: Customize nearly every aspect, including boost levels, detection radius, supported plants, and performance settings.

Informative Visuals: Players receive feedback through configurable particle effects, action bar messages, and sound effects.

Multiple Plant Types: The boost applies to a wide variety of plants, including standard crops, saplings, bamboo, sugar cane, and kelp.

XP Bonuses: Reward cooperative farming with extra experience points when harvesting fully grown crops.

Performance Tuned: Built to be lightweight with settings to control processing load (max-crops-per-tick) and efficient location caching.

World Blacklist: Disable the plugin's effects in specific worlds like the Nether or The End.

Customizable Messages: Change all player-facing messages to match your server's theme.

📊 Default Boost Scaling
The growth speed bonus scales with the number of players farming together. This is fully customizable in the config.yml.

Players Nearby	Growth Speed Multiplier	Boost Percentage
1 (solo)	1.0x	0
2 players	1.25x	+25
3 players	1.5x	+50
4+ players	1.75x	+75 (max)

Export to Sheets
🔧 Installation
Download the latest release of the plugin.

Place the HarvestBoost-X.X.X.jar file into your server's plugins/ folder.

Restart or reload your server.

The plugin will generate a config.yml file in plugins/HarvestBoost/. Modify it to your liking and run /harvestboost reload.

⚙️ Configuration (config.yml)
The plugin is controlled through a detailed configuration file, allowing you to fine-tune its behavior.

YAML

# HarvestBoost Configuration File

# Detection radius for finding nearby farming players (in blocks)
detection:
  radius: 10
  # How often to check for nearby players (in ticks, 20 ticks = 1 second)
  check-interval: 100

# Growth multipliers based on number of nearby farming players
boosts:
  # Player count: multiplier (1.0 = normal speed, 2.0 = 2x speed)
  1: 1.0   # Solo farming - normal speed
  2: 1.25  # 2 players - 25% faster
  3: 1.5   # 3 players - 50% faster
  4: 1.75  # 4 players - 75% faster
  # Maximum boost level (caps at this many players)
  max-players: 4

# What should be boosted
enable:
  crops: true       # Wheat, carrots, potatoes, etc.
  saplings: true    # Tree saplings and mushrooms
  bamboo: true      # Bamboo growth
  tall-plants: true # Cactus, sugar cane, kelp

# Visual and feedback settings
effects:
  particles:
    enabled: true
    type: "HAPPY_VILLAGER"
    amount: 3
    interval: 60
  actionbar:
    enabled: true
    update-interval: 40
    format: "🌱 Growth boosted by %boost%% (%players% farmers nearby!)"
  sounds:
    enabled: true
    enter-boost-area: "ENTITY_EXPERIENCE_ORB_PICKUP"
    boost-change: "BLOCK_NOTE_BLOCK_CHIME"
    volume: 0.5
    pitch: 1.2

# Advanced settings
advanced:
  minimum-presence-time: 100
  debug: false
  disabled-worlds:
    - "world_nether"
    - "world_the_end"
  farming-detection-radius: 3
  xp-bonus:
    enabled: true
    per-level: 1 # Extra XP orbs per boost level

# Performance settings
performance:
  max-crops-per-tick: 50
  location-cache-time: 20

# All plugin messages
messages:
  prefix: "&a[HarvestBoost] "
  reload-success: "&aConfiguration reloaded successfully!"
  no-permission: "&cYou don't have permission to use this command."
  status-header: "&e--- Harvest Boost Status ---"
  status-players: "&7Players in area: &b%count%"
  status-boost: "&7Current boost: &a%boost%%"
  status-no-boost: "&7No boost active in this area."
🎮 Commands
The main command is /harvestboost, which can also be accessed via the aliases /hb or /harvest.

Command	Description	Permission
/hb status	Shows the current boost level in your area.	harvestboost.use
/hb info	Displays plugin version and key settings.	harvestboost.use
/hb help	Shows a list of available commands.	harvestboost.use
/hb reload	Reloads the config.yml from disk.	harvestboost.admin
/hb debug <on|off>	Toggles debug mode for advanced diagnostics.	harvestboost.admin

Export to Sheets
📜 Permissions
Permission Node	Description	Default
harvestboost.use	(Player) Access to basic commands like /hb status and /hb info.	true
harvestboost.admin	(Admin) Grants access to all admin commands like /hb reload.	op
