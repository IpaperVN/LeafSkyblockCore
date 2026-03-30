package me.ipapervn.leafskyblockcore.config;

import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class MotdConfig extends BaseConfig {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private Component line1 = Component.empty();
    private Component line2 = Component.empty();

    public MotdConfig(@NotNull LeafSkyblockCore plugin) {
        super(plugin);
        initFile("motd.yml");
        load();
    }

    @Override
    protected void setDefaultsTo(@NotNull FileConfiguration target) {
        target.set("enabled", true);
        target.set("line1", "<gradient:green:aqua><bold>LeafSkyblock</bold></gradient> <gray>| 1.21");
        target.set("line2", "<yellow>✦ <white>Chào mừng bạn đến với server! <yellow>✦");
    }

    private void load() {
        boolean enabled = config.getBoolean("enabled", true);
        line1 = enabled ? MM.deserialize(config.getString("line1", "")) : Component.empty();
        line2 = enabled ? MM.deserialize(config.getString("line2", "")) : Component.empty();
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
        load();
    }

    public @NotNull Component getLine1() { return line1; }
    public @NotNull Component getLine2() { return line2; }
}
