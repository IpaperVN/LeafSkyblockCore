package me.ipapervn.leafskyblockcore.manager;

import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SeasonManager {

    private final LeafSkyblockCore plugin;
    private final FileConfiguration cropsConfig;

    private List<String> seasonOrder;
    private int currentSeasonIndex;
    private long lastChangeMillis;
    private long durationMillis;

    private final File stateFile;
    private FileConfiguration stateConfig;

    public SeasonManager(@NotNull LeafSkyblockCore plugin, @NotNull FileConfiguration cropsConfig) {
        this.plugin = plugin;
        this.cropsConfig = cropsConfig;
        this.stateFile = new File(plugin.getDataFolder(), "seasons-state.yml");
        load();
    }

    private void load() {
        // Parse duration from crops-tracker config
        String durationStr = cropsConfig.getString("seasons.duration", "30m");
        durationMillis = parseDuration(durationStr) * 1000L;

        // Season order
        seasonOrder = cropsConfig.getStringList("seasons.order");
        if (seasonOrder.isEmpty()) {
            seasonOrder = List.of("SPRING", "SUMMER", "AUTUMN", "WINTER");
        }

        // Restore persisted state
        if (stateFile.exists()) {
            stateConfig = YamlConfiguration.loadConfiguration(stateFile);
            String saved = stateConfig.getString("current-season", seasonOrder.getFirst());
            currentSeasonIndex = Math.max(0, seasonOrder.indexOf(saved));
            lastChangeMillis = stateConfig.getLong("last-change", System.currentTimeMillis());
        } else {
            currentSeasonIndex = 0;
            lastChangeMillis = System.currentTimeMillis();
            saveState();
        }
    }

    /** Called before any season check — advances season if duration elapsed. */
    private void tick() {
        if (!cropsConfig.getBoolean("seasons.enabled", true)) return;
        long now = System.currentTimeMillis();
        if (now - lastChangeMillis >= durationMillis) {
            currentSeasonIndex = (currentSeasonIndex + 1) % seasonOrder.size();
            lastChangeMillis = now;
            saveState();
            plugin.getComponentLogger().info("Season changed to {}", getCurrentSeason());
        }
    }

    @NotNull
    public String getCurrentSeason() {
        tick();
        return seasonOrder.get(currentSeasonIndex);
    }

    /** Returns the display name of the current season (MiniMessage format). */
    @NotNull
    public String getCurrentSeasonDisplay() {
        tick();
        String key = seasonOrder.get(currentSeasonIndex);
        return cropsConfig.getString("seasons." + key + ".display", key);
    }

    /** Returns true if the crop is allowed to score in the current season. */
    public boolean isAllowed(@NotNull Material material) {
        if (!cropsConfig.getBoolean("seasons.enabled", true)) return true;
        tick();
        String season = seasonOrder.get(currentSeasonIndex);
        List<String> allowed = cropsConfig.getStringList("seasons." + season + ".crops");
        return allowed.stream().anyMatch(s -> s.equalsIgnoreCase(material.name()));
    }

    public void reload(@NotNull FileConfiguration newCropsConfig) {
        // Re-read duration & order from updated config, keep current season state
        String durationStr = newCropsConfig.getString("seasons.duration", "30m");
        durationMillis = parseDuration(durationStr) * 1000L;
        List<String> newOrder = newCropsConfig.getStringList("seasons.order");
        if (!newOrder.isEmpty()) seasonOrder = newOrder;
        currentSeasonIndex = Math.min(currentSeasonIndex, seasonOrder.size() - 1);
    }

    private void saveState() {
        if (stateConfig == null) stateConfig = new YamlConfiguration();
        stateConfig.set("current-season", seasonOrder.get(currentSeasonIndex));
        stateConfig.set("last-change", lastChangeMillis);
        try {
            stateConfig.save(stateFile);
        } catch (IOException e) {
            plugin.getComponentLogger().error("Failed to save seasons state", e);
        }
    }

    /** Parses duration string to seconds. Supports: 30s, 5m, 1h, plain int. */
    private static long parseDuration(@NotNull String value) {
        value = value.trim().toLowerCase(Locale.ROOT);
        try {
            char unit = value.charAt(value.length() - 1);
            if (unit == 'h' || unit == 'm' || unit == 's') {
                long amount = Long.parseLong(value.substring(0, value.length() - 1));
                return switch (unit) {
                    case 'h' -> amount * 3600;
                    case 'm' -> amount * 60;
                    default  -> amount;
                };
            }
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 1800; // default 30m
        }
    }
}
