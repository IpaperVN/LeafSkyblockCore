package me.ipapervn.leafskyblockcore.manager;

import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import me.ipapervn.leafskyblockcore.database.DatabaseManager;
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
import java.util.Map;
import java.util.UUID;

public class MobCoinsManager {

    private final LeafSkyblockCore plugin;
    private final DatabaseManager database;
    private final Map<UUID, Long> cache = new HashMap<>();
    private File configFile;
    private FileConfiguration config;

    public MobCoinsManager(@NotNull LeafSkyblockCore plugin, @NotNull DatabaseManager database) {
        this.plugin = plugin;
        this.database = database;
        loadConfig();
        initDatabase();
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> loadAll());
    }

    private void loadConfig() {
        File folder = plugin.getDataFolder();
        if (!folder.exists() && !folder.mkdirs()) {
            plugin.getComponentLogger().error("Failed to create plugin data folder");
            return;
        }
        configFile = new File(folder, "mobcoins.yml");
        if (!configFile.exists()) {
            createConfigFile();
        } else {
            config = YamlConfiguration.loadConfiguration(configFile);
        }
    }

    private void createConfigFile() {
        try {
            if (!configFile.createNewFile()) {
                plugin.getComponentLogger().error("Failed to create mobcoins.yml");
                return;
            }
            config = YamlConfiguration.loadConfiguration(configFile);
            config.set("display-name", "MobCoins");
            saveConfig();
        } catch (IOException e) {
            plugin.getComponentLogger().error("Failed to create mobcoins.yml", e);
        }
    }

    private void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getComponentLogger().error("Failed to save mobcoins.yml", e);
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private void initDatabase() {
        database.createTable("mobcoins",
            "uuid VARCHAR(36) PRIMARY KEY, coins BIGINT NOT NULL DEFAULT 0"
        );
    }

    private void loadAll() {
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT uuid, coins FROM mobcoins");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cache.put(UUID.fromString(rs.getString("uuid")), rs.getLong("coins"));
            }
        } catch (SQLException e) {
            plugin.getComponentLogger().error("Failed to load mobcoins data", e);
        }
    }

    public long getCoins(@NotNull UUID uuid) {
        return cache.getOrDefault(uuid, 0L);
    }

    public void setCoins(@NotNull UUID uuid, long amount) {
        cache.put(uuid, Math.max(0, amount));
        persist(uuid, Math.max(0, amount));
    }

    public void addCoins(@NotNull UUID uuid, long amount) {
        setCoins(uuid, getCoins(uuid) + amount);
    }

    public void takeCoins(@NotNull UUID uuid, long amount) {
        setCoins(uuid, Math.max(0, getCoins(uuid) - amount));
    }

    public void resetCoins(@NotNull UUID uuid) {
        cache.remove(uuid);
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            try (Connection conn = database.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM mobcoins WHERE uuid=?")) {
                ps.setString(1, uuid.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getComponentLogger().error("Failed to reset mobcoins", e);
            }
        });
    }

    private void persist(@NotNull UUID uuid, long amount) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            try (Connection conn = database.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO mobcoins (uuid, coins) VALUES (?,?) ON CONFLICT(uuid) DO UPDATE SET coins=?")) {
                ps.setString(1, uuid.toString());
                ps.setLong(2, amount);
                ps.setLong(3, amount);
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getComponentLogger().error("Failed to save mobcoins", e);
            }
        });
    }

    @NotNull
    public String getDisplayName() {
        return config.getString("display-name", "MobCoins");
    }
}
