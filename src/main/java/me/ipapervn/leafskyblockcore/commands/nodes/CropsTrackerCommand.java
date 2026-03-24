package me.ipapervn.leafskyblockcore.commands.nodes;

import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import me.ipapervn.leafskyblockcore.commands.CommandNode;
import me.ipapervn.leafskyblockcore.config.MessagesConfig;
import me.ipapervn.leafskyblockcore.config.PermissionsConfig;
import me.ipapervn.leafskyblockcore.manager.CropsTrackerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({"SameReturnValue", "unused"})
public class CropsTrackerCommand implements CommandNode {

    private final CropsTrackerManager manager;
    private final MessagesConfig messages;
    private final PermissionsConfig permissions;

    @SuppressWarnings("unused")
    public CropsTrackerCommand(@NotNull LeafSkyblockCore plugin) {
        this.manager = plugin.getCropsTrackerManager();
        this.messages = plugin.getMessagesConfig();
        this.permissions = plugin.getPermissionsConfig();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(messages.getMessage("general.player-only"));
                return true;
            }
            long points = manager.getPoints(player.getUniqueId());
            sender.sendMessage(messages.getMessage("crops-tracker.your-points", Map.of("points", String.valueOf(points))));
            return true;
        }

        String subCommand = args[0].toLowerCase(Locale.ROOT);

        return switch (subCommand) {
            case "check" -> handleCheck(sender, args);
            case "set" -> handleSet(sender, args);
            case "add" -> handleAdd(sender, args);
            case "reset" -> handleReset(sender, args);
            case "reload" -> handleReload(sender);
            default -> {
                sender.sendMessage(messages.getMessage("general.unknown-command", Map.of("command", subCommand)));
                yield true;
            }
        };
    }

    private boolean handleCheck(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission(permissions.getPermission("crops-tracker.check"))) {
            sender.sendMessage(messages.getMessage("general.no-permission"));
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(messages.getMessage("usage.crops-tracker-check"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(messages.getMessage("general.player-not-found"));
            return true;
        }

        long points = manager.getPoints(target.getUniqueId());
        sender.sendMessage(messages.getMessage("crops-tracker.player-points", Map.of("player", target.getName(), "points", String.valueOf(points))));
        return true;
    }

    private boolean handleSet(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission(permissions.getPermission("crops-tracker.set"))) {
            sender.sendMessage(messages.getMessage("general.no-permission"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(messages.getMessage("usage.crops-tracker-set"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(messages.getMessage("general.player-not-found"));
            return true;
        }

        try {
            long points = Long.parseLong(args[2]);
            manager.setPoints(target.getUniqueId(), points);
            sender.sendMessage(messages.getMessage("crops-tracker.set-points", Map.of("player", target.getName(), "points", String.valueOf(points))));
        } catch (NumberFormatException e) {
            sender.sendMessage(messages.getMessage("general.invalid-number"));
        }
        return true;
    }

    private boolean handleAdd(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission(permissions.getPermission("crops-tracker.add"))) {
            sender.sendMessage(messages.getMessage("general.no-permission"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(messages.getMessage("usage.crops-tracker-add"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(messages.getMessage("general.player-not-found"));
            return true;
        }

        try {
            int points = Integer.parseInt(args[2]);
            manager.addPoints(target.getUniqueId(), points);
            sender.sendMessage(messages.getMessage("crops-tracker.add-points", Map.of("player", target.getName(), "points", String.valueOf(points))));
        } catch (NumberFormatException e) {
            sender.sendMessage(messages.getMessage("general.invalid-number"));
        }
        return true;
    }

    private boolean handleReset(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission(permissions.getPermission("crops-tracker.reset"))) {
            sender.sendMessage(messages.getMessage("general.no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(messages.getMessage("usage.crops-tracker-reset"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(messages.getMessage("general.player-not-found"));
            return true;
        }

        manager.resetPoints(target.getUniqueId());
        sender.sendMessage(messages.getMessage("crops-tracker.reset-points", Map.of("player", target.getName())));
        return true;
    }

    private boolean handleReload(@NotNull CommandSender sender) {
        if (!sender.hasPermission(permissions.getPermission("crops-tracker.reload"))) {
            sender.sendMessage(messages.getMessage("general.no-permission"));
            return true;
        }

        manager.reload();
        sender.sendMessage(messages.getMessage("crops-tracker.reloaded"));
        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            String input = args[0].toLowerCase(Locale.ROOT);
            for (String sub : List.of("check", "set", "add", "reset", "reload")) {
                if (sub.startsWith(input)) {
                    suggestions.add(sub);
                }
            }
            return suggestions;
        }

        if (args.length == 2 && !args[0].equalsIgnoreCase("reload")) {
            return null;
        }

        return List.of();
    }

    @Override
    public @NotNull String getName() {
        return "cropstracker";
    }

    @Override
    public @Nullable String getPermission() {
        return permissions.getPermission("crops-tracker.use");
    }

    @Override
    public @NotNull String getDescription() {
        return "Track crops harvesting points";
    }
}
