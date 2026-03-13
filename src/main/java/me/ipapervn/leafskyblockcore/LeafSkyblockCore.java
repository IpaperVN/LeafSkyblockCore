package me.ipapervn.leafskyblockcore;

import me.ipapervn.leafskyblockcore.commands.CommandLoader;
import me.ipapervn.leafskyblockcore.commands.CommandManager;
import me.ipapervn.leafskyblockcore.config.MessagesConfig;
import me.ipapervn.leafskyblockcore.config.PermissionsConfig;
import me.ipapervn.leafskyblockcore.database.DatabaseManager;
import me.ipapervn.leafskyblockcore.listeners.CommandBlockerListener;
import me.ipapervn.leafskyblockcore.listeners.CropsTrackerListener;
import me.ipapervn.leafskyblockcore.manager.CommandBlockerManager;
import me.ipapervn.leafskyblockcore.manager.CropsTrackerManager;
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
    private CommandBlockerManager commandBlockerManager;
    private MessagesConfig messagesConfig;
    private PermissionsConfig permissionsConfig;

    @Override
    public void onEnable() {
        // Initialize configs
        messagesConfig = new MessagesConfig(this);
        permissionsConfig = new PermissionsConfig(this);
        
        // Initialize database
        databaseManager = new DatabaseManager(this);
        
        // Initialize managers
        cropsTrackerManager = new CropsTrackerManager(this, databaseManager);
        commandBlockerManager = new CommandBlockerManager(this);
        
        // Initialize command system
        commandManager = new CommandManager(this);
        commandLoader = new CommandLoader(this, commandManager);
        commandLoader.loadCommands();
        
        // Register listeners
        Bukkit.getPluginManager().registerEvents(new CropsTrackerListener(cropsTrackerManager), this);
        
        if (commandBlockerManager.isEnabled()) {
            Bukkit.getPluginManager().registerEvents(
                new CommandBlockerListener(this, commandBlockerManager.getBlockedCommands(), commandBlockerManager.isBlockAll()), this);
            getComponentLogger().info("Command Blocker enabled!");
        }
        
        // Register PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new LeafPlaceholderExpansion(this).register();
            getComponentLogger().info("PlaceholderAPI hooked!");
        }
        
        getComponentLogger().info("LeafSkyblockCore enabled!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
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

    public CommandManager getCommandManager() {
        return commandManager;
    }
    
    public CommandLoader getCommandLoader() {
        return commandLoader;
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public CropsTrackerManager getCropsTrackerManager() {
        return cropsTrackerManager;
    }
    
    public CommandBlockerManager getCommandBlockerManager() {
        return commandBlockerManager;
    }
    
    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }
    
    public PermissionsConfig getPermissionsConfig() {
        return permissionsConfig;
    }
}
