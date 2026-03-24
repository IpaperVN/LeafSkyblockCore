package me.ipapervn.leafskyblockcore.manager;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import me.ipapervn.leafskyblockcore.database.DatabaseManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GeneratorManager {

    private final LeafSkyblockCore plugin;
    private final DatabaseManager db;
    private final Map<Location, GeneratorData> activeGenerators = new HashMap<>();
    private final Map<Location, List<ItemStack>> storedItems = new HashMap<>();
    private File configFile;
    private FileConfiguration config;

    private static final String PDC_KEY = "leafskyblockcore_generator";
    private static final String TABLE = "generators";
    private static final MiniMessage MM = MiniMessage.miniMessage();
    private Material generatorMaterial = Material.END_PORTAL_FRAME;

    public GeneratorManager(@NotNull LeafSkyblockCore plugin) {
        this.plugin = plugin;
        this.db = plugin.getDatabaseManager();
        loadConfig();
        createTable();
        restoreGenerators();
    }

    // region Config

    /**
     * Parse duration string to seconds. Supports: 30s, 5m, 1h, or plain integer (seconds).
     * Examples: "30s" = 30, "5m" = 300, "1h" = 3600, "15" = 15
     */
    private int parseDuration(@NotNull String value) {
        value = value.trim().toLowerCase(java.util.Locale.ROOT);
        try {
            if (value.endsWith("h") || value.endsWith("m") || value.endsWith("s")) {
                int amount = Integer.parseInt(value.substring(0, value.length() - 1));
                if (value.endsWith("h")) return amount * 3600;
                if (value.endsWith("m")) return amount * 60;
                return amount;
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            plugin.getComponentLogger().warn("Invalid countdown value '{}', defaulting to 15s", value);
            return 15;
        }
    }

    private int getCountdownSeconds() {
        return parseDuration(config.getString("countdown", "15s"));
    }

    /** Format seconds to human-readable string. E.g. 3661 -> "1h 1m 1s", 90 -> "1m 30s", 45 -> "45s" */
    @NotNull
    private static String formatDuration(int totalSeconds) {
        int h = totalSeconds / 3600;
        int m = (totalSeconds % 3600) / 60;
        int s = totalSeconds % 60;
        StringBuilder sb = new StringBuilder();
        if (h > 0) sb.append(h).append("h ");
        if (m > 0) sb.append(m).append("m ");
        if (s > 0 || sb.isEmpty()) sb.append(s).append("s");
        return sb.toString().trim();
    }

    private void loadConfig() {
        File folder = new File(plugin.getDataFolder(), "generator");
        if (!folder.exists() && !folder.mkdirs()) {
            plugin.getComponentLogger().error("Failed to create generator folder");
            return;
        }
        configFile = new File(folder, "config.yml");
        if (!configFile.exists()) {
            createConfigFile();
        } else {
            config = YamlConfiguration.loadConfiguration(configFile);
        }
        loadGeneratorMaterial();
    }

    private void createConfigFile() {
        try {
            if (!configFile.createNewFile()) {
                plugin.getComponentLogger().error("Failed to create generator config file");
                return;
            }
            config = YamlConfiguration.loadConfiguration(configFile);
            createDefaultConfig();
        } catch (IOException e) {
            plugin.getComponentLogger().error("Failed to create generator config", e);
        }
    }

    private void createDefaultConfig() {
        config.set("countdown", "15s");
        config.set("max-per-island", 1);
        config.set("hologram.lines.counting", "<yellow>⏳ <gold>{seconds}s");
        config.set("hologram.lines.stored", "<gray>Stored: <yellow>{stored}");
        config.set("item.material", "END_PORTAL_FRAME");
        config.set("item.name", "<gold>⚙ Generator");
        config.set("item.lore", List.of("<gray>Đặt xuống để kích hoạt", "<gray>Đếm ngược: <yellow>{countdown}s"));
        config.set("item.custom-model-data", 0);

        config.set("output.material", "PAPER");
        config.set("output.amount", 1);
        config.set("output.name", "");
        config.set("output.lore", List.of());

        config.set("gui.title", "<dark_gray>⚙ Generator");
        config.set("gui.size", 27);
        config.set("gui.status-counting", "<yellow>⏳ Counting down");
        config.set("gui.background.material", "GRAY_STAINED_GLASS_PANE");
        config.set("gui.background.name", " ");

        config.set("gui.slots.info.slot", 11);
        config.set("gui.slots.info.material", "END_PORTAL_FRAME");
        config.set("gui.slots.info.name", "<gold>⚙ Generator");
        config.set("gui.slots.info.lore", List.of(
            "<gray>Status: {status}",
            "<gray>Time left: <yellow>{seconds}s",
            "<gray>Placed by: <yellow>{player}"
        ));

        config.set("gui.slots.collect.slot", 15);
        config.set("gui.slots.collect.material", "CHEST");
        config.set("gui.slots.collect.name", "<green>Collect Items");
        config.set("gui.slots.collect.lore", List.of(
            "<gray>Stored: <yellow>{stored} items",
            "<gray>Click to collect!"
        ));
        config.set("gui.slots.collect.action", "collect");

        saveConfig();
    }

    private void loadGeneratorMaterial() {
        String materialName = config.getString("item.material", "END_PORTAL_FRAME");
        Material mat = Material.matchMaterial(materialName);
        generatorMaterial = (mat != null) ? mat : Material.END_PORTAL_FRAME;
    }

    private void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getComponentLogger().error("Failed to save generator config", e);
        }
    }

    @SuppressWarnings("unused")
    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
        loadGeneratorMaterial();
    }

    // endregion

    // region Database

    private void createTable() {
        db.createTable(TABLE,
            "world TEXT NOT NULL, x INT NOT NULL, y INT NOT NULL, z INT NOT NULL, " +
            "placed_by TEXT NOT NULL, seconds_left INT NOT NULL, finished INT NOT NULL DEFAULT 0, " +
            "saved_at INT NOT NULL DEFAULT 0, stored_count INT NOT NULL DEFAULT 0, " +
            "PRIMARY KEY (world, x, y, z)"
        );
        // Migrate old tables missing columns
        tryAlterTable("saved_at INT NOT NULL DEFAULT 0");
        tryAlterTable("stored_count INT NOT NULL DEFAULT 0");
    }

    private void tryAlterTable(@NotNull String columnDef) {
        String colName = columnDef.split(" ")[0];
        if (!colName.matches("[a-zA-Z0-9_]+")) {
            plugin.getComponentLogger().error("Invalid column name '{}'", colName);
            return;
        }
        try (Connection conn = db.getConnection();
             java.sql.Statement st = conn.createStatement()) {
            st.execute("ALTER TABLE " + TABLE + " ADD COLUMN " + columnDef);
        } catch (java.sql.SQLException e) {
            if (!e.getMessage().contains("duplicate column") && !e.getMessage().contains("already exists")) {
                plugin.getComponentLogger().warn("Could not add column '{}': {}", colName, e.getMessage());
            }
        }
    }

    private void saveGenerator(@NotNull Location loc, @NotNull UUID placedBy, int secondsLeft) {
        String worldName = loc.getWorld().getName();
        int bx = loc.getBlockX(), by = loc.getBlockY(), bz = loc.getBlockZ();
        long now = System.currentTimeMillis() / 1000L;
        int storedCount = storedItems.getOrDefault(loc, List.of()).stream().mapToInt(ItemStack::getAmount).sum();
        String sql = "INSERT OR REPLACE INTO " + TABLE + " (world, x, y, z, placed_by, seconds_left, finished, saved_at, stored_count) VALUES (?,?,?,?,?,?,0,?,?)";
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, worldName);
                ps.setInt(2, bx);
                ps.setInt(3, by);
                ps.setInt(4, bz);
                ps.setString(5, placedBy.toString());
                ps.setInt(6, secondsLeft);
                ps.setLong(7, now);
                ps.setInt(8, storedCount);
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getComponentLogger().error("Failed to save generator", e);
            }
        });
    }

    private void deleteGenerator(@NotNull Location loc) {
        String worldName = loc.getWorld().getName();
        int bx = loc.getBlockX(), by = loc.getBlockY(), bz = loc.getBlockZ();
        String sql = "DELETE FROM " + TABLE + " WHERE world=? AND x=? AND y=? AND z=?";
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, worldName);
                ps.setInt(2, bx);
                ps.setInt(3, by);
                ps.setInt(4, bz);
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getComponentLogger().error("Failed to delete generator", e);
            }
        });
    }

    private void restoreGenerators() {
        long nowSeconds = System.currentTimeMillis() / 1000L;
        String sql = "SELECT * FROM " + TABLE;
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String worldName = rs.getString("world");
                    int x = rs.getInt("x"), y = rs.getInt("y"), z = rs.getInt("z");
                    UUID placedBy = UUID.fromString(rs.getString("placed_by"));
                    long savedAt = rs.getLong("saved_at");
                    long elapsed = (savedAt > 0) ? (nowSeconds - savedAt) : 0;
                    int adjustedSeconds = (int) Math.max(0, rs.getInt("seconds_left") - elapsed);
                    int storedCount = rs.getInt("stored_count");

                    plugin.getServer().getGlobalRegionScheduler().run(plugin, syncTask -> {
                        World world = Bukkit.getWorld(worldName);
                        if (world == null) {
                            plugin.getComponentLogger().warn("World '{}' not found, skipping generator restore at {},{},{}", worldName, x, y, z);
                            return;
                        }
                        Location loc = new Location(world, x, y, z);
                        // Restore stored items
                        if (storedCount > 0) {
                            ItemStack outputItem = createOutputItem();
                            outputItem.setAmount(storedCount);
                            storedItems.computeIfAbsent(loc, k -> new ArrayList<>()).add(outputItem);
                        }
                        Runnable restore = () -> startCountdownFrom(loc, placedBy, adjustedSeconds);
                        if (world.isChunkLoaded(x >> 4, z >> 4)) {
                            restore.run();
                        } else {
                            world.getChunkAtAsync(x >> 4, z >> 4).thenRun(() ->
                                plugin.getServer().getGlobalRegionScheduler().run(plugin, st -> restore.run())
                            );
                        }
                    });
                }
            } catch (SQLException e) {
                plugin.getComponentLogger().error("Failed to restore generators", e);
            }
        });
    }

    // endregion

    // region Countdown

    public void startCountdown(@NotNull Location location, @NotNull UUID placedBy) {
        startCountdownFrom(location, placedBy, getCountdownSeconds());
    }

    private void startCountdownFrom(@NotNull Location location, @NotNull UUID placedBy, int seconds) {
        // If elapsed time >= countdown, gen item immediately and start fresh
        if (seconds <= 0) {
            storedItems.computeIfAbsent(location, k -> new ArrayList<>()).add(createOutputItem());
            seconds = getCountdownSeconds();
        }

        String countingLine = config.getString("hologram.lines.counting", "&e⏳ &6{seconds}s");
        String storedLine = config.getString("hologram.lines.stored", "&7Stored: &e{stored}");
        Location holoLocation = location.clone().add(0.5, 1.5, 0.5);
        String holoName = holoName(location);

        DHAPI.removeHologram(holoName);
        int initStored = storedItems.getOrDefault(location, List.of()).stream().mapToInt(ItemStack::getAmount).sum();

        List<String> holoLines = new ArrayList<>();
        holoLines.add(countingLine.replace("{seconds}", String.valueOf(seconds)));
        holoLines.add(storedLine.replace("{stored}", String.valueOf(initStored)));
        Hologram hologram = DHAPI.createHologram(holoName, holoLocation, holoLines);

        int[] secondsLeft = {seconds};
        ScheduledTask task = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, t -> {
            secondsLeft[0]--;
            int stored = storedItems.getOrDefault(location, List.of()).stream().mapToInt(ItemStack::getAmount).sum();
            if (hologram.getPage(0) != null && !hologram.getPage(0).getLines().isEmpty())
                DHAPI.setHologramLine(hologram, 0, countingLine.replace("{seconds}", String.valueOf(secondsLeft[0])));
            if (hologram.getPage(0) != null && hologram.getPage(0).getLines().size() > 1)
                DHAPI.setHologramLine(hologram, 1, storedLine.replace("{stored}", String.valueOf(stored)));
            activeGenerators.put(location, new GeneratorData(location, placedBy, hologram, t, secondsLeft[0]));
            refreshOpenGuis(location);
            if (secondsLeft[0] <= 0) {
                t.cancel();
                storedItems.computeIfAbsent(location, k -> new ArrayList<>()).add(createOutputItem());
                startCountdown(location, placedBy);
            }
        }, 20L, 20L);

        activeGenerators.put(location, new GeneratorData(location, placedBy, hologram, task, seconds));
        saveGenerator(location, placedBy, seconds);
    }

    // endregion

    // region Public API

    public void removeGenerator(@NotNull Location location) {
        GeneratorData data = activeGenerators.remove(location);
        if (data == null) return;
        if (data.task() != null) data.task().cancel();
        DHAPI.removeHologram(data.hologram().getName());
        storedItems.remove(location);
        deleteGenerator(location);
    }

    public boolean isGenerator(@NotNull Location location) {
        return activeGenerators.containsKey(location);
    }

    @Nullable
    public GeneratorData getGeneratorData(@NotNull Location location) {
        return activeGenerators.get(location);
    }

    @NotNull
    public List<ItemStack> getStoredItems(@NotNull Location location) {
        return storedItems.getOrDefault(location, List.of());
    }

    /** Collect all stored items. Countdown continues unaffected. */
    public void collectItems(@NotNull Player player, @NotNull Location location) {
        List<ItemStack> items = storedItems.remove(location);
        if (items == null || items.isEmpty()) return;
        int totalCollected = 0;
        for (ItemStack item : items) {
            totalCollected += item.getAmount();
            Map<Integer, ItemStack> leftover = player.getInventory().addItem(item.clone());
            leftover.values().forEach(i -> player.getWorld().dropItemNaturally(player.getLocation(), i));
        }
        player.sendMessage(plugin.getMessagesConfig().getMessage("generator.collected",
            Map.of("amount", String.valueOf(totalCollected))));
    }

    @NotNull
    public ItemStack createOutputItem() {
        String matName = config.getString("output.material", "PAPER");
        Material mat = Material.matchMaterial(matName);
        if (mat == null) mat = Material.PAPER;
        int amount = Math.max(1, config.getInt("output.amount", 1));
        ItemStack item = new ItemStack(mat, amount);
        String name = config.getString("output.name", "");
        List<String> lore = config.getStringList("output.lore");
        if (!name.isEmpty() || !lore.isEmpty()) {
            ItemMeta meta = item.getItemMeta();
            if (!name.isEmpty()) meta.displayName(MM.deserialize(name));
            if (!lore.isEmpty()) meta.lore(lore.stream().map(MM::deserialize).toList());
            item.setItemMeta(meta);
        }
        return item;
    }

    public FileConfiguration getConfig() { return config; }

    public void openGui(@NotNull Player player, @NotNull Location location) {
        me.ipapervn.leafskyblockcore.gui.GeneratorGui gui =
            new me.ipapervn.leafskyblockcore.gui.GeneratorGui(plugin, player, location);
        player.openInventory(gui.getInventory());
    }

    /** Refresh all open GUIs for a specific generator location. */
    private void refreshOpenGuis(@NotNull Location location) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getOpenInventory().getTopInventory().getHolder() instanceof me.ipapervn.leafskyblockcore.gui.GeneratorGui gui
                && gui.getGeneratorLocation().equals(location)) {
                gui.refresh();
            }
        }
    }

    public void shutdown() {
        // Save current secondsLeft for all active generators before shutdown
        long now = System.currentTimeMillis() / 1000L;
        for (GeneratorData data : activeGenerators.values()) {
            if (data.task() != null) data.task().cancel();
            try { DHAPI.removeHologram(data.hologram().getName()); } catch (IllegalStateException ignored) {}
            // Synchronous save on shutdown (main thread, server is stopping)
            int storedCount = storedItems.getOrDefault(data.location(), List.of()).stream().mapToInt(ItemStack::getAmount).sum();
            String sql = "INSERT OR REPLACE INTO " + TABLE + " (world, x, y, z, placed_by, seconds_left, finished, saved_at, stored_count) VALUES (?,?,?,?,?,?,0,?,?)";
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, data.location().getWorld().getName());
                ps.setInt(2, data.location().getBlockX());
                ps.setInt(3, data.location().getBlockY());
                ps.setInt(4, data.location().getBlockZ());
                ps.setString(5, data.placedBy().toString());
                ps.setInt(6, data.secondsLeft());
                ps.setLong(7, now);
                ps.setInt(8, storedCount);
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getComponentLogger().error("Failed to save generator on shutdown", e);
            }
        }
        activeGenerators.clear();
        storedItems.clear();
    }

    /** Returns true if player does NOT have admin bypass — use as guard condition. */
    public boolean isNotAdmin(@NotNull Player player) {
        return !player.hasPermission(plugin.getPermissionsConfig().getPermission("generator.admin"));
    }

    public int countGeneratorsOnIsland(@NotNull Island island) {
        return (int) activeGenerators.keySet().stream()
            .filter(loc -> island.equals(SuperiorSkyblockAPI.getIslandAt(loc)))
            .count();
    }

    public int getMaxPerIsland() { return config.getInt("max-per-island", 1); }

    public Material getGeneratorMaterial() { return generatorMaterial; }

    @NotNull
    @SuppressWarnings("deprecation")
    public ItemStack createGeneratorItem() {
        ItemStack item = new ItemStack(generatorMaterial);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MM.deserialize(config.getString("item.name", "<gold>⚙ Generator")));
        meta.lore(config.getStringList("item.lore").stream()
            .map(line -> line.replace("{countdown}", formatDuration(getCountdownSeconds())))
            .map(MM::deserialize)
            .toList());
        int cmd = config.getInt("item.custom-model-data", 0);
        if (cmd > 0) meta.setCustomModelData(cmd);
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, PDC_KEY), PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isGeneratorItem(@NotNull ItemStack item) {
        if (item.getType() == Material.AIR) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(new NamespacedKey(plugin, PDC_KEY), PersistentDataType.BOOLEAN);
    }

    @SuppressWarnings("unused")
    public int getCountdownConfig() { return getCountdownSeconds(); }

    // endregion

    private static String holoName(@NotNull Location loc) {
        return "gen_" + loc.getWorld().getName() + "_" + loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
    }

    public record GeneratorData(
        @NotNull Location location,
        @NotNull UUID placedBy,
        @NotNull Hologram hologram,
        @Nullable ScheduledTask task,
        int secondsLeft
    ) {}
}
