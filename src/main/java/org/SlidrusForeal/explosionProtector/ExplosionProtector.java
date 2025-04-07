package org.SlidrusForeal.explosionProtector;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExplosionProtector extends JavaPlugin implements Listener {

    private CoreProtectAPI coreProtect;

    @Override
    public void onEnable() {
        coreProtect = fetchCoreProtectAPI();
        if (coreProtect == null || !coreProtect.isEnabled() || coreProtect.APIVersion() < 10) {
            getLogger().severe("[ExplosionProtector] CoreProtect не найден или версия несовместима.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("[ExplosionProtector] Плагин успешно активирован!");
    }

    /**
     * Получаем CoreProtect API, если плагин доступен и корректен.
     */
    private CoreProtectAPI fetchCoreProtectAPI() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("CoreProtect");
        if (plugin instanceof CoreProtect) {
            return ((CoreProtect) plugin).getAPI();
        }
        return null;
    }

    /**
     * Обработка событий взрывов мобов и блоков.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        protectPlacedBlocks(event.blockList());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        protectPlacedBlocks(event.blockList());
    }

    /**
     * Удаляет из списка взрыва блоки, поставленные игроками.
     */
    private void protectPlacedBlocks(List<Block> blocks) {
        if (blocks == null || blocks.isEmpty()) return;

        blocks.removeIf(block -> shouldProtectBlock(block));
    }

    /**
     * Определяет, нужно ли защищать блок от разрушения.
     */
    private boolean shouldProtectBlock(Block block) {
        // TNT всегда разрешено взрывать
        if (block.getType() == Material.TNT) return false;

        return isPlayerPlaced(block);
    }

    /**
     * Проверка через CoreProtect: был ли блок поставлен игроком.
     */
    private boolean isPlayerPlaced(Block block) {
        if (coreProtect == null) return false;

        try {
            List<String[]> lookup = coreProtect.blockLookup(block, 0);
            if (lookup == null || lookup.isEmpty()) return false;

            // Идём в обратном порядке (сначала последние изменения)
            for (int i = lookup.size() - 1; i >= 0; i--) {
                ParseResult result = coreProtect.parseResult(lookup.get(i));
                if (result == null) continue;

                // ActionId == 1 → размещение
                if (result.getActionId() == 1) {
                    String player = result.getPlayer();
                    // Игнорируем системных "игроков", например, плагины (начинаются с "#")
                    return player != null && !player.startsWith("#");
                }
            }

        } catch (Exception e) {
            getLogger().warning("[ExplosionProtector] Ошибка при проверке блока: " + e.getMessage());
        }

        return false;
    }
}
