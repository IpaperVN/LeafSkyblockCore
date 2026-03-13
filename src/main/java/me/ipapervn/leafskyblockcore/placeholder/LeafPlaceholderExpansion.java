package me.ipapervn.leafskyblockcore.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import me.ipapervn.leafskyblockcore.manager.CropsTrackerManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class LeafPlaceholderExpansion extends PlaceholderExpansion {

    private final LeafSkyblockCore plugin;
    private final CropsTrackerManager cropsTrackerManager;

    public LeafPlaceholderExpansion(@NotNull LeafSkyblockCore plugin) {
        this.plugin = plugin;
        this.cropsTrackerManager = plugin.getCropsTrackerManager();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "leafskyblockcore";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getPluginMeta().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return null;
        }

        // %leafskyblockcore_crops_points%
        if (params.equalsIgnoreCase("crops_points")) {
            return String.valueOf(cropsTrackerManager.getPoints(player.getUniqueId()));
        }

        // %leafskyblockcore_crops_rank%
        if (params.equalsIgnoreCase("crops_rank")) {
            int rank = cropsTrackerManager.getPlayerRank(player.getUniqueId());
            return rank == -1 ? "N/A" : String.valueOf(rank);
        }

        // %leafskyblockcore_crops_top_<number>_name%
        if (params.startsWith("crops_top_") && params.endsWith("_name")) {
            try {
                String[] parts = params.split("_");
                int position = Integer.parseInt(parts[2]);
                return getTopPlayerName(position);
            } catch (Exception e) {
                return "N/A";
            }
        }

        // %leafskyblockcore_crops_top_<number>_points%
        if (params.startsWith("crops_top_") && params.endsWith("_points")) {
            try {
                String[] parts = params.split("_");
                int position = Integer.parseInt(parts[2]);
                return getTopPlayerPoints(position);
            } catch (Exception e) {
                return "0";
            }
        }

        return null;
    }

    private String getTopPlayerName(int position) {
        java.util.List<java.util.Map.Entry<java.util.UUID, java.lang.Long>> top = cropsTrackerManager.getTopPlayers(position);
        if (position <= 0 || position > top.size()) {
            return "N/A";
        }
        
        java.util.UUID uuid = top.get(position - 1).getKey();
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        return player.getName() != null ? player.getName() : "Unknown";
    }

    private String getTopPlayerPoints(int position) {
        java.util.List<java.util.Map.Entry<java.util.UUID, java.lang.Long>> top = cropsTrackerManager.getTopPlayers(position);
        if (position <= 0 || position > top.size()) {
            return "0";
        }
        
        return String.valueOf(top.get(position - 1).getValue());
    }
}
