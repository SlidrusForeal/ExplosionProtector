package org.SlidrusForeal.explosionProtector;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command handler for /explosionprotector.
 *
 * Supported subcommands:
 * - enable – enables explosion protection.
 * - disable – disables explosion protection.
 * - stats – displays statistics (prevented blocks, number of CoreProtect queries).
 * - cache status – shows cache status.
 * - cache clear – clears the cache.
 */
public class ExplosionProtectorCommand implements CommandExecutor {

    private final ExplosionProtector plugin;

    public ExplosionProtectorCommand(ExplosionProtector plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check permission: only administrators can use this command
        if (!sender.hasPermission("explosionprotector.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /explosionprotector <enable|disable|stats|cache>");
            return true;
        }

        String subcommand = args[0].toLowerCase();
        switch (subcommand) {
            case "enable":
                plugin.setProtectionEnabled(true);
                sender.sendMessage(ChatColor.GREEN + "Explosion protection enabled.");
                break;
            case "disable":
                plugin.setProtectionEnabled(false);
                sender.sendMessage(ChatColor.GREEN + "Explosion protection disabled.");
                break;
            case "stats":
                sender.sendMessage(ChatColor.AQUA + "ExplosionProtector Statistics:");
                sender.sendMessage(ChatColor.AQUA + "Prevented blocks: " + plugin.getPreventedBlocksCount());
                sender.sendMessage(ChatColor.AQUA + "CoreProtect queries: " + plugin.getCoreProtectQueriesCount());
                break;
            case "cache":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.YELLOW + "Usage: /explosionprotector cache <status|clear>");
                    return true;
                }
                String cacheCmd = args[1].toLowerCase();
                if ("status".equals(cacheCmd)) {
                    sender.sendMessage(ChatColor.AQUA + plugin.getCacheStatus());
                } else if ("clear".equals(cacheCmd)) {
                    plugin.clearCache();
                    sender.sendMessage(ChatColor.GREEN + "Cache successfully cleared.");
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Invalid cache command. Use status or clear.");
                }
                break;
            default:
                sender.sendMessage(ChatColor.YELLOW + "Unknown command. Usage: /explosionprotector <enable|disable|stats|cache>");
                break;
        }
        return true;
    }
}
