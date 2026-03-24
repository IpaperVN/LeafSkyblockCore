package me.ipapervn.leafskyblockcore.listeners;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import me.ipapervn.leafskyblockcore.manager.CropsTrackerManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class CropsTrackerListener implements Listener {

    private final CropsTrackerManager manager;

    public CropsTrackerListener(@NotNull LeafSkyblockCore plugin) {
        this.manager = plugin.getCropsTrackerManager();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getType();

        if (!manager.isCropTracked(material)) return;
        if (!isFullyGrown(block)) return;
        if (!manager.getSeasonManager().isAllowed(material)) return;

        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);
        Island island = SuperiorSkyblockAPI.getIslandAt(block.getLocation());

        if (island == null) {
            return;
        }

        if (!island.isMember(superiorPlayer)) {
            return;
        }

        int points = manager.getCropPoints(material);
        manager.addPoints(player.getUniqueId(), points);
    }

    private boolean isFullyGrown(@NotNull Block block) {
        BlockData blockData = block.getBlockData();

        if (blockData instanceof Ageable ageable) {
            return ageable.getAge() == ageable.getMaximumAge();
        }

        return true;
    }
}
