package me.ipapervn.leafskyblockcore.config;

import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PermissionsConfig extends BaseConfig {

    private final Map<String, String> permissions = new HashMap<>();

    public PermissionsConfig(@NotNull LeafSkyblockCore plugin) {
        super(plugin);
        initFile("permissions.yml");
        loadPermissions();
    }

    @Override
    protected void setDefaultsTo(@NotNull FileConfiguration target) {
        target.set("crops-tracker.use", "leafskyblockcore.cropstracker");
        target.set("crops-tracker.admin", "leafskyblockcore.cropstracker.admin");
        target.set("crops-tracker.check", "leafskyblockcore.cropstracker.check");
        target.set("crops-tracker.set", "leafskyblockcore.cropstracker.set");
        target.set("crops-tracker.add", "leafskyblockcore.cropstracker.add");
        target.set("crops-tracker.reset", "leafskyblockcore.cropstracker.reset");
        target.set("crops-tracker.reload", "leafskyblockcore.cropstracker.reload");
        target.set("generator.use", "leafskyblockcore.generator.use");
        target.set("generator.break", "leafskyblockcore.generator.break");
        target.set("generator.admin", "leafskyblockcore.generator.admin");
        target.set("generator.give", "leafskyblockcore.generator.give");
        target.set("reload.use", "leafskyblockcore.reload");
        target.set("mobcoins.give", "leafskyblockcore.mobcoins.give");
        target.set("mobcoins.take", "leafskyblockcore.mobcoins.take");
        target.set("mobcoins.reset", "leafskyblockcore.mobcoins.reset");
    }

    private void loadPermissions() {
        permissions.clear();
        loadSection("", config, permissions);
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
