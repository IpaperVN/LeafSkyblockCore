package me.ipapervn.leafskyblockcore.config;

import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public abstract class BaseConfig {

    protected final LeafSkyblockCore plugin;
    protected File file;
    protected FileConfiguration config;

    protected BaseConfig(@NotNull LeafSkyblockCore plugin) {
        this.plugin = plugin;
    }

    protected void initFile(@NotNull String fileName) {
        File dataFolder = plugin.getDataFolder();
        file = new File(dataFolder, fileName);
        // Guard against path traversal (e.g. "../../etc/passwd")
        Path resolved = file.toPath().normalize();
        Path base = dataFolder.toPath().normalize();
        if (!resolved.startsWith(base)) {
            plugin.getComponentLogger().error("Blocked path traversal attempt for file: {}", fileName);
            return;
        }
        if (!file.exists()) {
            createFile();
        } else {
            config = YamlConfiguration.loadConfiguration(file);
            mergeDefaults();
        }
    }

    private void createFile() {
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            plugin.getComponentLogger().error("Failed to create plugin data folder");
            return;
        }
        try {
            if (!file.createNewFile()) {
                plugin.getComponentLogger().error("Failed to create {} file", file.getName());
                return;
            }
        } catch (IOException e) {
            plugin.getComponentLogger().error("Failed to create {}", file.getName(), e);
            return;
        }
        config = YamlConfiguration.loadConfiguration(file);
        setDefaultsTo(config);
        save();
    }

    protected void mergeDefaults() {
        YamlConfiguration defaults = new YamlConfiguration();
        setDefaultsTo(defaults);
        boolean changed = false;
        for (String key : defaults.getKeys(true)) {
            if (!defaults.isConfigurationSection(key) && !config.contains(key)) {
                config.set(key, defaults.get(key));
                changed = true;
            }
        }
        if (changed) save();
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getComponentLogger().error("Failed to save {}", file.getName(), e);
        }
    }

    protected void loadSection(@NotNull String prefix, @NotNull ConfigurationSection section,
                               @NotNull Map<String, String> target) {
        for (String key : section.getKeys(false)) {
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
            if (section.isConfigurationSection(key)) {
                ConfigurationSection sub = section.getConfigurationSection(key);
                if (sub != null) loadSection(fullKey, sub, target);
            } else {
                target.put(fullKey, section.getString(key, ""));
            }
        }
    }

    protected abstract void setDefaultsTo(@NotNull FileConfiguration target);
}
