package me.ipapervn.leafskyblockcore.manager;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.nexomc.nexo.utils.drops.Drop;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.ipapervn.leafskyblockcore.LeafSkyblockCore;
import me.ipapervn.leafskyblockcore.config.BaseConfig;
import me.ipapervn.leafskyblockcore.mining.MiningSession;
import me.ipapervn.leafskyblockcore.mining.MiningStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MiningManager extends BaseConfig {

    private final ConcurrentHashMap<UUID, MiningSession> sessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, MiningStats> statsCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Long> swingCooldown = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, ScheduledTask> pendingCache = new ConcurrentHashMap<>();

    private final Set<String> regions = new HashSet<>();
    private final Map<String, OreData> ores = new HashMap<>();

    private static final long SWING_COOLDOWN_MS = 250L;

    public MiningManager(@NotNull LeafSkyblockCore plugin) {
        super(plugin);
        initFile("mining/config.yml");
        loadData();
        startCleanupTask();
    }

    @Override
    protected void setDefaultsTo(@NotNull FileConfiguration target) {
        target.set("regions", List.of("mining_zone_1"));
        target.set("ores.CUSTOM_COAL.breaking-power", 1);
        target.set("ores.CUSTOM_COAL.mining-time", 3);
        target.set("ores.CUSTOM_COAL.respawn-time", 30);
        target.set("ores.CUSTOM_DIAMOND.breaking-power", 3);
        target.set("ores.CUSTOM_DIAMOND.mining-time", 8);
        target.set("ores.CUSTOM_DIAMOND.respawn-time", 60);
    }

    private void loadData() {
        regions.clear();
        ores.clear();
        regions.addAll(config.getStringList("regions"));
        ConfigurationSection oreSection = config.getConfigurationSection("ores");
        if (oreSection == null) return;
        for (String key : oreSection.getKeys(false)) {
            ConfigurationSection s = oreSection.getConfigurationSection(key);
            if (s == null) continue;
            ores.put(key, new OreData(
                s.getDouble("breaking-power", 1),
                s.getDouble("mining-time", 5),
                s.getInt("respawn-time", 30)
            ));
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
        loadData();
    }

    // region Region Check

    public boolean isInMiningRegion(@NotNull Location loc) {
        if (regions.isEmpty()) return false;
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager rm = container.get(BukkitAdapter.adapt(loc.getWorld()));
        if (rm == null) return false;
        for (String regionId : regions) {
            ProtectedRegion region = rm.getRegion(regionId);
            if (region != null && region.contains(
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) return true;
        }
        return false;
    }

    public boolean isNotInMiningRegion(@NotNull Location loc) {
        return !isInMiningRegion(loc);
    }

    // endregion

    // region Ore Check

    @Nullable
    public OreData getOreData(@NotNull Location loc) {
        CustomBlockMechanic mechanic = NexoBlocks.customBlockMechanic(loc.getBlock());
        if (mechanic == null) return null;
        return ores.get(mechanic.getItemID());
    }

    @Nullable
    public String getNexoId(@NotNull Location loc) {
        CustomBlockMechanic mechanic = NexoBlocks.customBlockMechanic(loc.getBlock());
        return mechanic != null ? mechanic.getItemID() : null;
    }

    // endregion

    // region Stat Cache

    public void cacheStats(@NotNull Player player) {
        ScheduledTask pending = pendingCache.remove(player.getUniqueId());
        if (pending != null) pending.cancel();

        var task = plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, t -> {
            pendingCache.remove(player.getUniqueId());
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR) {
                statsCache.put(player.getUniqueId(), MiningStats.EMPTY);
                return;
            }
            plugin.getServer().getAsyncScheduler().runNow(plugin, at -> {
                NBTItem nbt = NBTItem.get(item);
                double bp = nbt.getDouble("MMOITEMS_BREAKING_POWER");
                double ms = getStatValue(item, "MINING_SPEED");
                double fortune = getStatValue(item, "FORTUNE");
                statsCache.put(player.getUniqueId(), new MiningStats(bp, ms <= 0 ? 1 : ms, fortune));
            });
        }, 1L);
        pendingCache.put(player.getUniqueId(), task);
    }

    private double getStatValue(@NotNull ItemStack item, @NotNull String statId) {
        try {
            NBTItem nbt = NBTItem.get(item);
            var mmoItem = MMOItems.plugin.getMMOItem(
                MMOItems.plugin.getTypes().get(nbt.getString("MMOITEMS_ITEM_TYPE")),
                nbt.getString("MMOITEMS_ITEM_ID")
            );
            if (mmoItem == null) return 0;
            ItemStat<?, ?> stat = MMOItems.plugin.getStats().get(statId);
            if (stat == null) return 0;
            var data = mmoItem.getData(stat);
            return data instanceof DoubleData d ? d.getValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public void removeStats(@NotNull UUID uuid) {
        ScheduledTask pending = pendingCache.remove(uuid);
        if (pending != null) pending.cancel();
        statsCache.remove(uuid);
    }

    @NotNull
    public MiningStats getStats(@NotNull UUID uuid) {
        return statsCache.getOrDefault(uuid, MiningStats.EMPTY);
    }

    // endregion

    // region Cooldown

    public boolean checkCooldown(@NotNull UUID uuid) {
        long now = System.currentTimeMillis();
        Long last = swingCooldown.get(uuid);
        if (last != null && now - last < SWING_COOLDOWN_MS) return false;
        swingCooldown.put(uuid, now);
        return true;
    }

    // endregion

    // region Session

    public boolean hasSession(@NotNull UUID uuid) {
        return sessions.containsKey(uuid);
    }

    public void startSession(@NotNull Player player, @NotNull Location block, @NotNull OreData ore) {
        cancelSession(player.getUniqueId());

        MiningStats stats = getStats(player.getUniqueId());
        double adjustedTime = ore.miningTime() / Math.max(0.1, stats.miningSpeed());
        MiningSession session = new MiningSession(block, adjustedTime);
        sessions.put(player.getUniqueId(), session);

        var asyncTask = plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, t -> {
            if (session.isCancelled()) { t.cancel(); return; }
            double progress = session.tick();
            if (progress >= 1.0) {
                session.markFinished();
                t.cancel();
            }
        }, 50, 50, java.util.concurrent.TimeUnit.MILLISECONDS);
        session.setAsyncTask(asyncTask);

        var mainTask = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, t -> {
            if (session.isCancelled()) { t.cancel(); return; }
            if (session.stageChanged()) sendCrackPacket(player, block, session.getStage());
            if (session.isFinished()) {
                t.cancel();
                sessions.remove(player.getUniqueId());
                sendCrackPacket(player, block, -1);
                finishMining(player, block, ore);
            }
        }, 1L, 1L);
        session.setMainTask(mainTask);
    }

    public void cancelSession(@NotNull UUID uuid) {
        MiningSession session = sessions.remove(uuid);
        if (session == null) return;
        session.cancel();
        plugin.getServer().getGlobalRegionScheduler().run(plugin, t -> {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) sendCrackPacket(player, session.getBlock(), -1);
        });
    }

    // endregion

    // region Mining Finish

    private void finishMining(@NotNull Player player, @NotNull Location loc, @NotNull OreData ore) {
        String nexoId = getNexoId(loc);
        loc.getBlock().setType(Material.BEDROCK);
        if (nexoId != null) handleDrop(player, loc, nexoId);
        long respawnTicks = ore.respawnTime() * 20L;
        plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, t -> {
            if (nexoId != null) NexoBlocks.place(nexoId, loc);
        }, respawnTicks);
    }

    private void handleDrop(@NotNull Player player, @NotNull Location loc, @NotNull String nexoId) {
        CustomBlockMechanic mechanic = NexoBlocks.customBlockMechanic(nexoId);
        if (mechanic == null) return;

        ItemStack held = player.getInventory().getItemInMainHand();
        boolean silkTouch = getStatValue(held, "SILK_TOUCH") > 0;
        if (silkTouch) {
            var builder = NexoItems.itemFromId(nexoId);
            if (builder != null) giveItem(player, loc, builder.build());
            return;
        }

        double fortune = getStats(player.getUniqueId()).fortune();
        int multiplier = calculateFortuneMultiplier(fortune);

        var breakable = mechanic.getBreakable();
        Drop drop = breakable.getDrop();
        drop.getLoots().forEach(loot -> {
            ItemStack item = loot.getItemStack();
            if (item == null) return;
            item = item.clone();
            item.setAmount(Math.min(item.getAmount() * multiplier, item.getMaxStackSize()));
            giveItem(player, loc, item);
        });
    }

    private int calculateFortuneMultiplier(double fortune) {
        if (fortune <= 0) return 1;
        int guaranteed = (int) (fortune / 100);
        double bonusChance = (fortune % 100) / 100.0;
        int bonus = Math.random() < bonusChance ? 1 : 0;
        return Math.max(1, guaranteed + bonus);
    }

    private void giveItem(@NotNull Player player, @NotNull Location loc, @NotNull ItemStack item) {
        Map<Integer, ItemStack> leftover = player.getInventory().addItem(item.clone());
        leftover.values().forEach(i -> loc.getWorld().dropItemNaturally(loc, i));
    }

    // endregion

    // region Packet

    private void sendCrackPacket(@NotNull Player player, @NotNull Location loc, int stage) {
        // stage -1 = reset, 0-9 = crack animation
        player.sendBlockDamage(loc, stage == -1 ? 0f : (stage + 1) / 10f, player.getEntityId());
    }

    // endregion

    // region Cleanup

    private void startCleanupTask() {
        plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, t -> {
            long now = System.currentTimeMillis();
            sessions.forEach((uuid, session) -> {
                Player player = plugin.getServer().getPlayer(uuid);
                boolean stale = player == null
                    || !player.isOnline()
                    || (now - session.getLastTick()) > 5000;
                if (stale) cancelSession(uuid);
            });
        }, 5, 5, java.util.concurrent.TimeUnit.SECONDS);
    }

    public void shutdown() {
        sessions.keySet().forEach(this::cancelSession);
        pendingCache.values().forEach(ScheduledTask::cancel);
        sessions.clear();
        statsCache.clear();
        swingCooldown.clear();
        pendingCache.clear();
    }

    // endregion

    public record OreData(double breakingPower, double miningTime, int respawnTime) {}
}
