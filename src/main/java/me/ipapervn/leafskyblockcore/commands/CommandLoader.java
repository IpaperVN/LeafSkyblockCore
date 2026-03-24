package me.ipapervn.leafskyblockcore.commands;

import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import me.ipapervn.leafskyblockcore.commands.nodes.CropsTrackerCommand;
import me.ipapervn.leafskyblockcore.commands.nodes.GeneratorCommand;
import me.ipapervn.leafskyblockcore.commands.nodes.ReloadCommand;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CommandLoader {

    private final LeafSkyblockCore plugin;
    private final CommandManager commandManager;

    public CommandLoader(@NotNull LeafSkyblockCore plugin, @NotNull CommandManager commandManager) {
        this.plugin = plugin;
        this.commandManager = commandManager;
    }

    public void loadCommands() {
        plugin.getComponentLogger().info(Component.text("Loading command nodes..."));

        // Register all command nodes here
        commandManager.registerNode(new CropsTrackerCommand(plugin));
        commandManager.registerNode(new GeneratorCommand(plugin));
        commandManager.registerNode(new ReloadCommand(plugin));

        plugin.getComponentLogger().info(Component.text("Loaded " + commandManager.getNodes().size() + " command nodes"));
    }

    @SuppressWarnings("unused")
    public void reloadCommands() {
        plugin.getComponentLogger().info(Component.text("Reloading command nodes..."));
        new ArrayList<>(commandManager.getNodes()).forEach(node -> commandManager.unregisterNode(node.getName()));
        loadCommands();
    }
}
