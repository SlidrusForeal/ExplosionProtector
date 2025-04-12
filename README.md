# 💣 ExplosionProtector

**Protects structures from explosions if the block was placed by a player**  
A plugin for Minecraft (Spigot/Paper) that uses CoreProtect to prevent explosions from destroying blocks.

---

## 🧠 Description

`ExplosionProtector` prevents blocks that were **placed by players** from being destroyed during explosions. This is especially useful on PvE servers or creative worlds where preserving player constructions is important.

The plugin utilizes the **CoreProtect API** to determine the origin of a block and excludes only those blocks that were manually placed by a player from being affected by explosions.

---

## ⚙️ How It Works

- When an explosion occurs (TNT, creeper, or block explosion), the affected blocks are checked.
- Each block is analyzed using the CoreProtect API:
  - If the block was **placed by a player** (and not by a system process, such as WorldEdit), it is protected from destruction.
  - TNT blocks are always allowed to explode.
- Other blocks are destroyed as normal.

---

## 🧱 Dependencies

- [✅] **Spigot / Paper** (1.13+)
- [✅] **CoreProtect** (v10 or higher)

---

## 🚀 Installation

1. Make sure **CoreProtect** is installed and active.
2. Download the `ExplosionProtector.jar` file and place it in the `plugins/` folder.
3. Restart your server.
4. Check your logs for the message:
[ExplosionProtector] Plugin successfully enabled!

---

## 📋 Usage Example

A player builds a house → a creeper explodes nearby → the house remains intact because the blocks were manually placed by the player.

---

## 🔐 Safety and Performance

- The protection mechanism is applied only to the list of blocks affected by an explosion.
- A safe wrapper around the CoreProtect API is used.
- The plugin does not store any data or impose additional load on the server under normal conditions.

---

## 🛠 Support and Suggestions

Found a bug? Want to propose a new feature (for example, configuring which block types to protect in `config.yml`)?  
Please create an Issue or a Pull Request!

---

## 🏆 Author

Developed with a passion for stability and order by:  
**SlidrusForeal**  
[GitHub](https://github.com/SlidrusForeal)

---

## 📜 License

MIT License — Feel free to use, modify, and distribute.
