package me.ipapervn.leafskyblockcore.commands;

import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import me.ipapervn.leafskyblockcore.config.MessagesConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CommandManager {

    private final LeafSkyblockCore plugin;
    private final Map<String, CommandNode> nodes = new HashMap<>();
    private final MessagesConfig messages;

    public CommandManager(@NotNull LeafSkyblockCore plugin) {
        this.plugin = plugin;
        this.messages = plugin.getMessagesConfig();
    }

    public void registerNode(@NotNull CommandNode node) {
        nodes.put(node.getName().toLowerCase(Locale.ROOT), node);
        plugin.getComponentLogger().info("Registered command node: {}", node.getName());
    }

    public void unregisterNode(@NotNull String nodeName) {
        nodes.remove(nodeName.toLowerCase(Locale.ROOT));
    }

    public boolean handleCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String nodeName = args[0].toLowerCase(Locale.ROOT);
        CommandNode node = nodes.get(nodeName);

        if (node == null) {
            sender.sendMessage(messages.getMessage("general.unknown-command", Map.of("command", nodeName)));
            return true;
        }

        String permission = node.getPermission();
        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage(messages.getMessage("general.no-permission"));
            return true;
        }

        String[] nodeArgs = Arrays.copyOfRange(args, 1, args.length);
        return node.execute(sender, nodeArgs);
    }

    @Nullable
    public List<String> handleTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            String input = args[0].toLowerCase(Locale.ROOT);
            
            for (CommandNode node : nodes.values()) {
                String permission = node.getPermission();
                if (permission == null || sender.hasPermission(permission)) {
                    if (node.getName().toLowerCase(Locale.ROOT).startsWith(input)) {
                        suggestions.add(node.getName());
                    }
                }
            }
            
            return suggestions;
        }

        if (args.length > 1) {
            String nodeName = args[0].toLowerCase(Locale.ROOT);
            CommandNode node = nodes.get(nodeName);

            if (node != null) {
                String permission = node.getPermission();
                if (permission == null || sender.hasPermission(permission)) {
                    String[] nodeArgs = Arrays.copyOfRange(args, 1, args.length);
                    return node.tabComplete(sender, nodeArgs);
                }
            }
        }

        return Collections.emptyList();
    }

    private void sendHelp(@NotNull CommandSender sender) {
        sender.sendMessage(Component.text("=== LeafSkyblockCore Commands ===", NamedTextColor.GOLD));
        
        for (CommandNode node : nodes.values()) {
            String permission = node.getPermission();
            if (permission == null || sender.hasPermission(permission)) {
                sender.sendMessage(Component.text("  /" + getCommandLabel() + " " + node.getName() + " - " + node.getDescription(), NamedTextColor.YELLOW));
            }
        }
    }

    private String getCommandLabel() {
        return "lc";
    }

    @NotNull
    public Collection<CommandNode> getNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }
}
