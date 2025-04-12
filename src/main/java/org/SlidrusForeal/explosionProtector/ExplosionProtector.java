package org.SlidrusForeal.explosionProtector;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.coreprotect.CoreProtectAPI.ParseResult;

/**
 * Main class of the ExplosionProtector plugin.
 *
 * Features:
 * - Protects blocks from explosions if they were placed by a player.
 * - Provides admin commands via /explosionprotector to control the plugin.
 * - Implements a fallback mode on CoreProtect errors with extended logging.
 */
public class ExplosionProtector extends JavaPlugin implements Listener {

    private CoreProtectAPI coreProtect;
    // Flag to determine whether protection is enabled
    private boolean protectionEnabled = true;
    // Cache to store block-check results (key: world_x_y_z)
    private final Map<String, Boolean> blockCache = new ConcurrentHashMap<>();
    // Counters for plugin statistics
    private long preventedBlocksCount = 0;
    private long coreProtectQueriesCount = 0;

    @Override
    public void onEnable() {
        coreProtect = fetchCoreProtectAPI();
        if (coreProtect == null || !coreProtect.isEnabled() || coreProtect.APIVersion() < 10) {
            getLogger().severe("[ExplosionProtector] CoreProtect not found or incompatible version.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Register event listeners
        Bukkit.getPluginManager().registerEvents(this, this);
        // Register the admin command /explosionprotector
        this.getCommand("explosionprotector").setExecutor(new ExplosionProtectorCommand(this));

        getLogger().info("[ExplosionProtector] Plugin successfully enabled!");
    }

    /**
     * Fetches the CoreProtect API if the plugin is available.
     */
    private CoreProtectAPI fetchCoreProtectAPI() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("CoreProtect");
        if (plugin instanceof CoreProtect) {
            return ((CoreProtect) plugin).getAPI();
        }
        return null;
    }

    /**
     * Event handler for entity explosions.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!protectionEnabled) return;
        protectPlacedBlocks(event.blockList());
    }


    /**
     * Event handler for block explosions.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        if (!protectionEnabled) return;
        protectPlacedBlocks(event.blockList());
    }

    /**
     * Filters the list of blocks affected by the explosion, removing those that should be protected.
     * In case of an exception (e.g. a CoreProtect error), the fallback mode is activated:
     * the block is not protected but the error is logged and administrators are notified.
     */
    private void protectPlacedBlocks(List<Block> blocks) {
        if (blocks == null || blocks.isEmpty()) return;

        blocks.removeIf(block -> {
            try {
                if (shouldProtectBlock(block)) {
                    // Increase the count of prevented blocks
                    preventedBlocksCount++;
                    return true;
                }
            } catch (Exception e) {
                // Fallback: error while processing the block. Do not prevent destruction,
                // but log the error and notify administrators.
                getLogger().severe("[ExplosionProtector] Error processing block at "
                        + block.getLocation() + ": " + e.getMessage());
                notifyAdmins("Error processing block at " + block.getLocation()
                        + ": " + e.getMessage());
            }
            return false;
        });
    }

    /**
     * Determines whether the given block should be protected.
     * Currently, TNT blocks are not protected.
     */
    private boolean shouldProtectBlock(Block block) {
        if (block.getType() == Material.TNT) return false;
        return isPlayerPlaced(block);
    }

    /**
     * Checks via CoreProtect whether the block was placed by a player.
     * Uses caching to reduce the load on the CoreProtect API.
     */
    public boolean isPlayerPlaced(Block block) {
        if (coreProtect == null) return false;
        coreProtectQueriesCount++;

        String key = block.getWorld().getName() + "_" + block.getX() + "_" + block.getY() + "_" + block.getZ();
        if (blockCache.containsKey(key)) {
            return blockCache.get(key);
        }

        boolean result = false;
        try {
            List<String[]> lookup = coreProtect.blockLookup(block, 0);
            if (lookup != null && !lookup.isEmpty()) {
                // Process records in reverse order (latest first)
                for (int i = lookup.size() - 1; i >= 0; i--) {
                    ParseResult parsed = coreProtect.parseResult(lookup.get(i));
                    if (parsed == null) continue;
                    // ActionId 1 indicates a block placement
                    if (parsed.getActionId() == 1) {
                        String player = parsed.getPlayer();
                        result = (player != null && !player.startsWith("#"));
                        if (result) break;
                    }
                }
            }
        } catch (Exception e) {
            // Fallback mode: on CoreProtect error, do not prevent block destruction,
            // but log the error and notify administrators.
            getLogger().severe("[ExplosionProtector] Error checking block: " + e.getMessage());
            notifyAdmins("CoreProtect error: " + e.getMessage());
            result = false;
        }
        // Cache the result
        blockCache.put(key, result);
        return result;
    }

    /**
     * Notifies all online administrators (with explosionprotector.admin permission) about an error.
     */
    private void notifyAdmins(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("explosionprotector.admin")) {
                player.sendMessage(ChatColor.RED + "[ExplosionProtector] " + message);
            }
        }
    }

    // Methods used by the command handler for plugin management

    public boolean isProtectionEnabled() {
        return protectionEnabled;
    }

    public void setProtectionEnabled(boolean protectionEnabled) {
        this.protectionEnabled = protectionEnabled;
    }

    public void clearCache() {
        blockCache.clear();
    }

    public long getPreventedBlocksCount() {
        return preventedBlocksCount;
    }

    public long getCoreProtectQueriesCount() {
        return coreProtectQueriesCount;
    }

    public String getCacheStatus() {
        return "Cache contains " + blockCache.size() + " entries.";
    }
}
