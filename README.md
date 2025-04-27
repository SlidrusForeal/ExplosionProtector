# 💣 ExplosionProtector for CoreProtect

**Protect your players’ builds from unwanted explosions**  
A lightweight Spigot/Paper plugin that uses CoreProtect to prevent explosions from destroying blocks placed by players.

---

## 🧠 Description

`ExplosionProtector` hooks into all types of explosions (TNT, creeper, ender crystal, bed/respawn-anchor explosions, etc.) and checks each affected block’s origin via the CoreProtect API.  
- **Player-placed blocks** are protected and remain intact.  
- **All other blocks** (natural terrain, plugin-placed, etc.) are destroyed as normal.  
- **TNT chain reactions** are still allowed: TNT will break TNT and natural blocks, but any player-placed block in the blast radius remains safe.

This is ideal for:
- PvE or Creative servers where you want to preserve player builds.  
- Minigame or adventure maps that use TNT but need to protect certain structures.  
- Any world where accidental or malicious explosions should not ruin player work.

---

## ⚙️ Installation

1. **Download** the latest `ExplosionProtector.jar`.  
2. Place it into your server’s `plugins/` directory.  
3. Ensure you have **CoreProtect v10+** installed and enabled.  
4. **Start** or **reload** your server. You should see in console:
   ```
   [ExplosionProtector] Plugin enabled: protecting player-placed blocks from explosions.
   ```

---

## 🛠 Configuration

All settings are in `plugins/ExplosionProtector/`.

### 1. `config.yml`
```yaml
# config.yml
# Supported language codes: en, ru, es, zh, hi, ar, fr, de, ja, pt
language: en
```

### 2. Message files
On first run the plugin extracts:
- `messages.yml` (default English)
- `messages_ru.yml`
- `messages_es.yml`
- `messages_zh.yml`
- `messages_hi.yml`
- `messages_ar.yml`
- `messages_fr.yml`
- `messages_de.yml`
- `messages_ja.yml`
- `messages_pt.yml`

Each contains all user-facing strings. To add or adjust translations, edit the corresponding file in the plugin’s folder.

---

## 💻 Commands

All commands require the `explosionprotector.info` permission (default OP-only).

| Command                       | Description                                       |
|-------------------------------|---------------------------------------------------|
| `/ep status` or `/ep info`    | Show plugin status and number of blocks protected in the last explosion. |
| `/ep language <code>`         | (Admin) Change plugin language at runtime. Valid codes: `en`, `ru`, `es`, `zh`, `hi`, `ar`, `fr`, `de`, `ja`, `pt`. |

### Examples
```shell
/ep status
# Status: enabled
# Blocks protected in last operation: 17

/ep language ru
# Language set to 'ru'.
```

---

## 🔄 Change Log

### [1.1] – 2025-04-27
- **CoreProtect Lookup Cache**  
  Added Guava-backed cache to reduce repeated CoreProtect queries and improve performance during big explosions.
- **Multi-Language Support**  
  • Extracts all `messages_<lang>.yml` on first run.  
  • `config.yml` option `language: <code>`.  
  • `/ep language <code>` for on-the-fly language switching.
- **TNT Chain Reaction Handling**  
  Refactored logic so TNT chain reactions still destroy TNT and natural blocks but protect player-placed blocks.
- **Unified Explosion Handlers**  
  Consolidated `EntityExplodeEvent` and `BlockExplodeEvent` logic for consistent protection.
- **Automatic Resource Extraction**  
  Ensures no “file not found” warnings when all translation files are present in the JAR.
- **Configurable Messages & Clean Code**  
  All user text moved to message files; comments and code streamlined and fully English-documented.

### [1.0] – Initial Release
- Basic protection of player-placed blocks against all explosion types using CoreProtect API.
- Support for TNT, creeper, ender crystal, block explosions.
- `/ep status` command showing protection status.

---

## 🧱 Dependencies

- **Spigot / Paper** 1.13+  
- **CoreProtect** v10 or higher

---

## 📄 License

MIT License. See `LICENSE` in the GitHub repository for details.
