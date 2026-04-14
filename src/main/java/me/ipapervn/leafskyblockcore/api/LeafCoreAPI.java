package me.ipapervn.leafskyblockcore.api;

import me.ipapervn.leafskyblockcore.config.MessagesConfig;
import me.ipapervn.leafskyblockcore.database.DatabaseManager;
import me.ipapervn.leafskyblockcore.manager.*;
import org.jetbrains.annotations.NotNull;

/**
 * API interface cho LeafSkyblockCore.
 * Module khác lấy instance qua ServicesManager:
 *
 * <pre>{@code
 * RegisteredServiceProvider<LeafCoreAPI> provider =
 *     Bukkit.getServicesManager().getRegistration(LeafCoreAPI.class);
 * if (provider != null) {
 *     LeafCoreAPI api = provider.getProvider();
 * }
 * }</pre>
 */
public interface LeafCoreAPI {

    @NotNull CropsTrackerManager getCropsTrackerManager();
    @NotNull MobCoinsManager getMobCoinsManager();
    @NotNull MiningManager getMiningManager();
    @NotNull GeneratorManager getGeneratorManager();
    @NotNull TimeFrameManager getTimeFrameManager();
    @NotNull MessagesConfig getMessagesConfig();
    @NotNull DatabaseManager getDatabaseManager();
}
