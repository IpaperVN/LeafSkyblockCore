package me.ipapervn.leafskyblockcore.listeners;

import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import me.ipapervn.leafskyblockcore.config.MessagesConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommandBlockerListener implements Listener {

    private final MessagesConfig messages;
    private final Map<String, String> blockedCommands;
    private final boolean blockAll;

    public CommandBlockerListener(@NotNull LeafSkyblockCore plugin, @NotNull Map<String, String> blockedCommands, boolean blockAll) {
        this.messages = plugin.getMessagesConfig();
        this.blockedCommands = blockedCommands;
        this.blockAll = blockAll;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage().toLowerCase();
        String command = message.split(" ")[0].substring(1);
        String permission = blockedCommands.get(command);

        if (blockAll) {
            if (permission == null) {
                event.setCancelled(true);
                player.sendMessage(messages.getMessage("command-blocker.blocked", Map.of("command", command)));
            } else if (!player.hasPermission(permission)) {
                event.setCancelled(true);
                player.sendMessage(messages.getMessage("command-blocker.blocked", Map.of("command", command)));
            }
        } else {
            if (permission != null && !player.hasPermission(permission)) {
                event.setCancelled(true);
                player.sendMessage(messages.getMessage("command-blocker.blocked", Map.of("command", command)));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandSend(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();
        java.util.Collection<String> commands = event.getCommands();
        
        if (blockAll) {
            Set<String> allowed = new HashSet<>();
            
            for (String cmd : blockedCommands.keySet()) {
                String permission = blockedCommands.get(cmd);
                if (player.hasPermission(permission)) {
                    allowed.add(cmd);
                }
            }
            
            commands.clear();
            commands.addAll(allowed);
        } else {
            Set<String> toRemove = new HashSet<>();
            
            for (String cmd : commands) {
                String permission = blockedCommands.get(cmd.toLowerCase());
                if (permission != null && !player.hasPermission(permission)) {
                    toRemove.add(cmd);
                }
            }
            
            commands.removeAll(toRemove);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTabComplete(TabCompleteEvent event) {
        if (!(event.getSender() instanceof Player player)) {
            return;
        }

        String buffer = event.getBuffer().toLowerCase();
        if (!buffer.startsWith("/")) {
            return;
        }

        List<String> completions = event.getCompletions();
        
        if (blockAll) {
            List<String> allowed = new ArrayList<>();
            
            for (String suggestion : completions) {
                String cmd = suggestion.split(" ")[0].toLowerCase();
                String permission = blockedCommands.get(cmd);
                
                if (permission != null && player.hasPermission(permission)) {
                    allowed.add(suggestion);
                }
            }
            
            completions.clear();
            event.setCompletions(allowed);
        } else {
            List<String> filtered = new ArrayList<>();
            for (String suggestion : completions) {
                String cmd = suggestion.split(" ")[0].toLowerCase();
                String permission = blockedCommands.get(cmd);
                
                if (permission == null || player.hasPermission(permission)) {
                    filtered.add(suggestion);
                }
            }
            event.setCompletions(filtered);
        }
    }
}
