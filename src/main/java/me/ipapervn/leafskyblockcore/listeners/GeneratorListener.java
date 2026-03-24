package me.ipapervn.leafskyblockcore.listeners;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import me.ipapervn.leafskyblockcore.manager.GeneratorManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GeneratorListener implements Listener {

    private final LeafSkyblockCore plugin;
    private final GeneratorManager manager;

    public GeneratorListener(@NotNull LeafSkyblockCore plugin) {
        this.plugin = plugin;
        this.manager = plugin.getGeneratorManager();
    }

    // Fix #1/#6: changed MONITOR to HIGH — MONITOR must not cancel events
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        // Fix #8: use configured material from manager instead of hardcoded END_PORTAL_FRAME
        if (block.getType() != manager.getGeneratorMaterial()) return;

        // Fix 6: check NBT first, ignore non-generator items
        if (!manager.isGeneratorItem(event.getItemInHand())) return;

        Player player = event.getPlayer();

        if (!player.hasPermission(plugin.getPermissionsConfig().getPermission("generator.use"))
            && manager.isNotAdmin(player)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getMessagesConfig().getMessage("general.no-permission"));
            return;
        }

        Island island = getOwnIsland(player, block);
        if (island == null && manager.isNotAdmin(player)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getMessagesConfig().getMessage("generator.not-own-island"));
            return;
        }

        // Fix 3: duplicate location check
        if (manager.isGenerator(block.getLocation())) {
            event.setCancelled(true);
            return;
        }

        // Fix 4: max per island check (admin bypasses)
        if (manager.isNotAdmin(player) && island != null) {
            int max = manager.getMaxPerIsland();
            if (max > 0 && manager.countGeneratorsOnIsland(island) >= max) {
                event.setCancelled(true);
                player.sendMessage(plugin.getMessagesConfig().getMessage("generator.max-reached"));
                return;
            }
        }

        manager.startCountdown(block.getLocation(), player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Only main hand right-click on block
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType() != manager.getGeneratorMaterial()) return;
        if (!manager.isGenerator(block.getLocation())) return;

        event.setCancelled(true);
        manager.openGui(event.getPlayer(), block.getLocation());
    }

    // Fix #6: changed MONITOR to HIGH
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        // Fix #8: use configured material
        if (block.getType() != manager.getGeneratorMaterial()) return;
        if (!manager.isGenerator(block.getLocation())) return;

        Player player = event.getPlayer();
        if (!player.hasPermission(plugin.getPermissionsConfig().getPermission("generator.break")) &&
            manager.isNotAdmin(player)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getMessagesConfig().getMessage("general.no-permission"));
            return;
        }

        manager.removeGenerator(block.getLocation());

        // Fix 5: drop generator item back
        event.setDropItems(false);
        block.getWorld().dropItemNaturally(block.getLocation(), manager.createGeneratorItem());
    }

    @Nullable
    private Island getOwnIsland(@NotNull Player player, @NotNull Block block) {
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);
        Island island = SuperiorSkyblockAPI.getIslandAt(block.getLocation());
        if (island == null || !island.isMember(superiorPlayer)) return null;
        return island;
    }
}
