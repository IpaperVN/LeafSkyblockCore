package me.ipapervn.leafskyblockcore.listeners;

import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import me.ipapervn.leafskyblockcore.manager.MiningManager;
import me.ipapervn.leafskyblockcore.mining.MiningStats;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;

public class MiningListener implements Listener {

    private final LeafSkyblockCore plugin;
    private final MiningManager manager;

    public MiningListener(@NotNull LeafSkyblockCore plugin) {
        this.plugin = plugin;
        this.manager = plugin.getMiningManager();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        var loc = event.getBlock().getLocation();

        // Chỉ xử lý trong mining region
        if (manager.isNotInMiningRegion(loc)) return;

        MiningManager.OreData ore = manager.getOreData(loc);
        if (ore == null) return;

        // Cancel vanilla break hoàn toàn
        event.setCancelled(true);

        // Cooldown check — chống macro/autoclick
        if (!manager.checkCooldown(player.getUniqueId())) return;

        // Nếu đang đào block này rồi thì bỏ qua
        if (manager.hasSession(player.getUniqueId())) return;

        // Check Breaking Power
        MiningStats stats = manager.getStats(player.getUniqueId());
        if (stats.breakingPower() < ore.breakingPower()) {
            player.sendMessage(plugin.getMessagesConfig().getMessage("mining.not-enough-power",
                java.util.Map.of("required", String.valueOf((int) ore.breakingPower()),
                                 "current",  String.valueOf((int) stats.breakingPower()))));
            return;
        }

        // Bắt đầu session
        manager.startSession(player, loc, ore);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockDamageAbort(BlockDamageAbortEvent event) {
        var loc = event.getBlock().getLocation();
        if (manager.isNotInMiningRegion(loc)) return;
        manager.cancelSession(event.getPlayer().getUniqueId());
    }

    // Cancel vanilla BlockBreakEvent trong region — block chỉ vỡ qua custom system
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (manager.isNotInMiningRegion(event.getBlock().getLocation())) return;
        if (manager.getOreData(event.getBlock().getLocation()) == null) return;
        event.setCancelled(true);
    }

    // Update stat cache khi đổi item cầm tay
    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemHeld(PlayerItemHeldEvent event) {
        manager.cancelSession(event.getPlayer().getUniqueId());
        manager.cacheStats(event.getPlayer());
    }

    // Update stat cache khi swap tay (F key)
    @EventHandler(priority = EventPriority.MONITOR)
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        manager.cancelSession(event.getPlayer().getUniqueId());
        manager.cacheStats(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        manager.cacheStats(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        manager.cancelSession(event.getPlayer().getUniqueId());
        manager.removeStats(event.getPlayer().getUniqueId());
    }
}
