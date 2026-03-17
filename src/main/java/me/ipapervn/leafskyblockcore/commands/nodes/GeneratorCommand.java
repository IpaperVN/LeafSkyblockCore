package me.ipapervn.leafskyblockcore.commands.nodes;

import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import me.ipapervn.leafskyblockcore.commands.CommandNode;
import me.ipapervn.leafskyblockcore.config.MessagesConfig;
import me.ipapervn.leafskyblockcore.config.PermissionsConfig;
import me.ipapervn.leafskyblockcore.manager.GeneratorManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"SameReturnValue", "unused"})
public class GeneratorCommand implements CommandNode {

    private final GeneratorManager manager;
    private final MessagesConfig messages;
    private final PermissionsConfig permissions;

    public GeneratorCommand(@NotNull LeafSkyblockCore plugin) {
        this.manager = plugin.getGeneratorManager();
        this.messages = plugin.getMessagesConfig();
        this.permissions = plugin.getPermissionsConfig();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0 || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage(messages.getMessage("usage.generator-give"));
            return true;
        }
        return handleGive(sender, args);
    }

    private boolean handleGive(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission(permissions.getPermission("generator.give"))) {
            sender.sendMessage(messages.getMessage("general.no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(messages.getMessage("usage.generator-give"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(messages.getMessage("general.player-not-found"));
            return true;
        }

        int amount = 1;
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
                if (amount < 1) amount = 1;
            } catch (NumberFormatException e) {
                sender.sendMessage(messages.getMessage("general.invalid-number"));
                return true;
            }
        }

        ItemStack item = manager.createGeneratorItem();
        item.setAmount(amount);
        // Fix #9: handle inventory full — drop leftover items on ground
        Map<Integer, ItemStack> leftover = target.getInventory().addItem(item);
        if (!leftover.isEmpty()) {
            leftover.values().forEach(i -> target.getWorld().dropItemNaturally(target.getLocation(), i));
        }
        sender.sendMessage(messages.getMessage("generator.give-success", Map.of("player", target.getName(), "amount", String.valueOf(amount))));
        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1 && "give".startsWith(args[0].toLowerCase())) return List.of("give");
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) return null;
        return List.of();
    }

    @Override
    public @NotNull String getName() { return "generator"; }

    @Override
    public @NotNull String getDescription() { return "Manage generators"; }
}
