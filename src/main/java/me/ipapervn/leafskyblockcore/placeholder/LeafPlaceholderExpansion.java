package me.ipapervn.leafskyblockcore.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import me.ipapervn.leafskyblockcore.manager.CropsTrackerManager;
import me.ipapervn.leafskyblockcore.util.FormatUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class LeafPlaceholderExpansion extends PlaceholderExpansion {

    private final LeafSkyblockCore plugin;
    private final CropsTrackerManager cropsTrackerManager;

    public LeafPlaceholderExpansion(@NotNull LeafSkyblockCore plugin) {
        this.plugin = plugin;
        this.cropsTrackerManager = plugin.getCropsTrackerManager();
    }

    @Override
    public @NotNull String getIdentifier() { return "leafskyblockcore"; }

    @Override
    public @NotNull String getAuthor() { return plugin.getPluginMeta().getAuthors().toString(); }

    @Override
    public @NotNull String getVersion() { return plugin.getPluginMeta().getVersion(); }

    @Override
    public boolean persist() { return true; }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return null;

        String p = params.toLowerCase(Locale.ROOT);

        return switch (p) {
            case "crops_points"           -> String.valueOf(cropsTrackerManager.getPoints(player.getUniqueId()));
            case "crops_points_formatted" -> formatPoints(cropsTrackerManager.getPoints(player.getUniqueId()));
            case "crops_rank"             -> resolveRank(player);
            case "season"                 -> cropsTrackerManager.getSeasonManager().getCurrentSeasonDisplay();
            case "mobcoins"               -> String.valueOf(plugin.getMobCoinsManager().getCoins(player.getUniqueId()));
            case "mobcoins_formatted"     -> formatPoints(plugin.getMobCoinsManager().getCoins(player.getUniqueId()));
            default -> {
                if (p.startsWith("crops_top_") && p.endsWith("_points_formatted")) yield resolveTopPointsFormatted(p);
                if (p.startsWith("crops_top_") && p.endsWith("_name"))             yield resolveTopName(p);
                if (p.startsWith("crops_top_") && p.endsWith("_points"))           yield resolveTopPoints(p);
                yield null;
            }
        };
    }

    private String resolveRank(@NotNull OfflinePlayer player) {
        int rank = cropsTrackerManager.getPlayerRank(player.getUniqueId());
        return rank == -1 ? "N/A" : String.valueOf(rank);
    }

    private @Nullable String resolveTopName(@NotNull String params) {
        int position = parseTopPosition(params);
        if (position < 0) return "N/A";
        List<Map.Entry<UUID, Long>> top = cropsTrackerManager.getTopPlayers(position);
        if (position > top.size()) return "N/A";
        OfflinePlayer player = Bukkit.getOfflinePlayer(top.get(position - 1).getKey());
        return player.getName() != null ? player.getName() : "Unknown";
    }

    private @NotNull String resolveTopPoints(@NotNull String params) {
        int position = parseTopPosition(params);
        if (position < 0) return "0";
        List<Map.Entry<UUID, Long>> top = cropsTrackerManager.getTopPlayers(position);
        if (position > top.size()) return "0";
        return String.valueOf(top.get(position - 1).getValue());
    }

    private @NotNull String resolveTopPointsFormatted(@NotNull String params) {
        int position = parseTopPosition(params);
        if (position < 0) return "0";
        List<Map.Entry<UUID, Long>> top = cropsTrackerManager.getTopPlayers(position);
        if (position > top.size()) return "0";
        return formatPoints(top.get(position - 1).getValue());
    }

    private static @NotNull String formatPoints(long value) {
        return FormatUtil.formatNumber(value);
    }

    /** Parses position from "crops_top_X_name" or "crops_top_X_points". Returns -1 on failure. */
    private int parseTopPosition(@NotNull String params) {
        String[] parts = params.split("_");
        if (parts.length < 3) return -1;
        try {
            int pos = Integer.parseInt(parts[2]);
            return pos > 0 ? pos : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
