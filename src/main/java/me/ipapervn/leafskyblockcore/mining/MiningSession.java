package me.ipapervn.leafskyblockcore.mining;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class MiningSession {

    private final Location block;
    private final double miningTime;
    private final AtomicLong progressBits = new AtomicLong(Double.doubleToLongBits(0.0));
    private final AtomicBoolean finished = new AtomicBoolean(false);
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private volatile long lastTick;
    private volatile int lastStage = -1;
    private volatile ScheduledTask asyncTask;
    private volatile ScheduledTask mainTask;

    public MiningSession(@NotNull Location block, double miningTime) {
        this.block = block;
        this.miningTime = miningTime;
        this.lastTick = System.currentTimeMillis();
    }

    public double tick() {
        long now = System.currentTimeMillis();
        double delta = (now - lastTick) / 1000.0;
        lastTick = now;
        double increment = delta / miningTime;
        // CAS loop to atomically add increment
        double current, next;
        do {
            current = Double.longBitsToDouble(progressBits.get());
            next = current + increment;
        } while (!progressBits.compareAndSet(Double.doubleToLongBits(current), Double.doubleToLongBits(next)));
        return next;
    }

    private double getProgress() {
        return Double.longBitsToDouble(progressBits.get());
    }

    public boolean isFinished() { return finished.get(); }
    public boolean isCancelled() { return cancelled.get(); }
    public void markFinished() { finished.set(true); }
    public void markCancelled() { cancelled.set(true); }

    public int getStage() {
        return (int) Math.min(9, Math.floor(getProgress() * 10));
    }

    public boolean stageChanged() {
        int current = getStage();
        if (current != lastStage) {
            lastStage = current;
            return true;
        }
        return false;
    }

    public @NotNull Location getBlock() { return block; }
    public long getLastTick() { return lastTick; }

    public void setAsyncTask(@Nullable ScheduledTask task) { this.asyncTask = task; }
    public void setMainTask(@Nullable ScheduledTask task) { this.mainTask = task; }

    public void cancel() {
        markCancelled();
        if (asyncTask != null) asyncTask.cancel();
        if (mainTask != null) mainTask.cancel();
    }
}
