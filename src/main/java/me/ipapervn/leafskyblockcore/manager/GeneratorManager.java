package me.ipapervn.leafskyblockcore.manager;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import me.ipapervn.leafskyblockcore.database.DatabaseManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
    private Material generatorMaterial = Material.END_PORTAL_FRAME;

    public GeneratorManager(@NotNull LeafSkyblockCore plugin) {
        this.plugin = plugin;
        this.db = plugin.getDatabaseManager();
        loadConfig();
        createTable();
        restoreGenerators();
    }

    // region Config

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
        config.set("countdown", 15);
        config.set("max-per-island", 1);
        config.set("hologram.lines.counting", "&e⏳ &6{seconds}s");
        config.set("hologram.lines.stored", "&7Stored: &e{stored}");
        config.set("item.material", "END_PORTAL_FRAME");
        config.set("item.name", "&6⚙ Generator");
        config.set("item.lore", List.of("&7Đặt xuống để kích hoạt", "&7Đếm ngược: &e{countdown}s"));
        config.set("item.custom-model-data", 0);

        config.set("output.material", "PAPER");
        config.set("output.amount", 1);
        config.set("output.name", "");
        config.set("output.lore", List.of());

        config.set("gui.title", "&8⚙ Generator");
        config.set("gui.size", 27);
        config.set("gui.status-counting", "&e⏳ Counting down");
        config.set("gui.background.material", "GRAY_STAINED_GLASS_PANE");
        config.set("gui.background.name", " ");

        config.set("gui.slots.info.slot", 11);
        config.set("gui.slots.info.material", "END_PORTAL_FRAME");
        config.set("gui.slots.info.name", "&6⚙ Generator");
        config.set("gui.slots.info.lore", List.of(
            "&7Status: {status}",
            "&7Time left: &e{seconds}s",
            "&7Placed by: &e{player}"
        ));

        config.set("gui.slots.collect.slot", 15);
        config.set("gui.slots.collect.material", "CHEST");
        config.set("gui.slots.collect.name", "&aCollect Items");
        config.set("gui.slots.collect.lore", List.of(
            "&7Stored: &e{stored} items",
            "&7Click to collect!"
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
            "saved_at INT NOT NULL DEFAULT 0, " +
            "PRIMARY KEY (world, x, y, z)"
        );
        try (Connection conn = db.getConnection();
             java.sql.Statement st = conn.createStatement()) {
            st.execute("ALTER TABLE " + TABLE + " ADD COLUMN saved_at INT NOT NULL DEFAULT 0");
        } catch (java.sql.SQLException ignored) {}
    }

    private void saveGenerator(@NotNull Location loc, @NotNull UUID placedBy, int secondsLeft) {
        String worldName = loc.getWorld().getName();
        int bx = loc.getBlockX(), by = loc.getBlockY(), bz = loc.getBlockZ();
        long now = System.currentTimeMillis() / 1000L;
        String sql = "INSERT OR REPLACE INTO " + TABLE + " (world, x, y, z, placed_by, seconds_left, finished, saved_at) VALUES (?,?,?,?,?,?,0,?)";
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

                    plugin.getServer().getGlobalRegionScheduler().run(plugin, syncTask -> {
                        World world = Bukkit.getWorld(worldName);
                        if (world == null) {
                            plugin.getComponentLogger().warn("World '{}' not found, skipping generator restore at {},{},{}", worldName, x, y, z);
                            return;
                        }
                        Location loc = new Location(world, x, y, z);
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
        startCountdownFrom(location, placedBy, config.getInt("countdown", 15));
    }

    private void startCountdownFrom(@NotNull Location location, @NotNull UUID placedBy, int seconds) {
        // If elapsed time >= countdown, gen item immediately and start fresh
        if (seconds <= 0) {
            storedItems.computeIfAbsent(location, k -> new ArrayList<>()).add(createOutputItem());
            seconds = config.getInt("countdown", 15);
        }

        String countingLine = config.getString("hologram.lines.counting", "&e⏳ &6{seconds}s");
        String storedLine = config.getString("hologram.lines.stored", "&7Stored: &e{stored}");
        Location holoLocation = location.clone().add(0.5, 1.5, 0.5);
        String holoName = holoName(location);

        DHAPI.removeHologram(holoName);
        int initStored = storedItems.getOrDefault(location, List.of()).stream().mapToInt(ItemStack::getAmount).sum();
        Hologram hologram = DHAPI.createHologram(holoName, holoLocation, List.of(
            countingLine.replace("{seconds}", String.valueOf(seconds)),
            storedLine.replace("{stored}", String.valueOf(initStored))
        ));

        int[] secondsLeft = {seconds};
        ScheduledTask task = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, t -> {
            secondsLeft[0]--;
            int stored = storedItems.getOrDefault(location, List.of()).stream().mapToInt(ItemStack::getAmount).sum();
            DHAPI.setHologramLine(hologram, 0, countingLine.replace("{seconds}", String.valueOf(secondsLeft[0])));
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
        for (ItemStack item : items) {
            Map<Integer, ItemStack> leftover = player.getInventory().addItem(item.clone());
            leftover.values().forEach(i -> player.getWorld().dropItemNaturally(player.getLocation(), i));
        }
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
            if (!name.isEmpty()) meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(name));
            if (!lore.isEmpty()) meta.lore(lore.stream()
                .map(l -> LegacyComponentSerializer.legacyAmpersand().deserialize(l))
                .toList());
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
        for (GeneratorData data : activeGenerators.values()) {
            if (data.task() != null) data.task().cancel();
            try { DHAPI.removeHologram(data.hologram().getName()); } catch (Exception ignored) {}
        }
        activeGenerators.clear();
        storedItems.clear();
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
        meta.displayName(LegacyComponentSerializer.legacyAmpersand()
            .deserialize(config.getString("item.name", "&6⚙ Generator")));
        int countdown = config.getInt("countdown", 15);
        List<Component> lore = config.getStringList("item.lore").stream()
            .map(line -> line.replace("{countdown}", String.valueOf(countdown)))
            .map(line -> (Component) LegacyComponentSerializer.legacyAmpersand().deserialize(line))
            .toList();
        meta.lore(lore);
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
    public int getCountdownConfig() { return config.getInt("countdown", 15); }

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
