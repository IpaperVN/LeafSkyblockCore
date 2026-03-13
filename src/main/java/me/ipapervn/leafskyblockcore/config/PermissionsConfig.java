package me.ipapervn.leafskyblockcore.config;

import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PermissionsConfig {

    private final LeafSkyblockCore plugin;
    private File file;
    private FileConfiguration config;
    private final Map<String, String> permissions = new HashMap<>();

    public PermissionsConfig(@NotNull LeafSkyblockCore plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        file = new File(plugin.getDataFolder(), "permissions.yml");
        if (!file.exists()) {
            createFile();
        } else {
            config = YamlConfiguration.loadConfiguration(file);
        }
        loadPermissions();
    }

    private void createFile() {
        if (!createParentFolder()) {
            return;
        }
        if (!createYamlFile()) {
            return;
        }
        config = YamlConfiguration.loadConfiguration(file);
        createDefaults();
    }

    private boolean createParentFolder() {
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            plugin.getComponentLogger().error("Failed to create plugin data folder");
            return false;
        }
        return true;
    }

    private boolean createYamlFile() {
        try {
            if (!file.createNewFile()) {
                plugin.getComponentLogger().error("Failed to create permissions.yml file");
                return false;
            }
            return true;
        } catch (IOException e) {
            plugin.getComponentLogger().error("Failed to create permissions.yml", e);
            return false;
        }
    }

    private void createDefaults() {
        config.set("crops-tracker.use", "leafskyblockcore.cropstracker");
        config.set("crops-tracker.admin", "leafskyblockcore.cropstracker.admin");
        config.set("crops-tracker.check", "leafskyblockcore.cropstracker.check");
        config.set("crops-tracker.set", "leafskyblockcore.cropstracker.set");
        config.set("crops-tracker.add", "leafskyblockcore.cropstracker.add");
        config.set("crops-tracker.reset", "leafskyblockcore.cropstracker.reset");
        config.set("crops-tracker.reload", "leafskyblockcore.cropstracker.reload");
        
        config.set("command-blocker.plugins", "leafskyblockcore.command.plugins");
        config.set("command-blocker.version", "leafskyblockcore.command.version");
        config.set("command-blocker.about", "leafskyblockcore.command.about");
        config.set("command-blocker.help", "leafskyblockcore.command.help");
        config.set("command-blocker.stop", "leafskyblockcore.command.stop");
        config.set("command-blocker.reload", "leafskyblockcore.command.reload");
        
        save();
    }

    private void loadPermissions() {
        permissions.clear();
        loadSection("", config);
    }

    private void loadSection(String prefix, @NotNull org.bukkit.configuration.ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
            if (section.isConfigurationSection(key)) {
                org.bukkit.configuration.ConfigurationSection subSection = section.getConfigurationSection(key);
                if (subSection != null) {
                    loadSection(fullKey, subSection);
                }
            } else {
                permissions.put(fullKey, section.getString(key, ""));
            }
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getComponentLogger().error("Failed to save permissions.yml", e);
        }
    }

    @SuppressWarnings("unused")
    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
        loadPermissions();
    }

    /**
     * Get permission string by key.
     *
     * @param key Permission key
     * @return Permission string
     */
    @NotNull
    public String getPermission(@NotNull String key) {
        return permissions.getOrDefault(key, "leafskyblockcore.default");
    }
}
