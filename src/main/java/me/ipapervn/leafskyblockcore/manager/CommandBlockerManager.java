package me.ipapervn.leafskyblockcore.manager;

import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CommandBlockerManager {

    private final LeafSkyblockCore plugin;
    private final Map<String, String> blockedCommands = new HashMap<>();
    private File configFile;
    private FileConfiguration config;

    public CommandBlockerManager(@NotNull LeafSkyblockCore plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        File folder = new File(plugin.getDataFolder(), "command-blocker");
        if (!folder.exists() && !folder.mkdirs()) {
            plugin.getComponentLogger().error("Failed to create command-blocker folder");
            return;
        }

        configFile = new File(folder, "config.yml");
        if (!configFile.exists()) {
            createConfigFile();
        } else {
            config = YamlConfiguration.loadConfiguration(configFile);
        }

        loadBlockedCommands();
    }

    private void createConfigFile() {
        try {
            if (!configFile.createNewFile()) {
                plugin.getComponentLogger().error("Failed to create command-blocker config file");
                return;
            }
            config = YamlConfiguration.loadConfiguration(configFile);
            createDefaultConfig();
        } catch (IOException e) {
            plugin.getComponentLogger().error("Failed to create command-blocker config", e);
        }
    }

    private void createDefaultConfig() {
        config.set("enabled", true);
        config.set("block-all-tab-complete", true);
        
        config.set("commands.lc", "leafskyblockcore.use");
        config.set("commands.leafskyblockcore", "leafskyblockcore.use");
        config.set("commands.leaf", "leafskyblockcore.use");
        config.set("commands.leafcore", "leafskyblockcore.use");
        config.set("commands.plugins", "leafskyblockcore.command.plugins");
        config.set("commands.pl", "leafskyblockcore.command.plugins");
        config.set("commands.version", "leafskyblockcore.command.version");
        config.set("commands.ver", "leafskyblockcore.command.version");
        config.set("commands.about", "leafskyblockcore.command.about");
        config.set("commands.help", "leafskyblockcore.command.help");
        config.set("commands.?", "leafskyblockcore.command.help");
        config.set("commands.stop", "leafskyblockcore.command.stop");
        config.set("commands.reload", "leafskyblockcore.command.reload");
        config.set("commands.rl", "leafskyblockcore.command.reload");
        
        saveConfig();
    }

    private void loadBlockedCommands() {
        blockedCommands.clear();
        
        if (!config.getBoolean("enabled", true)) {
            return;
        }

        ConfigurationSection commandsSection = config.getConfigurationSection("commands");
        if (commandsSection != null) {
            for (String command : commandsSection.getKeys(false)) {
                String permission = commandsSection.getString(command);
                if (permission != null) {
                    blockedCommands.put(command.toLowerCase(), permission);
                }
            }
        }
    }

    private void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getComponentLogger().error("Failed to save command-blocker config", e);
        }
    }

    @SuppressWarnings("unused")
    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
        loadBlockedCommands();
    }

    @NotNull
    public Map<String, String> getBlockedCommands() {
        return new HashMap<>(blockedCommands);
    }

    public boolean isEnabled() {
        return config.getBoolean("enabled", true);
    }

    public boolean isBlockAll() {
        return config.getBoolean("block-all-tab-complete", true);
    }
}
