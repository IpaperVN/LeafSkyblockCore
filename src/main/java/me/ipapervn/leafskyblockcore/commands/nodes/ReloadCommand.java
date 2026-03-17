package me.ipapervn.leafskyblockcore.commands.nodes;

import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import me.ipapervn.leafskyblockcore.commands.CommandNode;
import me.ipapervn.leafskyblockcore.config.MessagesConfig;
import me.ipapervn.leafskyblockcore.config.PermissionsConfig;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ReloadCommand implements CommandNode {

    private final LeafSkyblockCore plugin;
    private final MessagesConfig messages;
    private final PermissionsConfig permissions;

    public ReloadCommand(@NotNull LeafSkyblockCore plugin) {
        this.plugin = plugin;
        this.messages = plugin.getMessagesConfig();
        this.permissions = plugin.getPermissionsConfig();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission(permissions.getPermission("reload.use"))) {
            sender.sendMessage(messages.getMessage("general.no-permission"));
            return true;
        }

        String target = args.length > 0 ? args[0].toLowerCase() : "all";

        return switch (target) {
            case "all" -> reloadAll(sender);
            case "generator" -> reloadGenerator(sender);
            default -> {
                sender.sendMessage(messages.getMessage("general.unknown-command", Map.of("command", target)));
                yield true;
            }
        };
    }

    private boolean reloadAll(@NotNull CommandSender sender) {
        plugin.getMessagesConfig().reload();
        plugin.getPermissionsConfig().reload();
        plugin.getGeneratorManager().reload();
        sender.sendMessage(messages.getMessage("reload.all"));
        return true;
    }

    private boolean reloadGenerator(@NotNull CommandSender sender) {
        plugin.getGeneratorManager().reload();
        sender.sendMessage(messages.getMessage("reload.generator"));
        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            String input = args[0].toLowerCase();
            for (String sub : List.of("all", "generator")) {
                if (sub.startsWith(input)) suggestions.add(sub);
            }
            return suggestions;
        }
        return List.of();
    }

    @Override
    public @NotNull String getName() {
        return "reload";
    }

    @Override
    public @Nullable String getPermission() {
        return permissions.getPermission("reload.use");
    }

    @Override
    public @NotNull String getDescription() {
        return "Reload plugin configs";
    }
}
