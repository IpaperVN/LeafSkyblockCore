package me.ipapervn.leafskyblockcore;

import me.ipapervn.leafskyblockcore.commands.CommandLoader;
import me.ipapervn.leafskyblockcore.commands.CommandManager;
import me.ipapervn.leafskyblockcore.config.MessagesConfig;
import me.ipapervn.leafskyblockcore.config.MotdConfig;
import me.ipapervn.leafskyblockcore.config.PermissionsConfig;
import me.ipapervn.leafskyblockcore.database.DatabaseManager;
import me.ipapervn.leafskyblockcore.listeners.CropsTrackerListener;
import me.ipapervn.leafskyblockcore.listeners.GeneratorGuiListener;
import me.ipapervn.leafskyblockcore.listeners.GeneratorListener;
import me.ipapervn.leafskyblockcore.listeners.MotdListener;
import me.ipapervn.leafskyblockcore.manager.CropsTrackerManager;
import me.ipapervn.leafskyblockcore.manager.GeneratorManager;
import me.ipapervn.leafskyblockcore.placeholder.LeafPlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class LeafSkyblockCore extends JavaPlugin {

    private CommandManager commandManager;
    private CommandLoader commandLoader;
    private DatabaseManager databaseManager;
    private CropsTrackerManager cropsTrackerManager;
    private GeneratorManager generatorManager;
    private MessagesConfig messagesConfig;
    private PermissionsConfig permissionsConfig;
    private MotdConfig motdConfig;

    @Override
    public void onEnable() {
        messagesConfig = new MessagesConfig(this);
        permissionsConfig = new PermissionsConfig(this);
        motdConfig = new MotdConfig(this);
        databaseManager = new DatabaseManager(this);
        cropsTrackerManager = new CropsTrackerManager(this, databaseManager);
        generatorManager = new GeneratorManager(this);
        commandManager = new CommandManager(this);
        commandLoader = new CommandLoader(this, commandManager);
        commandLoader.loadCommands();

        Bukkit.getPluginManager().registerEvents(new CropsTrackerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GeneratorListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GeneratorGuiListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MotdListener(motdConfig), this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new LeafPlaceholderExpansion(this).register();
            getComponentLogger().info("PlaceholderAPI hooked!");
        }

        getComponentLogger().info("LeafSkyblockCore enabled!");
    }

    @Override
    public void onDisable() {
        if (generatorManager != null) generatorManager.shutdown();
        if (databaseManager != null) databaseManager.close();
        getComponentLogger().info("LeafSkyblockCore disabled!");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return commandManager.handleCommand(sender, args);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return commandManager.handleTabComplete(sender, args);
    }

    public CommandManager getCommandManager() { return commandManager; }
    public CommandLoader getCommandLoader() { return commandLoader; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public CropsTrackerManager getCropsTrackerManager() { return cropsTrackerManager; }
    public GeneratorManager getGeneratorManager() { return generatorManager; }
    public MessagesConfig getMessagesConfig() { return messagesConfig; }
    public PermissionsConfig getPermissionsConfig() { return permissionsConfig; }
    public MotdConfig getMotdConfig() { return motdConfig; }
}
