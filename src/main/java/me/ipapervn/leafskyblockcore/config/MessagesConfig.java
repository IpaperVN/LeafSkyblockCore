package me.ipapervn.leafskyblockcore.config;

import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MessagesConfig {

    private final LeafSkyblockCore plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private File file;
    private FileConfiguration config;
    private final Map<String, String> messages = new HashMap<>();

    public MessagesConfig(@NotNull LeafSkyblockCore plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            createFile();
        } else {
            config = YamlConfiguration.loadConfiguration(file);
            mergeDefaults();
        }
        loadMessages();
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
                plugin.getComponentLogger().error("Failed to create messages.yml file");
                return false;
            }
            return true;
        } catch (IOException e) {
            plugin.getComponentLogger().error("Failed to create messages.yml", e);
            return false;
        }
    }

    private void createDefaults() {
        setDefaults();
        save();
    }

    private void mergeDefaults() {
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

    private void setDefaults() {
        setDefaultsTo(config);
    }

    private void setDefaultsTo(@NotNull FileConfiguration target) {
        target.set("general.no-permission", "<red>You don't have permission to use this command!");
        target.set("general.player-only", "<red>This command is for players only!");
        target.set("general.player-not-found", "<red>Player not found!");
        target.set("general.invalid-number", "<red>Invalid number!");
        target.set("general.unknown-command", "<red>Unknown command: {command}");

        target.set("crops-tracker.your-points", "<green>Your crops points: <yellow>{points}");
        target.set("crops-tracker.player-points", "<green>{player}'s crops points: <yellow>{points}");
        target.set("crops-tracker.set-points", "<green>Set {player}'s points to <yellow>{points}");
        target.set("crops-tracker.add-points", "<green>Added <yellow>{points}</yellow> points to {player}");
        target.set("crops-tracker.reset-points", "<green>Reset {player}'s points");
        target.set("crops-tracker.reloaded", "<green>Crops tracker reloaded!");

        target.set("usage.crops-tracker-check", "<red>Usage: /lc cropstracker check <player>");
        target.set("usage.crops-tracker-set", "<red>Usage: /lc cropstracker set <player> <points>");
        target.set("usage.crops-tracker-add", "<red>Usage: /lc cropstracker add <player> <points>");
        target.set("usage.crops-tracker-reset", "<red>Usage: /lc cropstracker reset <player>");
        target.set("usage.generator-give", "<red>Usage: /lc generator give <player> [amount]");

        target.set("generator.not-own-island", "<red>You can only place generators on your own island!");
        target.set("generator.give-success", "<green>Gave <yellow>{amount}x Generator</yellow> to <yellow>{player}</yellow>!");
        target.set("generator.max-reached", "<red>You have reached the maximum number of generators on this island!");

        target.set("reload.all", "<green>Reloaded all configs!");
        target.set("reload.generator", "<green>Reloaded generator config!");
    }

    private void loadMessages() {
        messages.clear();
        loadSection("", config);
    }

    private void loadSection(String prefix, @NotNull ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
            if (section.isConfigurationSection(key)) {
                ConfigurationSection subSection = section.getConfigurationSection(key);
                if (subSection != null) {
                    loadSection(fullKey, subSection);
                }
            } else {
                messages.put(fullKey, section.getString(key, ""));
            }
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getComponentLogger().error("Failed to save messages.yml", e);
        }
    }

    @SuppressWarnings("unused")
    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
        loadMessages();
    }

    @NotNull
    public Component getMessage(@NotNull String key, @NotNull Map<String, String> placeholders) {
        String message = messages.getOrDefault(key, "<red>Message not found: " + key);
        
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        
        return miniMessage.deserialize(message);
    }

    @NotNull
    public Component getMessage(@NotNull String key) {
        return getMessage(key, Map.of());
    }
}
