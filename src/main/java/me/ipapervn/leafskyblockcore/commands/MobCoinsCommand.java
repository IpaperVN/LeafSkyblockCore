package me.ipapervn.leafskyblockcore.commands;

import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import me.ipapervn.leafskyblockcore.config.MessagesConfig;
import me.ipapervn.leafskyblockcore.config.PermissionsConfig;
import me.ipapervn.leafskyblockcore.manager.MobCoinsManager;
import me.ipapervn.leafskyblockcore.util.FormatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MobCoinsCommand implements CommandExecutor, TabCompleter {

    private final MobCoinsManager manager;
    private final MessagesConfig messages;
    private final PermissionsConfig permissions;

    public MobCoinsCommand(@NotNull LeafSkyblockCore plugin) {
        this.manager = plugin.getMobCoinsManager();
        this.messages = plugin.getMessagesConfig();
        this.permissions = plugin.getPermissionsConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(messages.getMessage("general.player-only"));
                return true;
            }
            long coins = manager.getCoins(player.getUniqueId());
            sender.sendMessage(messages.getMessage("mobcoins.your-coins",
                Map.of("coins", String.valueOf(coins),
                       "coins_formatted", formatCoins(coins),
                       "name", manager.getDisplayName())));
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "give", "add" -> handleModify(sender, args, true);
            case "take"        -> handleModify(sender, args, false);
            case "reset"       -> handleReset(sender, args);
            default -> sender.sendMessage(messages.getMessage("general.unknown-command", Map.of("command", sub)));
        }
        return true;
    }

    private void handleModify(@NotNull CommandSender sender, @NotNull String[] args, boolean give) {
        if (checkPermAndUsage(sender, give ? "mobcoins.give" : "mobcoins.take",
                              args.length < 3, give ? "usage.mobcoins-give" : "usage.mobcoins-take")) return;
        Player target = resolvePlayer(sender, args[1]);
        if (target == null) return;
        try {
            long amount = Long.parseLong(args[2]);
            if (amount <= 0) { sender.sendMessage(messages.getMessage("general.invalid-number")); return; }
            if (give) manager.addCoins(target.getUniqueId(), amount);
            else      manager.takeCoins(target.getUniqueId(), amount);
            sender.sendMessage(messages.getMessage(give ? "mobcoins.give-success" : "mobcoins.take-success",
                Map.of("player", target.getName(), "amount", String.valueOf(amount), "name", manager.getDisplayName())));
            target.sendMessage(messages.getMessage(give ? "mobcoins.received" : "mobcoins.taken",
                Map.of("amount", String.valueOf(amount), "name", manager.getDisplayName(),
                       "coins", String.valueOf(manager.getCoins(target.getUniqueId())))));
        } catch (NumberFormatException e) {
            sender.sendMessage(messages.getMessage("general.invalid-number"));
        }
    }

    private void handleReset(@NotNull CommandSender sender, @NotNull String[] args) {
        if (checkPermAndUsage(sender, "mobcoins.reset", args.length < 2, "usage.mobcoins-reset")) return;
        Player target = resolvePlayer(sender, args[1]);
        if (target == null) return;
        manager.resetCoins(target.getUniqueId());
        sender.sendMessage(messages.getMessage("mobcoins.reset-success",
            Map.of("player", target.getName(), "name", manager.getDisplayName())));
    }

    /** Returns true (and sends message) if sender lacks permission or usage condition is met. */
    private boolean checkPermAndUsage(@NotNull CommandSender sender, @NotNull String permKey,
                                      boolean usageFail, @NotNull String usageKey) {
        if (!sender.hasPermission(permissions.getPermission(permKey))) {
            sender.sendMessage(messages.getMessage("general.no-permission"));
            return true;
        }
        if (usageFail) {
            sender.sendMessage(messages.getMessage(usageKey));
            return true;
        }
        return false;
    }

    /** Returns the online player or sends player-not-found and returns null. */
    private @Nullable Player resolvePlayer(@NotNull CommandSender sender, @NotNull String name) {
        Player target = Bukkit.getPlayer(name);
        if (target == null) sender.sendMessage(messages.getMessage("general.player-not-found"));
        return target;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<String> subs = new ArrayList<>();
            String input = args[0].toLowerCase(Locale.ROOT);
            for (String s : List.of("give", "take", "add", "reset")) {
                if (s.startsWith(input)) subs.add(s);
            }
            return subs;
        }
        if (args.length == 2) return null;
        return List.of();
    }

    public static @NotNull String formatCoins(long value) {
        return FormatUtil.formatNumber(value);
    }
}
