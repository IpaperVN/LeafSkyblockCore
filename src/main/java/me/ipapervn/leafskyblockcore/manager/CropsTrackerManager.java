package me.ipapervn.leafskyblockcore.manager;

import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import me.ipapervn.leafskyblockcore.database.DatabaseManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CropsTrackerManager {

    private final LeafSkyblockCore plugin;
    private final DatabaseManager database;
    private final Map<Material, Integer> cropsPoints = new HashMap<>();
    private final Map<UUID, Long> cachedPoints = new HashMap<>();
    private File configFile;
    private FileConfiguration config;
    private SeasonManager seasonManager;

    public CropsTrackerManager(@NotNull LeafSkyblockCore plugin, @NotNull DatabaseManager database) {
        this.plugin = plugin;
        this.database = database;
        loadConfig();
        initializeDatabase();
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> loadAllPoints());
    }

    private void initializeDatabase() {
        database.createTable("crops_tracker",
            "uuid VARCHAR(36) PRIMARY KEY, " +
            "points BIGINT NOT NULL DEFAULT 0"
        );
    }

    private void loadConfig() {
        File folder = new File(plugin.getDataFolder(), "crops-tracker");
        if (!folder.exists() && !folder.mkdirs()) {
            plugin.getComponentLogger().error("Failed to create crops-tracker folder");
            return;
        }

        configFile = new File(folder, "config.yml");
        if (!configFile.exists()) {
            createConfigFile();
        } else {
            config = YamlConfiguration.loadConfiguration(configFile);
        }

        loadCropsPoints();
        seasonManager = new SeasonManager(plugin, config);
    }

    private void createConfigFile() {
        try {
            if (!configFile.createNewFile()) {
                plugin.getComponentLogger().error("Failed to create crops-tracker config file");
                return;
            }
            config = YamlConfiguration.loadConfiguration(configFile);
            createDefaultConfig();
        } catch (IOException e) {
            plugin.getComponentLogger().error("Failed to create crops-tracker config", e);
        }
    }

    private void createDefaultConfig() {
        config.set("crops.WHEAT", 1);
        config.set("crops.CARROTS", 1);
        config.set("crops.POTATOES", 1);
        config.set("crops.BEETROOTS", 1);
        config.set("crops.NETHER_WART", 2);
        config.set("crops.SWEET_BERRY_BUSH", 1);
        config.set("crops.COCOA", 2);
        config.set("crops.MELON", 1);
        config.set("crops.PUMPKIN", 1);

        config.set("seasons.enabled", true);
        config.set("seasons.duration", "30m");
        config.set("seasons.order", List.of("SPRING", "SUMMER", "AUTUMN", "WINTER"));
        config.set("seasons.SPRING.crops", List.of("WHEAT", "CARROTS", "POTATOES"));
        config.set("seasons.SUMMER.crops", List.of("MELON", "PUMPKIN", "SWEET_BERRY_BUSH"));
        config.set("seasons.AUTUMN.crops", List.of("BEETROOTS", "NETHER_WART", "COCOA"));
        config.set("seasons.WINTER.crops", List.of());
        saveConfig();
    }

    private void loadCropsPoints() {
        cropsPoints.clear();
        ConfigurationSection cropsSection = config.getConfigurationSection("crops");
        if (cropsSection != null) {
            for (String key : cropsSection.getKeys(false)) {
                try {
                    Material material = Material.valueOf(key.toUpperCase(Locale.ROOT));
                    int points = cropsSection.getInt(key);
                    cropsPoints.put(material, points);
                } catch (IllegalArgumentException e) {
                    plugin.getComponentLogger().warn("Invalid material in crops config: {}", key);
                }
            }
        }
    }

    private void loadAllPoints() {
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT uuid, points FROM crops_tracker ORDER BY points DESC");
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                long points = rs.getLong("points");
                cachedPoints.put(uuid, points);
            }
        } catch (SQLException e) {
            plugin.getComponentLogger().error("Failed to load crops tracker data", e);
        }
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getComponentLogger().error("Failed to save crops-tracker config", e);
        }
    }

    public void reload() {
        loadConfig();
        if (seasonManager != null) seasonManager.reload(config);
        cachedPoints.clear();
        loadAllPoints();
    }

    public SeasonManager getSeasonManager() { return seasonManager; }

    public int getCropPoints(@NotNull Material material) {
        return cropsPoints.getOrDefault(material, 0);
    }

    public boolean isCropTracked(@NotNull Material material) {
        return cropsPoints.containsKey(material);
    }

    public void addPoints(@NotNull UUID uuid, int points) {
        long newPoints = cachedPoints.getOrDefault(uuid, 0L) + points;
        cachedPoints.put(uuid, newPoints);
        
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            try (Connection conn = database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO crops_tracker (uuid, points) VALUES (?, ?) " +
                     "ON CONFLICT(uuid) DO UPDATE SET points = points + ?")) {
                
                stmt.setString(1, uuid.toString());
                stmt.setLong(2, points);
                stmt.setLong(3, points);
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getComponentLogger().error("Failed to add points", e);
            }
        });
    }

    public long getPoints(@NotNull UUID uuid) {
        return cachedPoints.getOrDefault(uuid, 0L);
    }

    public void setPoints(@NotNull UUID uuid, long points) {
        cachedPoints.put(uuid, points);
        
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            try (Connection conn = database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO crops_tracker (uuid, points) VALUES (?, ?) " +
                     "ON CONFLICT(uuid) DO UPDATE SET points = ?")) {
                
                stmt.setString(1, uuid.toString());
                stmt.setLong(2, points);
                stmt.setLong(3, points);
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getComponentLogger().error("Failed to set points", e);
            }
        });
    }

    public void resetPoints(@NotNull UUID uuid) {
        cachedPoints.remove(uuid);
        
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            try (Connection conn = database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM crops_tracker WHERE uuid = ?")) {
                
                stmt.setString(1, uuid.toString());
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getComponentLogger().error("Failed to reset points", e);
            }
        });
    }

    @NotNull
    public java.util.List<java.util.Map.Entry<java.util.UUID, java.lang.Long>> getTopPlayers(int limit) {
        return cachedPoints.entrySet().stream()
            .sorted(java.util.Map.Entry.<java.util.UUID, java.lang.Long>comparingByValue().reversed())
            .limit(limit)
            .toList();
    }

    public int getPlayerRank(@NotNull UUID uuid) {
        java.util.List<java.util.Map.Entry<java.util.UUID, java.lang.Long>> sorted = cachedPoints.entrySet().stream()
            .sorted(java.util.Map.Entry.<java.util.UUID, java.lang.Long>comparingByValue().reversed())
            .toList();
        
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getKey().equals(uuid)) {
                return i + 1;
            }
        }
        return -1;
    }
}
