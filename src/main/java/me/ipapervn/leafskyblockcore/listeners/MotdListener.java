package me.ipapervn.leafskyblockcore.listeners;

import me.ipapervn.leafskyblockcore.config.MotdConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.jetbrains.annotations.NotNull;

public class MotdListener implements Listener {

    private final MotdConfig motdConfig;

    public MotdListener(@NotNull MotdConfig motdConfig) {
        this.motdConfig = motdConfig;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onServerListPing(ServerListPingEvent event) {
        Component motd = motdConfig.getLine1()
            .append(Component.newline())
            .append(motdConfig.getLine2());
        event.motd(motd);
    }
}
