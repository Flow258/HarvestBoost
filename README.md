# 🌱 HarvestBoost Plugin

A cooperative farming plugin for Minecraft 1.21.5 that encourages players to farm together by boosting crop growth when multiple players are farming in the same area.

## 🎯 Core Features

- **Cooperative Growth Boost**: Crops and saplings grow faster when 2+ players farm together
- **Configurable Boost Levels**: Customize growth multipliers based on player count
- **Visual Effects**: Particles, actionbar messages, and sound effects
- **Multiple Plant Types**: Works with crops, saplings, bamboo, and tall plants
- **XP Bonuses**: Extra experience when farming cooperatively
- **Performance Optimized**: Efficient caching and processing limits

## 📊 Default Boost Scaling

| Players Nearby | Growth Speed | Boost Percentage |
|----------------|--------------|------------------|
| 1 (solo)       | Normal       | 0%               |
| 2 players      | 1.25x        | +25%             |
| 3 players      | 1.5x         | +50%             |
| 4+ players     | 1.75x        | +75% (max)       |

## 🔧 Installation

1. **Requirements**:
   - Minecraft Server 1.21.5
   - Java 21
   - Maven 3.6+

2. **Building**:
   ```bash
   git clone <repository-url>
   cd HarvestBoost
   mvn clean package
   ```

3. **Installation**:
   - Place the generated JAR file in your server's `plugins/` folder
   - Restart your server
   - Configuration file will be generated automatically

## ⚙️ Configuration

The plugin generates a comprehensive `config.yml` file with all settings:

### Basic Settings
```yaml
detection:
  radius: 10                    # Detection radius in blocks
  check-interval: 100          # Update frequency in ticks

boosts:
  1: 1.0    # Solo farming - normal speed  
  2: 1.25   # 2 players - 25% faster
  3: 1.5    # 3 players - 50% faster
  4: 1.75   # 4 players - 75% faster
  max-players: 4
```

### Plant Types
```yaml
enable:
  crops: true          # Wheat, carrots, potatoes, etc.
  saplings: true       # Tree saplings and mushrooms
  bamboo: true         # Bamboo growth
  tall-plants: true    # Cactus, sugar cane, kelp
```

### Effects
```yaml
effects:
  particles:
    enabled: true
    type: "HAPPY_VILLAGER"
    amount: 3
  
  actionbar:
    enabled: true
    format: "🌱 Growth boosted by %boost%% (%players% farmers nearby!)"
  
  sounds:
    enabled: true
    enter-boost-area: "ENTITY_EXPERIENCE_ORB_PICKUP"
```

## 🎮 Commands

### Player Commands
- `/harvestboost status` - Show current boost level in your area
- `/harvestboost info` - Display plugin information and settings
- `/harvestboost help` - Show command help

### Admin Commands
# 🌱 HarvestBoost Plugin

A cooperative farming plugin for Minecraft 1.21.5 that encourages players to farm together by boosting crop growth when multiple players are farming in the same area. Now with **Team Support**!

## 🎯 Core Features

- **Cooperative Growth Boost**: Crops and saplings grow faster when 2+ players farm together
- **Team Integration**: Works with popular team/party plugins or built-in team system
- **Configurable Boost Levels**: Customize growth multipliers based on player count
- **Visual Effects**: Particles, actionbar messages, and sound effects
- **Multiple Plant Types**: Works with crops, saplings, bamboo, and tall plants
- **XP Bonuses**: Extra experience when farming cooperatively
- **Performance Optimized**: Efficient caching and processing limits

## 📊 Default Boost Scaling

| Players Nearby | Growth Speed | Boost Percentage |
|----------------|--------------|------------------|
| 1 (solo)       | Normal       | 0%               |
| 2 players      | 1.25x        | +25%             |
| 3 players      | 1.5x         | +50%             |
| 4+ players     | 1.75x        | +75% (max)       |

## 🤝 Team System Features

### Team Modes
- **Mixed Cooperation**: All players boost each other, team members get bonus
- **Team-Only**: Only team members can boost each other's farming
- **Cross-Team**: Different teams can cooperate (configurable)

### Supported Team Plugins
- **Parties** - Popular party plugin
- **mcMMO** - Party system integration
- **Towny** - Town member cooperation
- **Factions** - Faction member cooperation  
- **Guilds** - Guild member cooperation
- **Built-in** - Simple team system included

### Team Commands
```
/hb team create <n>         # Create a new team
/hb team join <n>           # Join existing team
/hb team leave              # Leave current team
/hb team invite <player>    # Invite player to team
/hb team accept             # Accept team invitation
/hb team decline            # Decline team invitation
/hb team info               # Show team status
```

## 🔧 Installation

