
# 🌱 HarvestBoost Plugin

A cooperative farming plugin for Minecraft `1.21` that encourages players to farm together by boosting crop growth when multiple players are active in the same area.

Now includes **party/guild/town/faction integrations** so boosts can apply to official groups, not just random nearby players.

---

## 🎯 Core Features

* **Cooperative Growth Boost** – Crops, saplings, and other plants grow significantly faster when 2 or more players are farming in close proximity.
* **Party & Guild Integrations** – Auto-detects popular plugins like `mcMMO`, `Towny`, `Factions`, and `Guilds` to apply boosts only within groups/teams. Falls back to a **built-in team system** if no integration is available.
* **Highly Configurable** – Customize nearly every aspect, including boost levels, detection radius, supported plants, and performance settings.
* **Informative Visuals** – Players receive feedback through configurable particle effects, action bar messages, and sound effects.
* **Multiple Plant Types** – The boost applies to a wide variety of plants, including standard crops, saplings, bamboo, sugar cane, cactus, and kelp.
* **XP Bonuses** – Reward cooperative farming with extra experience points when harvesting fully grown crops.
* **Performance Tuned** – Lightweight design with settings to control processing load (`max-crops-per-tick`) and efficient location caching.
* **World Blacklist** – Disable the plugin’s effects in specific worlds like the Nether or The End.
* **Customizable Messages** – Change all player-facing messages to match your server’s theme.

---

## 🔗 Plugin Integrations

HarvestBoost automatically detects and integrates with team/party plugins.

| Integration Detected | Behavior                                                                          |
| -------------------- | --------------------------------------------------------------------------------- |
| **mcMMO**            | Boosts apply to nearby members of the same **party**.                             |
| **Towny**            | Boosts apply to nearby members of the same **town**.                              |
| **Factions**         | Boosts apply to nearby members of the same **faction**.                           |
| **Guilds**           | Boosts apply to nearby members of the same **guild**.                             |
| **None**             | Falls back to HarvestBoost’s **built-in team system** (all nearby players count). |

When the plugin loads, it logs which integration was enabled, e.g.:

```
[HarvestBoost] Auto-detected and enabled mcMMO party integration.
```

---

## 📊 Default Boost Scaling

The growth speed bonus scales with the number of players farming together. This is fully customizable in the `config.yml`.

| Players Nearby | Growth Speed Multiplier | Boost Percentage |
| -------------- | ----------------------- | ---------------- |
| 1 (solo)       | 1.0×                    | 0%               |
| 2 players      | 1.25×                   | +25%             |
| 3 players      | 1.5×                    | +50%             |
| 4+ players     | 1.75×                   | +75% (max)       |

---

## 🔧 Installation

1. Download the latest release of the plugin.
2. Place the `HarvestBoost-X.X.X.jar` file into your server’s `plugins/` folder.
3. Restart or reload your server.
4. The plugin will generate a `config.yml` file in `plugins/HarvestBoost/`. Modify it to your liking and run `/harvestboost reload`.

---

## ⚙️ Configuration (`config.yml`)

*(Already updated with integrations and extra features – detection, boosts, effects, XP bonus, debug, etc.)*
👉 See the included [`config.yml`](./config.yml) for details.

---

## 🎮 Commands

| Command               | Description                                     | Permission           |
| --------------------- | ----------------------------------------------- | -------------------- |
| `/hb status`          | Shows the current boost level in your area.     | `harvestboost.use`   |
| `/hb info`            | Displays plugin version and active integration. | `harvestboost.use`   |
| `/hb help`            | Shows a list of available commands.             | `harvestboost.use`   |
| `/hb reload`          | Reloads the `config.yml` from disk.             | `harvestboost.admin` |
| `/hb debug <on\|off>` | Toggles debug mode for diagnostics.             | `harvestboost.admin` |

---

## 📜 Permissions

| Permission Node       | Description                                                            | Default |
| --------------------- | ---------------------------------------------------------------------- | ------- |
| `harvestboost.use`    | Access to basic commands like `/hb status` and `/hb info`.             | true    |
| `harvestboost.admin`  | Grants access to all admin commands like `/hb reload` and `/hb debug`. | op      |
| `harvestboost.reload` | Allows reloading the plugin configuration.                             | op      |

---
