package me.ipapervn.leafskyblockcore.gui;

import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import me.ipapervn.leafskyblockcore.manager.GeneratorManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * GUI hiển thị thông tin generator khi player chuột phải vào block.
 * Layout và items hoàn toàn config trong generator/config.yml section gui.
 */
public class GeneratorGui implements InventoryHolder {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final LeafSkyblockCore plugin;
    private final Inventory inventory;
    private int collectSlot = -1;
    private final Location generatorLocation;
    // Cache slot configs for refresh
    private final ConfigurationSection slotsSection;
    private final int inventorySize;
    private final String playerName;

    public GeneratorGui(@NotNull LeafSkyblockCore plugin, @NotNull Player player, @NotNull Location generatorLocation) {
        this.plugin = plugin;
        this.generatorLocation = generatorLocation;
        this.playerName = player.getName();

        FileConfiguration config = plugin.getGeneratorManager().getConfig();
        GeneratorManager manager = plugin.getGeneratorManager();
        GeneratorManager.GeneratorData data = manager.getGeneratorData(generatorLocation);

        int secondsLeft = data != null ? data.secondsLeft() : 0;
        int storedCount = manager.getStoredItems(generatorLocation).stream()
            .mapToInt(ItemStack::getAmount).sum();

        int size = config.getInt("gui.size", 27);
        size = Math.max(9, Math.min(54, (int) Math.ceil(size / 9.0) * 9));
        this.inventorySize = size;

        String status = config.getString("gui.status-counting", "Counting down");
        String title = apply(config.getString("gui.title", "&8⚙ Generator"),
            playerName, secondsLeft, status, storedCount);

        this.inventory = Bukkit.createInventory(this, size,
            MM.deserialize(title));

        ItemStack bg = buildItem(
            config.getString("gui.background.material", "GRAY_STAINED_GLASS_PANE"),
            config.getString("gui.background.name", " "),
            List.of(), playerName, secondsLeft, status, storedCount);
        for (int i = 0; i < size; i++) inventory.setItem(i, bg);

        this.slotsSection = config.getConfigurationSection("gui.slots");
        fillSlots(secondsLeft, status, storedCount);
    }

    /** Called every second by GeneratorManager to update dynamic slots. */
    public void refresh() {
        GeneratorManager manager = plugin.getGeneratorManager();
        GeneratorManager.GeneratorData data = manager.getGeneratorData(generatorLocation);
        int secondsLeft = data != null ? data.secondsLeft() : 0;
        int storedCount = manager.getStoredItems(generatorLocation).stream()
            .mapToInt(ItemStack::getAmount).sum();
        String status = plugin.getGeneratorManager().getConfig()
            .getString("gui.status-counting", "Counting down");
        fillSlots(secondsLeft, status, storedCount);
    }

    private void fillSlots(int secondsLeft, @NotNull String status, int storedCount) {
        if (slotsSection == null) return;
        for (String key : slotsSection.getKeys(false)) {
            ConfigurationSection slot = slotsSection.getConfigurationSection(key);
            if (slot == null) continue;
            int slotIndex = slot.getInt("slot", -1);
            if (slotIndex < 0 || slotIndex >= inventorySize) continue;

            String name = apply(slot.getString("name", ""), playerName, secondsLeft, status, storedCount);
            List<String> lore = slot.getStringList("lore").stream()
                .map(l -> apply(l, playerName, secondsLeft, status, storedCount))
                .toList();
            inventory.setItem(slotIndex, buildItem(slot.getString("material", "STONE"),
                name, lore, playerName, secondsLeft, status, storedCount));

            if ("collect".equals(slot.getString("action"))) collectSlot = slotIndex;
        }
    }

    private String apply(@NotNull String text, @NotNull String playerName,
                         int seconds, @NotNull String status, int stored) {
        return text
            .replace("{player}", playerName)
            .replace("{seconds}", String.valueOf(seconds))
            .replace("{status}", status)
            .replace("{stored}", String.valueOf(stored));
    }

    private ItemStack buildItem(@NotNull String materialName, @NotNull String name,
                                @NotNull List<String> lore, @NotNull String playerName,
                                int seconds, @NotNull String status, int stored) {
        Material mat = Material.matchMaterial(materialName);
        if (mat == null) mat = Material.STONE;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MM.deserialize(apply(name, playerName, seconds, status, stored)));
        if (!lore.isEmpty()) {
            meta.lore(lore.stream()
                .map(MM::deserialize)
                .toList());
        }
        item.setItemMeta(meta);
        return item;
    }

    /** Slot index of the collect button (-1 if not configured). */
    public int getCollectSlot() { return collectSlot; }

    public @NotNull Location getGeneratorLocation() { return generatorLocation; }

    @Override
    public @NotNull Inventory getInventory() { return inventory; }
}
