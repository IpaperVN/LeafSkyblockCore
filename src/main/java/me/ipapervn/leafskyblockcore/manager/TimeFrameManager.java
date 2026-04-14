package me.ipapervn.leafskyblockcore.manager;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import me.ipapervn.leafskyblockcore.config.BaseConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class TimeFrameManager extends BaseConfig {

    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final MiniMessage MM = MiniMessage.miniMessage();

    private record TimeSlot(int startHour, int startMinute, int endHour, int endMinute) {
        boolean isActive(int hour, int minute) {
            int now  = hour * 60 + minute;
            int from = startHour * 60 + startMinute;
            int to   = endHour * 60 + endMinute;
            if (from <= to) return now >= from && now < to;
            return now >= from || now < to;
        }
    }

    private final List<TimeSlot> slots = new ArrayList<>();
    private ScheduledTask task;
    private boolean lastActive = false;

    public TimeFrameManager(@NotNull LeafSkyblockCore plugin) {
        super(plugin);
        initFile("timeframe.yml");
        parseSlots();
        lastActive = isActive();
        startScheduler();
    }

    @Override
    protected void setDefaultsTo(@NotNull FileConfiguration target) {
        target.set("enabled", true);
        target.set("slots", List.of("20:00-22:00", "08:00-10:00"));
        target.set("broadcast-message", "<gold>⏰ <yellow>Khung giờ đặc biệt đã bắt đầu!");
        target.set("broadcast-end-message", "<gray>⏰ <yellow>Khung giờ đặc biệt đã kết thúc!");
        target.set("placeholder-active", "<green>Đang diễn ra");
        target.set("placeholder-inactive", "<red>Không hoạt động");
    }

    private void parseSlots() {
        slots.clear();
        if (!config.getBoolean("enabled", true)) return;
        for (String entry : config.getStringList("slots")) {
            String[] parts = entry.split("-");
            if (parts.length != 2) {
                plugin.getComponentLogger().warn("Invalid timeframe slot '{}', expected HH:mm-HH:mm", entry);
                continue;
            }
            try {
                int[] from = parseTime(parts[0]);
                int[] to   = parseTime(parts[1]);
                slots.add(new TimeSlot(from[0], from[1], to[0], to[1]));
            } catch (Exception e) {
                plugin.getComponentLogger().warn("Invalid timeframe slot '{}': {}", entry, e.getMessage());
            }
        }
    }

    private static int[] parseTime(@NotNull String hhmm) {
        String[] p = hhmm.trim().split(":");
        if (p.length != 2) throw new IllegalArgumentException("Expected HH:mm");
        return new int[]{ Integer.parseInt(p[0]), Integer.parseInt(p[1]) };
    }

    public boolean isActive() {
        if (!config.getBoolean("enabled", true)) return false;
        ZonedDateTime now = ZonedDateTime.now(VIETNAM_ZONE);
        int hour = now.getHour(), minute = now.getMinute();
        for (TimeSlot slot : slots) {
            if (slot.isActive(hour, minute)) return true;
        }
        return false;
    }

    public String getStatusDisplay() {
        String key = isActive() ? "placeholder-active" : "placeholder-inactive";
        return config.getString(key, "");
    }

    private void startScheduler() {
        task = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, t -> {
            boolean current = isActive();
            if (current && !lastActive) broadcast("broadcast-message");
            if (!current && lastActive) broadcast("broadcast-end-message");
            lastActive = current;
        }, 1200L, 1200L);
    }

    private void broadcast(@NotNull String key) {
        String msg = config.getString(key, "");
        if (!msg.isEmpty()) Bukkit.broadcast(MM.deserialize(msg));
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
        parseSlots();
        lastActive = isActive();
    }

    public void shutdown() {
        if (task != null) task.cancel();
    }
}
