package me.ipapervn.leafskyblockcore;

import me.ipapervn.leafskyblockcore.api.LeafCoreAPI;
import me.ipapervn.leafskyblockcore.commands.CommandLoader;
import me.ipapervn.leafskyblockcore.commands.CommandManager;
import me.ipapervn.leafskyblockcore.commands.MobCoinsCommand;
import me.ipapervn.leafskyblockcore.config.MessagesConfig;
import me.ipapervn.leafskyblockcore.config.MotdConfig;
import me.ipapervn.leafskyblockcore.config.PermissionsConfig;
import me.ipapervn.leafskyblockcore.database.DatabaseManager;
import me.ipapervn.leafskyblockcore.listeners.CropsTrackerListener;
import me.ipapervn.leafskyblockcore.listeners.GeneratorGuiListener;
import me.ipapervn.leafskyblockcore.listeners.GeneratorListener;
import me.ipapervn.leafskyblockcore.listeners.MiningListener;
import me.ipapervn.leafskyblockcore.listeners.MotdListener;
import me.ipapervn.leafskyblockcore.manager.CropsTrackerManager;
import me.ipapervn.leafskyblockcore.manager.GeneratorManager;
import me.ipapervn.leafskyblockcore.manager.MiningManager;
import me.ipapervn.leafskyblockcore.manager.MobCoinsManager;
import me.ipapervn.leafskyblockcore.manager.TimeFrameManager;
import me.ipapervn.leafskyblockcore.placeholder.LeafPlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class LeafSkyblockCore extends JavaPlugin implements LeafCoreAPI {

    private DatabaseManager databaseManager;
    private CommandManager commandManager;
    private CommandLoader commandLoader;
    private CropsTrackerManager cropsTrackerManager;
    private GeneratorManager generatorManager;
    private MessagesConfig messagesConfig;
    private PermissionsConfig permissionsConfig;
    private MotdConfig motdConfig;
    private MobCoinsManager mobCoinsManager;
    private TimeFrameManager timeFrameManager;
    private MiningManager miningManager;

    @Override
    public void onEnable() {
        messagesConfig = new MessagesConfig(this);
        permissionsConfig = new PermissionsConfig(this);
        motdConfig = new MotdConfig(this);
        databaseManager = new DatabaseManager(this);
        mobCoinsManager = new MobCoinsManager(this, databaseManager);
        timeFrameManager = new TimeFrameManager(this);
        miningManager = new MiningManager(this);
        cropsTrackerManager = new CropsTrackerManager(this, databaseManager);
        generatorManager = new GeneratorManager(this);
        commandManager = new CommandManager(this);
        commandLoader = new CommandLoader(this, commandManager);
        commandLoader.loadCommands();

        Bukkit.getPluginManager().registerEvents(new CropsTrackerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MiningListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GeneratorListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GeneratorGuiListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MotdListener(motdConfig), this);

        MobCoinsCommand mobCoinsCmd = new MobCoinsCommand(this);
        var cmd = getCommand("mcoins");
        if (cmd != null) {
            cmd.setExecutor(mobCoinsCmd);
            cmd.setTabCompleter(mobCoinsCmd);
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new LeafPlaceholderExpansion(this).register();
            getComponentLogger().info("PlaceholderAPI hooked!");
        }

        getComponentLogger().info("LeafSkyblockCore enabled!");
        getServer().getServicesManager().register(LeafCoreAPI.class, this, this, ServicePriority.Normal);
        getComponentLogger().info("LeafCoreAPI registered!");
    }

    @Override
    public void onDisable() {
        if (generatorManager != null) generatorManager.shutdown();
        if (miningManager != null) miningManager.shutdown();
        if (timeFrameManager != null) timeFrameManager.shutdown();
        if (databaseManager != null) databaseManager.close();
        getServer().getServicesManager().unregisterAll(this);
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

    @Override public @NotNull DatabaseManager getDatabaseManager() { return databaseManager; }
    @Override public @NotNull CropsTrackerManager getCropsTrackerManager() { return cropsTrackerManager; }
    @Override public @NotNull GeneratorManager getGeneratorManager() { return generatorManager; }
    @Override public @NotNull MessagesConfig getMessagesConfig() { return messagesConfig; }
    public @NotNull PermissionsConfig getPermissionsConfig() { return permissionsConfig; }
    public @NotNull MotdConfig getMotdConfig() { return motdConfig; }
    public @NotNull CommandManager getCommandManager() { return commandManager; }
    public @NotNull CommandLoader getCommandLoader() { return commandLoader; }
    @Override public @NotNull MobCoinsManager getMobCoinsManager() { return mobCoinsManager; }
    @Override public @NotNull TimeFrameManager getTimeFrameManager() { return timeFrameManager; }
    @Override public @NotNull MiningManager getMiningManager() { return miningManager; }
}