1. **Requirements**:
   - Minecraft Server 1.21.5
   - Java 21
   - Maven 3.6+

2. **Building**:
   ```bash
   git clone <repository-url>
   cd HarvestBoost
   mvn clean package
   ```

3. **Installation**:
   - Place the generated JAR file in your server's `plugins/` folder
   - Restart your server
   - Configuration file will be generated automatically

## ⚙️ Configuration

The plugin generates a comprehensive `config.yml` file with all settings:

### Team Settings
```yaml
teams:
  mode: "auto"              # "disabled", "builtin", "auto", or plugin name
  
  team-only-boost:
    enabled: false          # Only team members boost each other
    team-bonus-multiplier: 1.2  # Extra boost for teammates
  
  cross-team:
    enabled: true           # Allow different teams to cooperate
    reduction-factor: 0.8   # Reduced boost for non-teammates
```

### Basic Settings
```yaml
detection:
  radius: 10                    # Detection radius in blocks
  check-interval: 100          # Update frequency in ticks

boosts:
  1: 1.0    # Solo farming - normal speed  
  2: 1.25   # 2 players - 25% faster
  3: 1.5    # 3 players - 50% faster
  4: 1.75   # 4 players - 75% faster
  max-players: 4
```

### Plant Types
```yaml
enable:
  crops: true          # Wheat, carrots, potatoes, etc.
  saplings: true       # Tree saplings and mushrooms
  bamboo: true         # Bamboo growth
  tall-plants: true    # Cactus, sugar cane, kelp
```

### Effects
```yaml
effects:
  particles:
    enabled: true
    type: "HAPPY_VILLAGER"
    amount: 3
  
  actionbar:
    enabled: true
    format: "🌱 Growth boosted by %boost%% (%players% farmers nearby!)"
  
  sounds:
    enabled: true
    enter-boost-area: "ENTITY_EXPERIENCE_ORB_PICKUP"
```

## 🎮 Commands

### Player Commands
- `/harvestboost status` - Show current boost level in your area
- `/harvestboost info` - Display plugin information and settings
- `/harvestboost team` - Team management (if using built-in system)
- `/harvestboost help` - Show command help

### Admin Commands
- `/harvestboost reload` - Reload configuration
- `/harvestboost debug <on|off|player|team>` - Debug commands

## 🔑 Permissions

- `harvestboost.use` - Basic plugin usage (default: true)
- `harvestboost.admin` - Admin commands (default: op)
- `harvestboost.reload` - Reload configuration (default: op)

## 🌟 How It Works

### Team Mode: Disabled
- All nearby players contribute to boost regardless of relationships
- Classic cooperation mode

### Team Mode: Mixed Cooperation (Default)
- All players can cooperate and boost each other
- Team members get bonus multiplier on top of regular boost
- Cross-team cooperation allowed with optional reduction

### Team Mode: Team-Only
- Only team members can boost each other's farming
- Solo players or mixed groups get no boost
- Perfect for team-focused servers

## 📈 Performance Features

- **Smart Caching**: Location-based boost caching with configurable TTL
- **Efficient Processing**: Configurable crop processing limits per tick
- **Memory Optimized**: Automatic cleanup of offline player data
- **Async Safe**: Thread-safe data structures and operations

## 🎨 Visual Effects

- **Particles**: Customizable particle effects on boosted crops
- **Action Bar**: Real-time boost status display
- **Sound Effects**: Audio feedback for boost changes
- **Team Indicators**: Visual distinction for team vs non-team boosts

## 📝 Example Scenarios

### Mixed Team Server
```yaml
teams:
  mode: "auto"
  team-only-boost:
    enabled: false
    team-bonus-multiplier: 1.2
```
- 3 random players farming = 50% boost
- 2 teammates + 1 other = 50% boost + 20% team bonus = 70% boost total

### Team-Only Server  
```yaml
teams:
  mode: "builtin"
  team-only-boost:
    enabled: true
```
- 3 random players = no boost
- 3 teammates = 50% boost + team bonus

### Classic Server
```yaml
teams:
  mode: "disabled"
```
- Works exactly like before teams were added
- All players cooperate equally

## 🚀 Advanced Features

- **Multi-World Support**: Configure per-world settings
- **XP Bonuses**: Extra experience for cooperative farming
- **Debug Mode**: Comprehensive debugging tools for admins
- **Hot Reload**: Change settings without server restart
- **Plugin Integration**: Seamless integration with popular team plugins

## 📊 Performance Monitoring

The plugin includes built-in performance monitoring:
- Tracks boost calculations per second
- Monitors memory usage of caches
- Provides detailed debug information
- Automatic cleanup of expired data

Use `/hb debug on` to enable detailed performance logging.
