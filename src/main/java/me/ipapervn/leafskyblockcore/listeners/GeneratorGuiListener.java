package me.ipapervn.leafskyblockcore.listeners;

import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import me.ipapervn.leafskyblockcore.gui.GeneratorGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;

public class GeneratorGuiListener implements Listener {

    private final LeafSkyblockCore plugin;

    public GeneratorGuiListener(@NotNull LeafSkyblockCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof GeneratorGui gui)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        int slot = event.getRawSlot();
        if (slot < 0 || slot >= event.getInventory().getSize()) return;

        if (slot == gui.getCollectSlot()) {
            player.closeInventory();
            plugin.getGeneratorManager().collectItems(player, gui.getGeneratorLocation());
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof GeneratorGui)) return;
        event.setCancelled(true);
    }
}
